package com.plethub.paaro.core.appservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plethub.paaro.core.appservice.apirequestmodel.BankDepositRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.BankDepositResponseModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.BankDepositValidationResponse;
import com.plethub.paaro.core.appservice.enums.*;
import com.plethub.paaro.core.appservice.repository.WalletFundingTransactionRepository;
import com.plethub.paaro.core.appservice.repository.WalletRepository;
import com.plethub.paaro.core.configuration.service.ConfigurationService;
import com.plethub.paaro.core.infrastructure.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.models.*;
import com.plethub.paaro.core.appservice.repository.BankDepositRepository;
import com.plethub.paaro.core.appservice.repository.PaymentReferenceRepository;
import com.plethub.paaro.core.request.apimodel.RequestResponse;
import com.plethub.paaro.core.request.service.RequestService;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import com.plethub.paaro.core.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class BankDepositService {


    @Autowired
    private WalletService walletService;

    @Autowired
    private PaymentReferenceRepository paymentReferenceRepository;

    @Autowired
    private BankDepositRepository bankDepositRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private WalletFundingTransactionRepository fundingTransactionRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WalletFundingTransactionRepository walletFundingTransactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletEmailService walletEmailService;

    public BankDepositResponseModel getAllLoggedInUserBankDepositsByCurrency (String currency) {

        if (StringUtils.isEmpty(currency)) {
            return BankDepositResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Currency cannot be blank");
        }
        Wallet wallet = walletService.getCurrentLoggedInUserWalletByCurrencyType(currency);

        if (wallet == null) {
            return BankDepositResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Wallet not found for user");
        }

        List<BankDeposit> bankDeposits = bankDepositRepository.findAllByPaymentReference_Wallet_Id(wallet.getId());

        BankDepositResponseModel bankDepositResponseModel = new BankDepositResponseModel();

        bankDepositResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        bankDepositResponseModel.setBankDeposits(bankDeposits);

        return bankDepositResponseModel;

    }

    public BankDepositResponseModel getAllLoggedInUserBankDeposits(Integer pageNo, Integer pageSize) {

        pageNo = (pageNo == null) ? 0 : pageNo;
        pageSize = (pageSize == null || pageSize < 1) ? 15 : pageSize;

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null || userDetailsTokenEnvelope.getManagedUser().getId() == null)
            return BankDepositResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "No currently logged in user found");


        Page<BankDeposit> bankDeposits = bankDepositRepository.findAllByPaymentReference_Wallet_ManagedUser_Id(userDetailsTokenEnvelope.getManagedUser().getId(), new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        BankDepositResponseModel bankDepositResponseModel = new BankDepositResponseModel();

        bankDepositResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        bankDepositResponseModel.setBankDepositPage(bankDeposits);

        return bankDepositResponseModel;

    }

    public BankDepositResponseModel getAllBankDeposits(Pageable pageable) {


        Page<BankDeposit> bankDeposits = bankDepositRepository.findAll(pageable);

        BankDepositResponseModel bankDepositResponseModel = new BankDepositResponseModel();

        bankDepositResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        bankDepositResponseModel.setBankDepositPage(bankDeposits);

        return bankDepositResponseModel;

    }

    public BankDeposit getDetails(Long id){
        return bankDepositRepository.getOne(id);
    }

    public BankDepositResponseModel getAllLoggedInUserPendingBankDeposits(Integer pageNo, Integer pageSize) {

        pageNo = (pageNo == null) ? 0 : pageNo;
        pageSize = (pageSize == null || pageSize < 1) ? 15 : pageSize;

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null || userDetailsTokenEnvelope.getManagedUser().getId() == null)
            return BankDepositResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "No currently logged in user found");


        Page<BankDeposit> bankDeposits = bankDepositRepository.findAllByPaymentReference_Wallet_ManagedUser_IdAndTransactionStatus(userDetailsTokenEnvelope.getManagedUser().getId(), DepositStatus.PAYMENT_AWAITING_VERIFICATION, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        BankDepositResponseModel bankDepositResponseModel = new BankDepositResponseModel();

        bankDepositResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        bankDepositResponseModel.setBankDepositPage(bankDeposits);

        return bankDepositResponseModel;

    }

    @Transactional
    public BankDepositResponseModel logBankDepositForUser(BankDepositRequestModel bankDepositRequestModel, HttpServletRequest servletRequest) throws JsonProcessingException {


        BankDepositValidationResponse bankDepositValidationResponse = validateBankDepositRequestModel(bankDepositRequestModel);

        if (bankDepositValidationResponse == null) {
            return BankDepositResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request");
        }

        if ( bankDepositValidationResponse.getBankDepositResponseModel() != null) {
            return bankDepositValidationResponse.getBankDepositResponseModel();
        }

        BankDeposit bankDeposit = new BankDeposit();
        bankDeposit.setAmount(bankDepositRequestModel.getAmount());
        bankDeposit.setLoggedDate(new Date());
        bankDeposit.setPaymentReference(bankDepositValidationResponse.getPaymentReference());
        bankDeposit.setTellerNumber(bankDepositRequestModel.getTellerNumber());
        bankDeposit.setTransactionStatus(DepositStatus.PAYMENT_AWAITING_VERIFICATION);
        bankDeposit.getPaymentReference().setUserDeposited(true);
        bankDeposit.getPaymentReference().setUserCanDeposit(false);
        bankDepositRepository.save(bankDeposit);

        logWalletFundingFromDeposit(bankDeposit);

        BankDepositResponseModel bankDepositResponseModel = new BankDepositResponseModel();

        bankDepositResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        bankDepositResponseModel.setBankDeposit(bankDeposit);

        auditLogService.saveAudit(null,objectMapper.writeValueAsString(bankDeposit), Module.BANK_DEPOSIT, servletRequest, "Customer logged bank deposit");

        return bankDepositResponseModel;

    }

    private void logWalletFundingFromDeposit(BankDeposit bankDeposit) {
        WalletFundingTransaction walletFundingTransaction = new WalletFundingTransaction();

        walletFundingTransaction.setInitiatedDate(new Date());
        walletFundingTransaction.setLastUpdatedDate(new Date());
        walletFundingTransaction.setNarration("Fund wallet via Bank payment");
        walletFundingTransaction.setCurrency(bankDeposit.getPaymentReference().getPaaroBankAccount().getCurrency());
        walletFundingTransaction.setPaymentMethod(PaymentMethod.BANK_DEPOSIT);
        walletFundingTransaction.setBankDeposit(bankDeposit);
        walletFundingTransaction.setTransactionStatus(TransactionStatus.AWAITING_BANK_PAYMENT_VERIFICATION);
        walletFundingTransaction.setPaymentReferenceId(bankDeposit.getPaymentReference().getId());
        walletFundingTransaction.setPaaroReferenceId(bankDeposit.getPaymentReference().getPaymentReferenceNumber());
        walletFundingTransaction.setThirdPartyReferenceId(bankDeposit.getTellerNumber());
        walletFundingTransaction.setWallet(bankDeposit.getPaymentReference().getWallet());
        walletFundingTransaction.setManagedUser(bankDeposit.getPaymentReference().getWallet().getManagedUser());
        walletFundingTransaction.setActualAmount(bankDeposit.getAmount());
        walletFundingTransaction.setExchangeRate(0d);

        walletFundingTransactionRepository.save(walletFundingTransaction);

    }

    private BankDepositValidationResponse validateBankDepositRequestModel(BankDepositRequestModel bankDepositRequestModel) {

        BankDepositResponseModel bankDepositResponseModel = null;

        if (bankDepositRequestModel == null) {
            bankDepositResponseModel = BankDepositResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request object");
            return new BankDepositValidationResponse(null, null, bankDepositResponseModel);
        }

        if (bankDepositRequestModel.getPaymentReferenceId() == null) {
            bankDepositResponseModel = BankDepositResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Payment refrence Id not passed");
            return new BankDepositValidationResponse(null, null, bankDepositResponseModel);
        }

        if (bankDepositRequestModel.getAmount().compareTo(BigDecimal.valueOf(0)) <= 0 ) {
            bankDepositResponseModel = BankDepositResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Amount must be greater than 0");
            return new BankDepositValidationResponse(null, null, bankDepositResponseModel);
        }

        if (StringUtils.isEmpty(bankDepositRequestModel.getCurrencyType())) {
            bankDepositResponseModel = BankDepositResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "No currency type passed");
            return new BankDepositValidationResponse(null, null, bankDepositResponseModel);
        }

        if (StringUtils.isEmpty(bankDepositRequestModel.getTellerNumber())) {
            bankDepositResponseModel = BankDepositResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "No teller number passed");
            return new BankDepositValidationResponse(null, null, bankDepositResponseModel);
        }

        Wallet wallet = walletService.getCurrentLoggedInUserWalletByCurrencyType(bankDepositRequestModel.getCurrencyType());

        if (wallet == null) {
            bankDepositResponseModel = BankDepositResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Wallet not found for user");
            return new BankDepositValidationResponse(null, null, bankDepositResponseModel);
        }

        PaymentReference paymentReference = paymentReferenceRepository.findOne(bankDepositRequestModel.getPaymentReferenceId());

        if (paymentReference == null) {
            bankDepositResponseModel = BankDepositResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Payment reference not found");
            return new BankDepositValidationResponse(null, null, bankDepositResponseModel);
        }

        if (paymentReference.getWallet() == null || paymentReference.getWallet().getId() != wallet.getId()) {
            bankDepositResponseModel = BankDepositResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Wallet doesn't match the specified payment reference");
            return new BankDepositValidationResponse(null, null, bankDepositResponseModel);
        }

        if (!paymentReference.isUserCanDeposit()) {
            bankDepositResponseModel = BankDepositResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "User cannot longer make deposit");
            return new BankDepositValidationResponse(null, null, bankDepositResponseModel);
        }

        BankDeposit bankDeposit = bankDepositRepository.findTopByPaymentReference_Id(bankDepositRequestModel.getPaymentReferenceId());

        if (bankDeposit != null) {
            bankDepositResponseModel = BankDepositResponseModel.fromNarration(ApiResponseCode.ALREADY_EXIST, "Specified payment reference already logged");
            return new BankDepositValidationResponse(null, null, bankDepositResponseModel);
        }

        return new BankDepositValidationResponse(wallet, paymentReference, bankDepositResponseModel);


    }

    public BankDepositResponseModel approveBankDepositForUser(Long id, HttpServletRequest servletRequest) throws JsonProcessingException {

        BankDeposit bankDeposit = bankDepositRepository.findOne(id);
        if (bankDeposit == null || bankDeposit.getPaymentReference() == null) return BankDepositResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Null request object");

        if (bankDeposit.getTransactionStatus() != DepositStatus.PAYMENT_AWAITING_VERIFICATION)
            return BankDepositResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Deposit not awaiting verification");

        ManagedUser user = userService.getCurrentLoggedInUser();

        if (requestService.isRequestPending(Action.VERIFY_DEPOSIT, bankDeposit.getId())) return BankDepositResponseModel.fromNarration(ApiResponseCode.PENDING_VERIFICATION, "Item is pending verification");
        if (configurationService.isMakerCheckerEnabledForAction(Action.UPDATE_BANK)) return createBankDepositApprovalRequestAndAudit(bankDeposit, user, servletRequest);

        if (user == null) return BankDepositResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "No current user found");

        return updateBankDepositAndAudit(bankDeposit, user, servletRequest);
    }

    private BankDepositResponseModel createBankDepositApprovalRequestAndAudit(BankDeposit bankDeposit, ManagedUser user, HttpServletRequest servletRequest) {

        bankDeposit.setVerificationInitiatedBy(user);

        bankDeposit = bankDepositRepository.save(bankDeposit);

        auditLogService.saveAudit(null, null, Module.BANK_DEPOSIT, servletRequest, "User initiated bank deposit approval", bankDeposit.getId());

        return BankDepositResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Successfully logged deposit");
    }

    private BankDepositResponseModel updateBankDepositAndAudit(BankDeposit bankDeposit, ManagedUser user, HttpServletRequest servletRequest) throws JsonProcessingException {

        bankDeposit.setTransactionStatus(DepositStatus.PAYMENT_VERIFIED);
        bankDeposit.setVerificationOrDeclinedDate(new Date());
        bankDeposit.setVerfifiedBy(user);
        Wallet wallet = bankDeposit.getPaymentReference().getWallet();
        if (wallet == null) return BankDepositResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Wallet not found");
        if (bankDeposit.getPaymentReference() == null) return BankDepositResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Payment refernce not found");

        WalletFundingTransaction fundingTransaction = fundingTransactionRepository.findTopByBankDeposit_Id(bankDeposit.getId());
        if (fundingTransaction == null || fundingTransaction.getTransactionStatus() != TransactionStatus.AWAITING_BANK_PAYMENT_VERIFICATION) return BankDepositResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Funding transaction not found");
        fundingTransaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        walletFundingTransactionRepository.save(fundingTransaction);
        bankDeposit = bankDepositRepository.save(bankDeposit);

        BigDecimal newAvailableBalance = wallet.getAvailableAccountBalance().add(bankDeposit.getAmount());
        BigDecimal newLedgerBalance = wallet.getLedgerAccountBalance().add(bankDeposit.getAmount());
        wallet.setLedgerAccountBalance(newLedgerBalance);
        wallet.setAvailableAccountBalance(newAvailableBalance);
        walletRepository.save(wallet);

        walletEmailService.sendWalletFundingApprovalEmail(bankDeposit.getPaymentReference());
        walletEmailService.sendWalletFundingEmail(wallet, fundingTransaction.getActualAmount());

        auditLogService.saveAudit(null, objectMapper.writeValueAsString(bankDeposit), Module.BANK_DEPOSIT, servletRequest, "User verified bank deposit", bankDeposit.getId());

        return BankDepositResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Successfully verified deposit");
    }

    public RequestResponse verifyBankDeposit(Request request, HttpServletRequest servletRequest) throws JsonProcessingException {
        if (request == null || request.getEntityId() == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request");

        BankDeposit bankDeposit = bankDepositRepository.findOne(request.getEntityId());
        Wallet wallet = bankDeposit.getPaymentReference().getWallet();
        if (wallet == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Wallet not found");
        if (bankDeposit == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Deposit not found");
        if (bankDeposit.getTransactionStatus() != DepositStatus.PAYMENT_AWAITING_VERIFICATION) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Request not awaiting verification");

        ManagedUser user = userService.getCurrentLoggedInUser();
        if (user == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "No currently logged in user found");

        BankDeposit oldRecord = bankDeposit;

        bankDeposit.setTransactionStatus(DepositStatus.PAYMENT_VERIFIED);
        bankDeposit.setVerfifiedBy(user);
        bankDeposit.setVerificationOrDeclinedDate(new Date());
        WalletFundingTransaction fundingTransaction = fundingTransactionRepository.findTopByBankDeposit_Id(bankDeposit.getId());
        if (fundingTransaction == null || fundingTransaction.getTransactionStatus() != TransactionStatus.AWAITING_BANK_PAYMENT_VERIFICATION) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Funding transaction not found");
        fundingTransaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        walletFundingTransactionRepository.save(fundingTransaction);
        bankDeposit = bankDepositRepository.save(bankDeposit);

        BigDecimal newAvailableBalance = wallet.getAvailableAccountBalance().add(bankDeposit.getAmount());
        BigDecimal newLedgerBalance = wallet.getLedgerAccountBalance().add(bankDeposit.getAmount());
        wallet.setLedgerAccountBalance(newLedgerBalance);
        wallet.setAvailableAccountBalance(newAvailableBalance);
        walletRepository.save(wallet);

        walletEmailService.sendWalletFundingApprovalEmail(bankDeposit.getPaymentReference());
        walletEmailService.sendWalletFundingEmail(wallet, fundingTransaction.getActualAmount());
        auditLogService.saveAudit(objectMapper.writeValueAsString(oldRecord), objectMapper.writeValueAsString(bankDeposit), Module.FUNDING, servletRequest, "User verified bank deposit approval request", bankDeposit.getId());

        return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "Verification successful");
    }

    public BankDepositResponseModel searchForBankDepositModelWithAnd(BankDepositRequestModel requestModel) {

        if (requestModel == null) return BankDepositResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request");

        Integer pageNo = requestModel.getPageNo() == null || requestModel.getPageNo() < 0 ? 0 : requestModel.getPageNo();
        Integer pageSize = requestModel.getPageSize() == null || requestModel.getPageSize() < 1 ? 15 : requestModel.getPageSize();

        Page bankDepositPage = bankDepositRepository.findAll((Specification<WalletTransferTransaction>) (root, criteriaQuery, criteriaBuilder) -> {


            return criteriaBuilder.and(composePredicateForDepositSearch(requestModel, criteriaBuilder, root).toArray(new Predicate[0]));

        }, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        BankDepositResponseModel bankDepositResponseModel = new BankDepositResponseModel();
        bankDepositResponseModel.setBankDepositPage(bankDepositPage);
        bankDepositResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);

        return bankDepositResponseModel;
    }

    public BankDepositResponseModel searchForBankDepositModelPageapbleWithAnd(Pageable pageable, BankDepositRequestModel requestModel) {

        if (requestModel == null) return BankDepositResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request");

        Page bankDepositPage = bankDepositRepository.findAll((Specification<WalletTransferTransaction>) (root, criteriaQuery, criteriaBuilder) -> {


            return criteriaBuilder.and(composePredicateForDepositSearch(requestModel, criteriaBuilder, root).toArray(new Predicate[0]));

        }, new PageRequest(pageable.getOffset()/pageable.getPageSize(), pageable.getPageSize(), Sort.Direction.DESC, "id"));

        BankDepositResponseModel bankDepositResponseModel = new BankDepositResponseModel();
        bankDepositResponseModel.setBankDepositPage(bankDepositPage);
        bankDepositResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);

        return bankDepositResponseModel;
    }

    public BankDepositResponseModel searchForBankDepositModelWithOr(BankDepositRequestModel requestModel) {

        if (requestModel == null) return BankDepositResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request");

        Integer pageNo = requestModel.getPageNo() == null || requestModel.getPageNo() < 0 ? 0 : requestModel.getPageNo();
        Integer pageSize = requestModel.getPageSize() == null || requestModel.getPageSize() < 1 ? 15 : requestModel.getPageSize();

        Page bankDepositPage = bankDepositRepository.findAll((Specification<WalletTransferTransaction>) (root, criteriaQuery, criteriaBuilder) -> {


            return criteriaBuilder.or(composePredicateForDepositSearch(requestModel, criteriaBuilder, root).toArray(new Predicate[0]));

        }, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        BankDepositResponseModel bankDepositResponseModel = new BankDepositResponseModel();
        bankDepositResponseModel.setBankDepositPage(bankDepositPage);
        bankDepositResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);

        return bankDepositResponseModel;
    }

    private List<Predicate> composePredicateForDepositSearch(BankDepositRequestModel requestModel, CriteriaBuilder criteriaBuilder, Root root) {
        List<Predicate> searchPredicate = new ArrayList<>();

        if (requestModel.getDepositStatus() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("transactionStatus")), requestModel.getDepositStatus()));
        if (!StringUtils.isEmpty(requestModel.getPaymentReferenceNo()))
            searchPredicate.add(criteriaBuilder.equal((root.get("paymentReference").get("paymentReferenceNumber")), requestModel.getPaymentReferenceNo()));
        if (!StringUtils.isEmpty(requestModel.getCurrencyType()))
            searchPredicate.add(criteriaBuilder.equal((root.get("paymentReference").get("wallet").get("currency").get("type")), requestModel.getCurrencyType()));
        if (!StringUtils.isEmpty(requestModel.getUserEmail()))
            searchPredicate.add(criteriaBuilder.equal((root.get("paymentReference").get("wallet").get("managedUser").get("email")), requestModel.getUserEmail()));
        if (!StringUtils.isEmpty(requestModel.getTellerNumber()))
            searchPredicate.add(criteriaBuilder.equal((root.get("tellerNumber")), requestModel.getTellerNumber()));
        if (!StringUtils.isEmpty(requestModel.getAmount()))
            searchPredicate.add(criteriaBuilder.equal((root.get("amount")), requestModel.getAmount()));

        return searchPredicate;

    }
}
