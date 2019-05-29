package com.plethub.paaro.core.appservice.services;

import com.plethub.paaro.core.appservice.apiresponsemodel.PaymentReferenceResponseModel;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.PaymentMethod;
import com.plethub.paaro.core.appservice.enums.TransactionStatus;
import com.plethub.paaro.core.appservice.repository.WalletFundingTransactionRepository;
import com.plethub.paaro.core.infrastructure.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.infrastructure.utils.PaymentReferenceGenerationStrategy;
import com.plethub.paaro.core.models.*;
import com.plethub.paaro.core.appservice.repository.PaaroBankAccountRepository;
import com.plethub.paaro.core.appservice.repository.PaymentReferenceRepository;
import com.plethub.paaro.core.infrastructure.utils.GeneralUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Transactional(rollbackFor = Exception.class)
public class PaymentReferenceService {

    @Autowired
    private WalletService walletService;

    @Autowired
    private PaymentReferenceRepository paymentReferenceRepository;

    @Autowired
    private PaaroBankAccountRepository paaroBankAccountRepository;

    @Autowired
    private PaymentReferenceGenerationStrategy paymentReferenceGenerationStrategy;

    @Value("${initial.payment-reference:00001}")
    private String initialReference;

    @Autowired
    private WalletFundingTransactionRepository walletFundingTransactionRepository;

    @Transactional
    public PaymentReferenceResponseModel generatePaymentRefrenceForUser(String currency, Long paaroBankId) throws Exception {

        if (StringUtils.isEmpty(currency)) {
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Currency type must be provided");
        }

        Wallet wallet = walletService.getCurrentLoggedInUserWalletByCurrencyType(currency);

        if (wallet == null) {
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Wallet not found for user");
        }

        PaaroBankAccount paaroBankAccount = paaroBankAccountRepository.findOne(paaroBankId);

        if (paaroBankAccount == null) {
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Bank account not found");
        }

        if (paaroBankAccount.getCurrency() == null || paaroBankAccount.getCurrency().getType() == null || !paaroBankAccount.getCurrency().getType().trim().equalsIgnoreCase(currency.trim())) {
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request. Currency does not match");
        }


        PaymentReference paymentReference =  generatePaymentCodeForUser(wallet, paaroBankAccount);

        //logWalletFundingFromDeposit(paymentReference);

        PaymentReferenceResponseModel paymentReferenceResponseModel = new PaymentReferenceResponseModel();
        paymentReferenceResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        paymentReferenceResponseModel.setMessage("Payment reference generated");
        paymentReferenceResponseModel.setPaymentReference(paymentReference);


        return paymentReferenceResponseModel;
    }

//    private void logWalletFundingFromDeposit(PaymentReference paymentReference) {
//        WalletFundingTransaction walletFundingTransaction = new WalletFundingTransaction();
//
//        walletFundingTransaction.setInitiatedDate(new Date());
//        walletFundingTransaction.setLastUpdatedDate(new Date());
//        walletFundingTransaction.setNarration("Bank payment from user");
//        walletFundingTransaction.setCurrency(paymentReference.getPaaroBankAccount().getCurrency());
//        walletFundingTransaction.setPaymentMethod(PaymentMethod.BANK_DEPOSIT);
//        walletFundingTransaction.setPaymentReference(paymentReference);
//        walletFundingTransaction.setTransactionStatus(TransactionStatus.AWAITING_BANK_PAYMENT_VERIFICATION);
//        walletFundingTransaction.setPaymentReferenceId(paymentReference.getId());
//        walletFundingTransaction.setPaaroReferenceId(paymentReference.getPaymentReferenceNumber());
//        walletFundingTransaction.setWallet(paymentReference.getWallet());
//        walletFundingTransaction.setManagedUser(paymentReference.getWallet().getManagedUser());
//        walletFundingTransaction.setActualAmount(BigDecimal.valueOf(0));
//        walletFundingTransaction.setExchangeRate(0d);
//
//        walletFundingTransactionRepository.save(walletFundingTransaction);
//
//    }


    public PaymentReferenceResponseModel getPaymentReferenceByRefNumber(String referenceNumber) {


        PaymentReference paymentReference =  paymentReferenceRepository.findTopByPaymentReferenceNumber(referenceNumber);

        if (paymentReference == null) {
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Payment reference not found");
        }

        PaymentReferenceResponseModel paymentReferenceResponseModel = new PaymentReferenceResponseModel();
        paymentReferenceResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        paymentReferenceResponseModel.setMessage("Payment reference found");
        paymentReferenceResponseModel.setPaymentReference(paymentReference);

        return paymentReferenceResponseModel;
    }

    public PaymentReferenceResponseModel getPaymentReferenceByWallet(String currency, Integer pageNo, Integer pageSize) {

        pageNo = (pageNo == null) ? 0 : pageNo;
        pageSize = (pageSize == null || pageSize < 1) ? 15 : pageSize;

        Page<PaymentReference> paymentReferencePage =  paymentReferenceRepository.findTopByWallet_Currency_Type(currency, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        if (paymentReferencePage == null || CollectionUtils.isEmpty(paymentReferencePage.getContent())) {
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Payment reference not found for currency");
        }

        PaymentReferenceResponseModel paymentReferenceResponseModel = new PaymentReferenceResponseModel();
        paymentReferenceResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        paymentReferenceResponseModel.setMessage("Payment reference found");
        paymentReferenceResponseModel.setPaymentReferencePage(paymentReferencePage);

        return paymentReferenceResponseModel;
    }

    public PaymentReferenceResponseModel getPaymentReferenceByLogggedInUserWallet(String currency, Integer pageNo, Integer pageSize) {

        pageNo = (pageNo == null) ? 0 : pageNo;
        pageSize = (pageSize == null || pageSize < 1) ? 15 : pageSize;

        Wallet wallet = walletService.getCurrentLoggedInUserWalletByCurrencyType(currency);

        if (wallet == null) {
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Wallet not found for");
        }

        Page<PaymentReference> paymentReferencePage =  paymentReferenceRepository.findTopByWallet_Id(wallet.getId(), new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        if (paymentReferencePage == null || CollectionUtils.isEmpty(paymentReferencePage.getContent())) {
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Payment reference not found for currency");
        }

        PaymentReferenceResponseModel paymentReferenceResponseModel = new PaymentReferenceResponseModel();
        paymentReferenceResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        paymentReferenceResponseModel.setMessage("Payment reference found");
        paymentReferenceResponseModel.setPaymentReferencePage(paymentReferencePage);

        return paymentReferenceResponseModel;
    }

    public PaymentReferenceResponseModel getPaymentReferenceForLogggedInUser(Integer pageNo, Integer pageSize) {

        pageNo = (pageNo == null) ? 0 : pageNo;
        pageSize = (pageSize == null || pageSize < 1) ? 15 : pageSize;

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null || userDetailsTokenEnvelope.getManagedUser().getId() == null)
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "No currently logged in user found");

        Page<PaymentReference> paymentReferencePage =  paymentReferenceRepository.findAllByWallet_ManagedUser_Id(userDetailsTokenEnvelope.getManagedUser().getId(), new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        if (paymentReferencePage == null || CollectionUtils.isEmpty(paymentReferencePage.getContent())) {
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Payment reference not found for user");
        }

        PaymentReferenceResponseModel paymentReferenceResponseModel = new PaymentReferenceResponseModel();
        paymentReferenceResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        paymentReferenceResponseModel.setMessage("Payment reference found");
        paymentReferenceResponseModel.setPaymentReferencePage(paymentReferencePage);

        return paymentReferenceResponseModel;
    }

    public PaymentReferenceResponseModel getPendingPaymentReferenceForLogggedInUser(Integer pageNo, Integer pageSize) {

        pageNo = (pageNo == null) ? 0 : pageNo;
        pageSize = (pageSize == null || pageSize < 1) ? 15 : pageSize;

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null || userDetailsTokenEnvelope.getManagedUser().getId() == null)
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "No currently logged in user found");

        Page<PaymentReference> paymentReferencePage =  paymentReferenceRepository.findAllByWallet_ManagedUser_IdAndAndUserDepositedIsFalseAndUserCanDeposit(userDetailsTokenEnvelope.getManagedUser().getId(), new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        if (paymentReferencePage == null || CollectionUtils.isEmpty(paymentReferencePage.getContent())) {
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Payment reference not found for user");
        }

        PaymentReferenceResponseModel paymentReferenceResponseModel = new PaymentReferenceResponseModel();
        paymentReferenceResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        paymentReferenceResponseModel.setMessage("Payment reference found");
        paymentReferenceResponseModel.setPaymentReferencePage(paymentReferencePage);

        return paymentReferenceResponseModel;
    }

    private PaymentReference generatePaymentCodeForUser(Wallet wallet, PaaroBankAccount paaroBankAccount) throws Exception {

        PaymentReference paymentReference = new PaymentReference();
        paymentReference.setDateGenerated(new Date());
        paymentReference.setWallet(wallet);
        paymentReference.setPaaroBankAccount(paaroBankAccount);
        paymentReference.setPaymentReferenceNumber(generatePaymentReference());
//        paymentReference.setPaymentReferenceNumber(String.format("PAARO-%s", GeneralUtil.generateRandomValueForRequest()));
        paymentReference.setPaymentVerified(false);

        paymentReference = paymentReferenceRepository.save(paymentReference);

        return paymentReference;

    }

    private String generatePaymentReference() throws Exception {

        PaymentReference paymentReference = paymentReferenceRepository.findTopByIdIsNotNullOrderByIdDesc();
        if (paymentReference == null || paymentReference.getPaymentReferenceNumber().trim().length() != 5) return  initialReference;

        return paymentReferenceGenerationStrategy.incrementPaymentRefrenceId(paymentReference.getPaymentReferenceNumber().trim());

    }

    public PaymentReferenceResponseModel cancelBid(Long bidId) {
        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null || userDetailsTokenEnvelope.getManagedUser().getId() == null)
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "No currently logged in user found");

        PaymentReference paymentReference = paymentReferenceRepository.findTopByIdAndWallet_ManagedUser_Id(bidId, userDetailsTokenEnvelope.getManagedUser().getId());
        if (paymentReference == null)
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Payment not found for user");

        if (paymentReference.isUserDeposited())
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "User already deposited");

        WalletFundingTransaction fundingTransaction = walletFundingTransactionRepository.findTopByPaymentReference_Id(bidId);
        if (fundingTransaction == null)
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "No funding transaction found");

        if (fundingTransaction.getTransactionStatus() != TransactionStatus.AWAITING_BANK_PAYMENT_VERIFICATION)
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "No funding transaction found");

        paymentReference.setUserCanDeposit(false);
        paymentReferenceRepository.save(paymentReference);
        fundingTransaction.setTransactionStatus(TransactionStatus.BID_CANCELLED);
        walletFundingTransactionRepository.save(fundingTransaction);

        return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Wallet credit successfullly cancelled");
    }

}
