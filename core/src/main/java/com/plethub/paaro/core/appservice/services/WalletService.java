package com.plethub.paaro.core.appservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plethub.paaro.core.appservice.apirequestmodel.WalletRequest;
import com.plethub.paaro.core.appservice.apirequestmodel.WalletTransactionSearchRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.WalletBalanceDashboardDetails;
import com.plethub.paaro.core.appservice.apiresponsemodel.WalletResponse;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.PaymentMethod;
import com.plethub.paaro.core.appservice.enums.TransactionStatus;
import com.plethub.paaro.core.appservice.enums.WalletStatus;
import com.plethub.paaro.core.infrastructure.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.models.*;
import com.plethub.paaro.core.appservice.repository.*;
import com.plethub.paaro.core.infrastructure.utils.GeneralUtil;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.models.ManagedUser;
//import com.plethub.paaro.core.security.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private WalletEmailService walletEmailService;

    @Autowired
    private WalletFundingTransactionRepository walletFundingTransactionRepository;

    @Autowired
    private WalletTransferTransactionRepository walletTransferTransactionRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private PaymentReferenceRepository paymentReferenceRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Logger logger = LoggerFactory.getLogger(WalletService.class.getName());

    @Value("${currency.naira-type:NGN}")
    private String nairaCurrency;

    @Value("${currency.naira-type:GBP}")
    private String gbpCurrency;

    public WalletResponse findWalletById(Long id) {

        if (id == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Null id passed");
        }

        Wallet wallet = walletRepository.getOne(id);

        if (wallet!= null) {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWallet(wallet);
            return walletResponse;
        } else {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Record not found");
        }

    }
    public WalletResponse findWalletsByUserId(Long id) {

        if (id == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Null id passed");
        }

        List<Wallet> wallets = walletRepository.findAllByManagedUser_Id(id);

        if (CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "No wallet found for user");

        } else {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWalletList(wallets);
            return walletResponse;
        }

    }

    public WalletResponse findWalletsByEmail(String email) {

        if (StringUtils.isBlank(email)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Email cannot be blank");
        }



        List<Wallet> wallets = walletRepository.findAllByManagedUser_Email(email);

        if (CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "No wallet found for email");

        } else {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWalletList(wallets);
            return walletResponse;
        }

    }

    public WalletResponse findWalletsByCurrency(String currency) {

        if (StringUtils.isBlank(currency)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        List<Wallet> wallets = walletRepository.findAllByCurrency_Type(currency);

        if (CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "No wallet found for currency");

        } else {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWalletList(wallets);
            return walletResponse;
        }
    }

    public Wallet findByManagedUser_EmailAndCurrency_Type(String email, String currency) throws Exception{
//        try {
            return walletRepository.findByManagedUser_EmailAndCurrency_Type(email, currency);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new Exception(e.getMessage());
//        }
    }
    public WalletResponse findLoggedInUserWallets() {
        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null || StringUtils.isBlank(userDetailsTokenEnvelope.getManagedUser().getEmail())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid credentials");
        }

        List<Wallet> wallets = walletRepository.findAllByManagedUser_Id(userDetailsTokenEnvelope.getManagedUser().getId());

        if (CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "No wallet found for user");

        } else {
            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWalletList(wallets);
            return walletResponse;
        }

    }

    public WalletResponse addWallet(WalletRequest walletRequest, HttpServletRequest servletRequest) throws IOException {

        if ( walletRequest == null || StringUtils.isBlank(walletRequest.getCurrencyType()) ) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null || StringUtils.isBlank(userDetailsTokenEnvelope.getManagedUser().getEmail())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid credentials");
        }

        String email = userDetailsTokenEnvelope.getManagedUser().getEmail();

        Currency currency = currencyRepository.findCurrencyByType(walletRequest.getCurrencyType());

        if (currency == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "The currency type is not found on the system. Please choose the appropriate currency");
        }

        List<Wallet> wallets = walletRepository.findAllByCurrency_TypeAndManagedUser_Email(walletRequest.getCurrencyType(), email);

        if (!CollectionUtils.isEmpty(wallets)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.ALREADY_EXIST, "Wallet of the passed currency already exist for user");

        } else {
            Wallet wallet = new Wallet();
            wallet.setCurrency(currency);
            wallet.setActive(true);
            wallet.setManagedUser(userDetailsTokenEnvelope.getManagedUser());
            walletRepository.save(wallet);

            WalletResponse walletResponse = new WalletResponse();
            walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletResponse.setWallet(wallet);

            walletEmailService.sendWalletAdditionToAccountEmail(wallet);
            auditLogService.saveAudit(null,objectMapper.writeValueAsString(wallet), Module.WALLET,servletRequest,String.format("Wallet added to user %s account",email));
            return walletResponse;
        }

    }

    public WalletResponse fundWallet(WalletRequest walletRequest, HttpServletRequest servletRequest) throws IOException {

        if (walletRequest == null ) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Request cannot be null");
        }

        String validationResult = getValidationRequestErrorMessage(walletRequest);

        if (validationResult != null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, validationResult);
        }

        if (walletRequest.getPaymentMethod() == PaymentMethod.BANK_DEPOSIT) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SUCCESSFUL, "Bank deposit funding will be confirmed later by admin");
        }

        Wallet wallet = getCurrentLoggedInUserWalletByCurrencyType(walletRequest.getCurrencyType());
        Wallet oldWalletReference = wallet;

        if (wallet == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Wallet of the specified currency type not found for user");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        walletRequest.setWallet(wallet);

        WalletFundingTransaction fundingTransaction = createFundTransactionFromWalletrequest(walletRequest);
        fundingTransaction.setManagedUser(userDetailsTokenEnvelope.getManagedUser());

        walletFundingTransactionRepository.save(fundingTransaction);
        WalletResponse walletResponse = new WalletResponse();


        if (fundingTransaction.getTransactionStatus() == TransactionStatus.SUCCESSFUL) {
            BigDecimal walletAmount = wallet.getAvailableAccountBalance();
            BigDecimal walletLedgerAmount = wallet.getLedgerAccountBalance();

            wallet.setAvailableAccountBalance(walletAmount.add(fundingTransaction.getActualAmount()));
            wallet.setLedgerAccountBalance(walletLedgerAmount.add(fundingTransaction.getActualAmount()));
            walletRepository.save(wallet);
            walletResponse.setMessage("Wallet has been credited with " + walletRequest.getActualAmount());
        }

        walletResponse.setWallet(wallet);
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);

        walletEmailService.sendWalletFundingEmail(wallet, fundingTransaction.getActualAmount());
        auditLogService.saveAudit(objectMapper.writeValueAsString(oldWalletReference), objectMapper.writeValueAsString(wallet),Module.WALLET,servletRequest,String.format("Wallet owned by %s funded",wallet.getManagedUser().getEmail()));
        return walletResponse;

    }

    public WalletResponse findAllFundingWalletTransactionsByUserWallet(WalletRequest walletRequest) {

        if (walletRequest == null || StringUtils.isBlank(walletRequest.getCurrencyType())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.getManagedUser().getEmail();

        if (StringUtils.isBlank(email)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "Unable to get email of the user");
        }

        List<WalletFundingTransaction> fundingTransactions = walletFundingTransactionRepository.findAllByManagedUser_EmailAndCurrency_Type(email, walletRequest.getCurrencyType());

        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletResponse.setMessage("Transactions fetched");
        walletResponse.setWalletFundingTransactions(fundingTransactions);

        return walletResponse;

    }

    public WalletResponse findALlFundingWalletTransactionsByUserWalletPaged(WalletRequest walletRequest) {

        if (walletRequest == null || StringUtils.isBlank(walletRequest.getCurrencyType())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.getManagedUser().getEmail();

        if (StringUtils.isBlank(email)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "Unable to get email of the user");
        }

        Pageable pageable = new PageRequest(walletRequest.getPageNo(), walletRequest.getPageSize());

        Page<WalletFundingTransaction> fundingTransactions = walletFundingTransactionRepository.findAllByManagedUser_EmailAndCurrency_Type(email, walletRequest.getCurrencyType(), pageable);

        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletResponse.setMessage("Transactions fetched");
        walletResponse.setWalletFundingTransactionPage(fundingTransactions);

        return walletResponse;

    }

    public WalletResponse findALlFundingWalletTransactionsByEmailAndCurrency(WalletRequest walletRequest) {

        if (walletRequest == null || StringUtils.isBlank(walletRequest.getCurrencyType()) || StringUtils.isBlank(walletRequest.getEmail())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type and email cannot be blank");
        }

        List<WalletFundingTransaction> fundingTransactions = walletFundingTransactionRepository.findAllByManagedUser_EmailAndCurrency_Type(walletRequest.getEmail(), walletRequest.getCurrencyType());

        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletResponse.setMessage("Transactions fetched");
        walletResponse.setWalletFundingTransactions(fundingTransactions);

        return walletResponse;

    }

    public WalletResponse findALlFundingWalletTransactionsByEmailAndCurrencyPaged(WalletRequest walletRequest) {

        if (walletRequest == null || StringUtils.isBlank(walletRequest.getCurrencyType()) || StringUtils.isBlank(walletRequest.getEmail())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type and email cannot be blank");
        }

        Pageable pageable = new PageRequest(walletRequest.getPageNo(), walletRequest.getPageSize());

        Page<WalletFundingTransaction> fundingTransactions = walletFundingTransactionRepository.findAllByManagedUser_EmailAndCurrency_Type(walletRequest.getEmail(), walletRequest.getCurrencyType(), pageable);

        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletResponse.setMessage("Transactions fetched");
        walletResponse.setWalletFundingTransactionPage(fundingTransactions);

        return walletResponse;

    }

    public WalletResponse findALlFundingWalletTransactionsByEmailPaged(WalletRequest walletRequest) {

        if (walletRequest == null || StringUtils.isBlank(walletRequest.getEmail())) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Email cannot be blank");
        }

        Pageable pageable = new PageRequest(walletRequest.getPageNo(), walletRequest.getPageSize());

        Page<WalletFundingTransaction> fundingTransactions = walletFundingTransactionRepository.findAllByWallet_ManagedUser_Email(walletRequest.getEmail(), pageable);

        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletResponse.setMessage("Transactions fetched");
        walletResponse.setWalletFundingTransactionPage(fundingTransactions);

        return walletResponse;

    }



    public String getValidationRequestErrorMessage(WalletRequest walletRequest) {

        if (StringUtils.isBlank(walletRequest.getNarration())   || StringUtils.isBlank(walletRequest.getThirdPartyTransactionId())
                || StringUtils.isBlank(walletRequest.getCurrencyType()) || walletRequest.getPaymentMethod() == null ) {

            return "Narration, third party transaction reference id, payment method and currency type cannot be blank.";

        }

        if (walletRequest.getActualAmount() == null || walletRequest.getActualAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
            return  "Actual amount must be greater than 0";
        }

        Currency currency = currencyRepository.findCurrencyByType(walletRequest.getCurrencyType());

        if (currency == null) {
            return "Currency type does not exist on the system";
        }

        walletRequest.setCurrency(currency);

        return null;

    }

    public Wallet getCurrentLoggedInUserWalletByCurrencyType(String currencyType) {

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.getManagedUser().getEmail();

        return walletRepository.findByManagedUser_EmailAndCurrency_Type(email, currencyType);

    }

    public Wallet getUserWalletByCurrencyType(ManagedUser managedUser, String currencyType) {

        String email = managedUser.getEmail();

        return walletRepository.findByManagedUser_EmailAndCurrency_Type(email, currencyType);

    }


    private WalletFundingTransaction createFundTransactionFromWalletrequest(WalletRequest walletRequest) {

        WalletFundingTransaction transaction = new WalletFundingTransaction();

        transaction.setActualAmount(walletRequest.getActualAmount());
        transaction.setExchangeRate(0d);
        transaction.setCurrency(walletRequest.getCurrency());
        transaction.setWallet(walletRequest.getWallet());

        if (null == walletRequest.getNarration() || walletRequest.getNarration().equalsIgnoreCase("")){
            transaction.setNarration("Fund wallet via Online payment");
        }else {
            transaction.setNarration(walletRequest.getNarration());
        }
        transaction.setInitiatedDate(new Date());
        transaction.setLastUpdatedDate(transaction.getInitiatedDate());
        transaction.setPaaroReferenceId(GeneralUtil.generateRandomValueForRequest());
        transaction.setThirdPartyReferenceId(walletRequest.getThirdPartyTransactionId());
        transaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        transaction.setPaymentMethod(walletRequest.getPaymentMethod());
        transaction.setPaymentReferenceId(walletRequest.getPaymentReferenceId());
        return transaction;

    }

    public Page<WalletFundingTransaction> findWalletFundingTransactionPageWithFilter (int pageNo, int pageSize, String filter) {

        PageRequest pageRequest = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id");

        return walletFundingTransactionRepository.filterByWalletUserFirstOrLastName("%"+filter+"%",pageRequest);

    }

    public Page<WalletTransferTransaction> findAllPendingTransactionsPageWithFilter (int pageNo, int pageSize, String filter) {

        PageRequest pageRequest = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id");

        return walletTransferTransactionRepository.filterByWalletUserFirstOrLastNameAndPendingRequests("%"+filter+"%", TransactionStatus.INITIATOR_LOGGED_REQUEST, pageRequest);

    }


    public Page<WalletFundingTransaction> findWalletFundingTransactionPage (int pageNo, int pageSize) {

        PageRequest pageRequest = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id");

        return walletFundingTransactionRepository.findAllByIdIsNotNull(pageRequest);

    }

    private List<Wallet> getAllTransferTransactionsByEmail(String email) {

        return walletRepository.findAllByManagedUser_Email(email);

    }

    public Page<Wallet> findWalletByCurrencyPaged(WalletRequest walletRequest){

        if (StringUtils.isBlank(walletRequest.getCurrencyType())) {
            return new PageImpl<>(new ArrayList<>());
        }

        PageRequest pageRequest = new PageRequest(walletRequest.getPageNo(), walletRequest.getPageSize(), Sort.Direction.DESC, "id");

        String filter = (walletRequest.getFilter() == null) ? "" : walletRequest.getFilter().trim();

        return walletRepository.findWalletsByCurrencyTypeAndFilter(walletRequest.getCurrencyType(),"%"+ filter + "%",pageRequest);

    }

    public  void addWalletToUserAccount(Currency currency, ManagedUser managedUser) {

        if (currency != null && managedUser != null) {
            Wallet wallet = new Wallet();
            wallet.setLedgerAccountBalance(BigDecimal.valueOf(0));
            wallet.setAvailableAccountBalance(BigDecimal.valueOf(0));
            wallet.setManagedUser(managedUser);
            wallet.setCurrency(currency);
            wallet.setActive(true);
            walletRepository.save(wallet);
        }

    }

    public WalletResponse fundWalletAndTransaction(WalletRequest walletRequest, HttpServletRequest servletRequest) throws IOException {

        if (walletRequest == null ) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Request cannot be null");
        }

        String validationResult = getValidationRequestErrorMessage(walletRequest);

        if (validationResult != null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, validationResult);
        }

        if (walletRequest.getPaymentMethod() == PaymentMethod.BANK_DEPOSIT) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SUCCESSFUL, "Bank deposit funding will be confirmed later by admin");
        }

        Wallet wallet = getCurrentLoggedInUserWalletByCurrencyType(walletRequest.getCurrencyType());
        Wallet oldWalletReference = wallet;

        if (wallet == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Wallet of the specified currency type not found for user");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        walletRequest.setWallet(wallet);

        WalletFundingTransaction fundingTransaction = createFundTransactionFromWalletrequest(walletRequest);
        fundingTransaction.setManagedUser(userDetailsTokenEnvelope.getManagedUser());

        walletFundingTransactionRepository.save(fundingTransaction);
        WalletResponse walletResponse = new WalletResponse();


        if (fundingTransaction.getTransactionStatus() == TransactionStatus.SUCCESSFUL) {
            BigDecimal walletAmount = wallet.getAvailableAccountBalance();
            BigDecimal ledgerAccountBalance = wallet.getLedgerAccountBalance();

            wallet.setAvailableAccountBalance(walletAmount.add(fundingTransaction.getActualAmount()));
            wallet.setLedgerAccountBalance(ledgerAccountBalance.add(fundingTransaction.getActualAmount()));
            walletRepository.save(wallet);
            walletResponse.setMessage("Wallet has been credited with " + walletRequest.getActualAmount());
        }

        walletResponse.setWallet(wallet);
        walletResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);

        walletResponse = fundTransaction(walletRequest, walletResponse, userDetailsTokenEnvelope.getManagedUser().getId());

        walletEmailService.sendWalletFundingEmail(wallet, fundingTransaction.getActualAmount());
        auditLogService.saveAudit(objectMapper.writeValueAsString(oldWalletReference), objectMapper.writeValueAsString(walletResponse.getWallet()),Module.WALLET,servletRequest,String.format("Wallet owned by %s funded and also tried to fund wallet",wallet.getManagedUser().getEmail()));
        return walletResponse;

    }

    public WalletResponse  fundTransaction(WalletRequest walletRequest, WalletResponse walletResponse, Long userId) {

        WalletTransferTransaction transferTransaction = walletTransferTransactionRepository.findOneByIdAndWalletStatusAndFromCurrency_TypeAndManagedUser_Id(walletRequest.getTransactionId(), WalletStatus.NOT_FUNDED, walletRequest.getCurrencyType(), userId);

        String message = "";
        Wallet wallet = walletResponse.getWallet();

        if (transferTransaction == null) {
            message = "No transaction that requires funding of the currency type " + walletRequest.getCurrencyType();
        } else if (transferTransaction.getTotalAmount().compareTo(walletResponse.getWallet().getAvailableAccountBalance()) > 0) {
            message = "You do not have upto the transaction amount in your wallet of type " + walletRequest.getCurrencyType();
        } else {
            BigDecimal availableAccountBalance = wallet.getAvailableAccountBalance();
            BigDecimal ledgerAccountBalance = wallet.getLedgerAccountBalance();

            availableAccountBalance = availableAccountBalance.subtract(transferTransaction.getTotalAmount());

            wallet.setAvailableAccountBalance(availableAccountBalance);
            walletRepository.save(wallet);

            transferTransaction.setWalletStatus(WalletStatus.FUNDED);
            transferTransaction.setDateFunded(new Date());

            walletTransferTransactionRepository.save(transferTransaction);

        }

        walletResponse.setMessage(String.format("%s %s",walletResponse.getMessage(), message));
        walletResponse.setWallet(wallet);
        return walletResponse;
    }

    public WalletResponse fundTransactionFromWallet(WalletRequest walletRequest, HttpServletRequest servletRequest) {

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (walletRequest == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request object");
        }

        if (walletRequest.getTransactionId() == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Transaction id cannot be null");
        }

        String currencyType = walletRequest.getCurrencyType();

        if (StringUtils.isBlank(currencyType)) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }


        Wallet wallet = walletRepository.findByManagedUser_IdAndCurrency_Type(userDetailsTokenEnvelope.getManagedUser().getId(), currencyType);

        if (wallet == null) {
            return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Wallet not found for user");
        }

        WalletResponse walletResponse = new WalletResponse();

        walletResponse.setMessage("");
        walletResponse.setWallet(wallet);

        walletResponse = fundTransaction(walletRequest,walletResponse,userDetailsTokenEnvelope.getManagedUser().getId());

        return walletResponse;

    }

    private boolean isPaymentReferenceValidForLogggedInUser(WalletRequest walletRequest) {

        PaymentReference paymentReference = paymentReferenceRepository.getOne(walletRequest.getPaymentReferenceId());

        if (paymentReference == null || paymentReference.getWallet()== null || paymentReference.getWallet().getId() != walletRequest.getWallet().getId()) {
            return false;
        }
        walletRequest.setPaymentReference(paymentReference);
        return true;
    }

    public WalletResponse searchTransactionsByOrOptions(WalletTransactionSearchRequestModel requestModel) {

        if (requestModel == null) return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request");

        Integer pageNo = requestModel.getPageNo() == null || requestModel.getPageNo() < 0 ? 0 : requestModel.getPageNo();
        Integer pageSize = requestModel.getPageSize() == null || requestModel.getPageSize() < 1 ? 15 : requestModel.getPageSize();

        Page transactions = walletFundingTransactionRepository.findAll((Specification<WalletFundingTransaction>) (root, criteriaQuery, criteriaBuilder) -> {

            return criteriaBuilder.or(getPredicateListFromRequest(requestModel, criteriaBuilder, root).toArray(new Predicate[0]));
        }, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        WalletResponse walletTransferRequestResponse = new WalletResponse();
        walletTransferRequestResponse.setWalletFundingTransactionPage(transactions);
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);

        return walletTransferRequestResponse;

    }

    public WalletResponse searchTransactionsByAndOptions(WalletTransactionSearchRequestModel requestModel) {

        if (requestModel == null) return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request");

        Integer pageNo = requestModel.getPageNo() == null || requestModel.getPageNo() < 0 ? 0 : requestModel.getPageNo();
        Integer pageSize = requestModel.getPageSize() == null || requestModel.getPageSize() < 1 ? 15 : requestModel.getPageSize();

        Page transactions = walletFundingTransactionRepository.findAll((Specification<WalletFundingTransaction>) (root, criteriaQuery, criteriaBuilder) -> {

            return criteriaBuilder.and(getPredicateListFromRequest(requestModel, criteriaBuilder, root).toArray(new Predicate[0]));
        }, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        WalletResponse walletTransferRequestResponse = new WalletResponse();
        walletTransferRequestResponse.setWalletFundingTransactionPage(transactions);
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);

        return walletTransferRequestResponse;
    }

    public WalletResponse searchTransactionsByAndOptionsPageable(Pageable pageable, WalletTransactionSearchRequestModel requestModel) {

        if (requestModel == null) return WalletResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request");

        Page transactions = walletFundingTransactionRepository.findAll((Specification<WalletFundingTransaction>) (root, criteriaQuery, criteriaBuilder) -> {

            return criteriaBuilder.and(getPredicateListFromRequest(requestModel, criteriaBuilder, root).toArray(new Predicate[0]));
        }, new PageRequest(pageable.getOffset()/pageable.getPageSize(), pageable.getPageSize(), Sort.Direction.DESC, "id"));

        WalletResponse walletTransferRequestResponse = new WalletResponse();
        walletTransferRequestResponse.setWalletFundingTransactionPage(transactions);
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);

        return walletTransferRequestResponse;
    }

    private List<Predicate> getPredicateListFromRequest(WalletTransactionSearchRequestModel requestModel, CriteriaBuilder criteriaBuilder, Root root) {
        List<Predicate> searchPredicate = new ArrayList<>();

        if (requestModel.getTransactionStatus() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("transactionStatus")), requestModel.getTransactionStatus()));
        if (requestModel.getPaymentMethod() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("paymentMethod")), requestModel.getPaymentMethod()));
        if (requestModel.getId() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("id")), requestModel.getId()));
        if (!StringUtils.isEmpty(requestModel.getPaaroReferenceId()))
            searchPredicate.add(criteriaBuilder.like((root.get("paaroReferenceId")), "%"+requestModel.getPaaroReferenceId()+"%"));
        if (!StringUtils.isEmpty(requestModel.getCurrency()))
            searchPredicate.add(criteriaBuilder.like((root.get("currency").get("type")), "%"+requestModel.getCurrency()+"%"));
        if (requestModel.getUserId() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("managedUser").get("id")), requestModel.getUserId()));
        if (requestModel.getActualAmount() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("actualAmount")), requestModel.getActualAmount()));

        return searchPredicate;
    }

    public WalletBalanceDashboardDetails getWalletBalanceDashboardDetails() {

        List<WalletBalanceDashboardDetails> nairawalletBalanceDashboardDetails = walletRepository.getWalletBalanceDetailsByCurrency(nairaCurrency);
        List<WalletBalanceDashboardDetails> gbpwalletBalanceDashboardDetails = walletRepository.getWalletBalanceDetailsByCurrency(gbpCurrency);

        WalletBalanceDashboardDetails walletBalanceDashboardDetails = new WalletBalanceDashboardDetails();

        if (!CollectionUtils.isEmpty(nairawalletBalanceDashboardDetails) && nairawalletBalanceDashboardDetails.get(0) != null) {
            walletBalanceDashboardDetails.setNairaAvailableBalance(NumberFormat.getInstance().format(nairawalletBalanceDashboardDetails.get(0).getAvailableBalance()));
            walletBalanceDashboardDetails.setNairaLedgerBalance(NumberFormat.getInstance().format(nairawalletBalanceDashboardDetails.get(0).getLedgerBalance()));
        }

        if (!CollectionUtils.isEmpty(gbpwalletBalanceDashboardDetails) && gbpwalletBalanceDashboardDetails.get(0) != null) {
            walletBalanceDashboardDetails.setForeignAvailableBalance(NumberFormat.getInstance().format(gbpwalletBalanceDashboardDetails.get(0).getAvailableBalance()));
            walletBalanceDashboardDetails.setForeignLedgerBalance(NumberFormat.getInstance().format(gbpwalletBalanceDashboardDetails.get(0).getLedgerBalance()));
        }

        walletBalanceDashboardDetails.setApiResponseCode(ApiResponseCode.SUCCESSFUL);

        return walletBalanceDashboardDetails;

    }

    private String formatWithComma(BigDecimal bigDecimal){
        if (null != bigDecimal){
            return NumberFormat.getInstance().format(bigDecimal);
        }else{
            return NumberFormat.getInstance().format(new BigDecimal(0));
        }
    }
}
