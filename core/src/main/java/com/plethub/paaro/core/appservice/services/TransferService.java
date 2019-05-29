package com.plethub.paaro.core.appservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.plethub.paaro.core.appservice.apirequestmodel.WalletTransferRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.TransferSummary;
import com.plethub.paaro.core.appservice.apiresponsemodel.WalletTransferRequestResponse;
import com.plethub.paaro.core.appservice.dao.EmailMatchAndCompleteTransaction;
import com.plethub.paaro.core.appservice.dao.SmsMatchAndCompleteTransaction;
import com.plethub.paaro.core.appservice.enums.*;
import com.plethub.paaro.core.configuration.service.ConfigurationService;
import com.plethub.paaro.core.infrastructure.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.models.*;
import com.plethub.paaro.core.appservice.repository.*;
import com.plethub.paaro.core.infrastructure.utils.GeneralUtil;
import com.plethub.paaro.core.request.apimodel.RequestResponse;
import com.plethub.paaro.core.request.service.RequestService;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.models.ManagedUser;
import com.plethub.paaro.core.usermanagement.repository.ManagedUserRepository;
//import com.plethub.paaro.core.security.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import com.plethub.paaro.core.usermanagement.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TransferService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletEmailService walletEmailService;

    @Autowired
    private WalletSmsService walletSmsService;

    @Autowired
    private TransferRequestMapRepository transferRequestMapRepository;

    @Autowired
    private WalletTransferTransactionRepository walletTransferTransactionRepository;

    @Autowired
    private WalletFundingTransactionRepository walletFundingTransactionRepository;

    @Autowired
    private SmsMatchAndCompleteTransaction smsMatchAndCompleteTransaction;

    @Autowired
    private EmailMatchAndCompleteTransaction emailMatchAndCompleteTransaction;

    @Autowired
    private ManagedUserRepository userRepository;

    @Autowired
    private TokensRepository tokensRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private RequestRepository requestRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void init() {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Value("${currency.naira-type:NGN}")
    private String nairaCurrency;

    @Value("${currency.naira-limit:10000}")
    private BigDecimal nairaMonthlyTransferLimit;

    @Value("${currency.gbp-limit:10000}")
    private BigDecimal gbpMonthlyTransferLimit;

    @Autowired
    private CurrencyDetailsRepository currencyDetailsRepository;

    @Autowired
    private TransferRequestValidator transferRequestValidator;

    private Logger logger = LoggerFactory.getLogger(TransferService.class.getName());

    @Autowired
    private RequestService requestService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private UserService userService;

    @Transactional
    public WalletTransferRequestResponse createCustomerTransferRequest(WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest) throws Exception {

        WalletTransferRequestResponse walletTransferRequestResponse = transferRequestValidator.validateRequestForCharges(walletTransferRequest);


        if (walletTransferRequestResponse.getResponseStatus() != ApiResponseCode.SUCCESSFUL) {
            return walletTransferRequestResponse;
        }

        TransferSummary transferSummary = computeCharges(walletTransferRequestResponse, false, walletTransferRequest);

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Unable to get logged in user details");
        }
        Wallet wallet = walletTransferRequestResponse.getWallet();
        WalletTransferRequestResponse limitTransferRequestResponse = getSumOfTransactionDoneByUserThisMonth(userDetailsTokenEnvelope.getManagedUser().getId(), wallet.getCurrency().getType(), transferSummary.getTotalAmount());

        if (limitTransferRequestResponse.getResponseStatus() != ApiResponseCode.SUCCESSFUL)
            return limitTransferRequestResponse;


        walletTransferRequest.setFromCurrency(walletTransferRequestResponse.getFromCurrency());
        walletTransferRequest.setToCurrency(walletTransferRequestResponse.getToCurrency());
        walletTransferRequest.setBank(walletTransferRequestResponse.getBank());
        walletTransferRequest.setWallet(wallet);
        walletTransferRequestResponse.setTransferSummary(transferSummary);

        WalletTransferTransaction walletTransferTransaction = createTransactionForLoggedInUser(walletTransferRequestResponse, walletTransferRequest);

        walletTransferTransaction.setManagedUser(userDetailsTokenEnvelope.getManagedUser());

        if (wallet.getAvailableAccountBalance().compareTo(transferSummary.getTotalAmount()) < 0) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Available balance must be greater or equal to the total amount");
        } else {
            walletTransferTransaction.setWalletStatus(WalletStatus.FUNDED);
            BigDecimal newAvailableAccountBalance = wallet.getAvailableAccountBalance().subtract(transferSummary.getTotalAmount());
            wallet.setAvailableAccountBalance(newAvailableAccountBalance);
        }

        walletTransferTransactionRepository.save(walletTransferTransaction);

        walletRepository.save(wallet);

        walletTransferRequestResponse.setMessage("Transfer request successfully logged by customer");
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setWallet(wallet);

        walletEmailService.sendTransferRequestEmail(walletTransferTransaction);
        auditLogService.saveAudit(null,objectMapper.writeValueAsString(walletTransferRequest), Module.TRANSFER,servletRequest,"User logged transfer request (by the customer)");

        return walletTransferRequestResponse;

    }


    @Transactional
    public TransferSummary computeCharges(WalletTransferRequestResponse walletTransferRequestResponse, boolean sendToken, WalletTransferRequest walletTransferRequest) throws IOException {

        TransferSummary transferSummary = new TransferSummary();

        BigDecimal charge ;
        BigDecimal chargeInNaira;
        Double exchangeRate;
        BigDecimal actualAmount = walletTransferRequest.getActualAmount();
        BigDecimal effectiveAmount = BigDecimal.valueOf(0);
        CurrencyDetails fromCurrencyDetails = walletTransferRequestResponse.getFromCurrencyDetails();
        CurrencyDetails toCurrencyDetails = walletTransferRequestResponse.getToCurrencyDetails();

        exchangeRate = walletTransferRequest.getExchangeRate();

        if (fromCurrencyDetails.isFlatChargeEnabled()) {
            charge = BigDecimal.valueOf(fromCurrencyDetails.getFlatChargeRate());
            transferSummary.setFlatRate(charge.doubleValue());
        } else {

            if (walletTransferRequestResponse.getTransferTransactionType().equals(TransferTransactionType.SELLING)) {
                chargeInNaira =  actualAmount.multiply(BigDecimal.valueOf(fromCurrencyDetails.getTransactionFeePerInternationalCurrencyInNaira()));
                charge = chargeInNaira.divide(BigDecimal.valueOf(exchangeRate), 2,RoundingMode.HALF_EVEN);
                effectiveAmount = actualAmount.subtract(charge);

            } else {
                BigDecimal chargeRate = BigDecimal.valueOf((1/exchangeRate));
                BigDecimal actualAmountInForeignCurrency = actualAmount.multiply(chargeRate).setScale(2, RoundingMode.HALF_EVEN);
                charge =  actualAmountInForeignCurrency.multiply(BigDecimal.valueOf(toCurrencyDetails.getTransactionFeePerInternationalCurrencyInNaira()));
                BigDecimal chargeInForeignCurrency = charge.multiply(chargeRate).setScale(2,RoundingMode.HALF_EVEN);
                effectiveAmount = actualAmount.subtract(charge).setScale(2,RoundingMode.HALF_EVEN);

            }

            if (charge.compareTo(BigDecimal.valueOf(fromCurrencyDetails.getServiceChargeCap())) > 0) {
                charge = BigDecimal.valueOf(fromCurrencyDetails.getServiceChargeCap());
            }
        }

        transferSummary.setExchangeRate(exchangeRate);
        transferSummary.setEffectiveAmount(effectiveAmount);
        transferSummary.setChargeAmount(charge);
        transferSummary.setTotalAmount(actualAmount);

        if (sendToken) {
            String token = GeneralUtil.generateTokenForRequest();
            saveToken(walletTransferRequest.getEmail(),token);
            walletEmailService.sendToken(walletTransferRequest.getEmail(),token);
        }

        return transferSummary;


    }

    public WalletTransferRequestResponse computeChargesAndSendToken(WalletTransferRequest walletTransferRequest) throws Exception {

        WalletTransferRequestResponse walletTransferRequestResponse = transferRequestValidator.validateRequestForCharges(walletTransferRequest);


        if (walletTransferRequestResponse.getResponseStatus() != ApiResponseCode.SUCCESSFUL) {
            return walletTransferRequestResponse;
        }

        TransferSummary transferSummary = computeCharges(walletTransferRequestResponse, walletTransferRequest.isSendToken(), walletTransferRequest);

        walletTransferRequestResponse.setTransferSummary(transferSummary);

        return walletTransferRequestResponse;
    }


    private void saveToken(String email, String token) {
        Tokens tokens = new Tokens();
        tokens.setEmail(email);
        tokens.setToken(token);
        tokens.setModule(Module.TRANSFER);
        tokens.setDateAdded(new Date());
        tokensRepository.save(tokens);
    }

    @Transactional
    public WalletTransferRequestResponse initiateTransferRequestForUser(WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest) throws Exception {

        if (walletTransferRequest == null || StringUtils.isBlank(walletTransferRequest.getToAccountName()) || StringUtils.isBlank(walletTransferRequest.getToAccountNumber())
                || StringUtils.isBlank(walletTransferRequest.getNarration()) || StringUtils.isBlank(walletTransferRequest.getEmail()) || StringUtils.isBlank(walletTransferRequest.getToken())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Account name, Token, Account number, Email and Narration cannot be null");
        }

        ManagedUser managedUser = userRepository.findOneByEmail(walletTransferRequest.getEmail());

        if (managedUser == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "User not found with the supplied email");
        }

        WalletTransferRequestResponse walletTransferRequestResponse = null;
//        WalletTransferRequestResponse walletTransferRequestResponse = computeChargesAndValues(walletTransferRequest, false);

        if (walletTransferRequestResponse == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "Null response received");
        }

        if (walletTransferRequestResponse.getResponseStatus() != ApiResponseCode.SUCCESSFUL) {
            return walletTransferRequestResponse;
        }

        Tokens tokens = tokensRepository.findByEmailAndModuleAndTokenOrderByIdDesc(walletTransferRequest.getEmail(),Module.TRANSFER,walletTransferRequest.getToken());

        if (tokens == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Token not valid");
        }

        long tokenDuration = ((new Date()).getTime() - tokens.getDateAdded().getTime())/1000;

        if (tokenDuration > 300) {
            tokensRepository.delete(tokens);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Token has expired");
        }

        tokensRepository.delete(tokens);

        Wallet wallet = walletRepository.findByManagedUser_EmailAndCurrency_Type(walletTransferRequest.getEmail(),walletTransferRequest.getFromCurrency().getType());

        if (wallet == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Wallet not found for user");
        }


        walletTransferRequest.setExchangeRate(walletTransferRequestResponse.getExchangeRate());
        walletTransferRequest.setActualAmount(walletTransferRequestResponse.getAmount());
        walletTransferRequest.setWallet(wallet);

        if (wallet.getAvailableAccountBalance().compareTo(walletTransferRequest.getActualAmount()) < 0 ) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.UNABLE_TO_PROCESS, "Available balance cannot be less than the total amount");
        }

        WalletTransferTransaction walletTransferTransaction = createTransactionForUserByInitiator(walletTransferRequest);

        walletTransferTransaction.setManagedUser(managedUser);

        walletTransferTransactionRepository.save(walletTransferTransaction);

        BigDecimal newAvailableAccountBalance = wallet.getAvailableAccountBalance().subtract(walletTransferRequest.getActualAmount());
        wallet.setAvailableAccountBalance(newAvailableAccountBalance);
        walletRepository.save(wallet);

        walletTransferRequestResponse.setMessage("Transfer request successfully logged for customer by admin user");
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setWallet(wallet);

        walletEmailService.sendTransferRequestEmail(walletTransferTransaction);

        auditLogService.saveAudit(null,objectMapper.writeValueAsString(walletTransferRequest),Module.TRANSFER, servletRequest,String.format("Transfer Request logged for user %s by admin user",managedUser.getEmail()));

        return walletTransferRequestResponse;

    }

    public WalletTransferTransaction createTransactionForUserByInitiator(WalletTransferRequest walletTransferRequest) {
        WalletTransferTransaction transaction = new WalletTransferTransaction();

        transaction.setActualAmount(walletTransferRequest.getActualAmount());
        transaction.setExchangeRate(walletTransferRequest.getExchangeRate());
        transaction.setToAccountNumber(walletTransferRequest.getToAccountNumber());
        transaction.setToCurrency(walletTransferRequest.getToCurrency());
        transaction.setFromCurrency(walletTransferRequest.getFromCurrency());
        transaction.setWallet(walletTransferRequest.getWallet());
        transaction.setNarration(walletTransferRequest.getNarration());
        transaction.setInitiatedDate(new Date());
        transaction.setLastUpdatedDate(transaction.getInitiatedDate());
        transaction.setToAccountName(walletTransferRequest.getToAccountName());
        transaction.setPaaroReferenceId(GeneralUtil.generateRandomValueForRequest());
        transaction.setTransactionStatus(TransactionStatus.INITIATOR_LOGGED_REQUEST);

        return transaction;
    }
    
    public WalletTransferTransaction createTransactionForLoggedInUser(WalletTransferRequestResponse walletTransferRequestResponse, WalletTransferRequest walletTransferRequest) {
        WalletTransferTransaction transaction = new WalletTransferTransaction();
        TransferSummary transferSummary = walletTransferRequestResponse.getTransferSummary();
        BigDecimal chargeAmount = BigDecimal.valueOf(0);

        if (walletTransferRequestResponse.getTransferSummary() != null) {
            chargeAmount = walletTransferRequestResponse.getTransferSummary().getChargeAmount();
        }


        transaction.setActualAmount(transferSummary.getEffectiveAmount());
        transaction.setTotalAmount(transferSummary.getTotalAmount());
        transaction.setExchangeRate(transferSummary.getExchangeRate());
        transaction.setToAccountNumber(walletTransferRequest.getToAccountNumber());
        transaction.setToCurrency(walletTransferRequest.getToCurrency());
        transaction.setFromCurrency(walletTransferRequest.getFromCurrency());
        transaction.setWallet(walletTransferRequest.getWallet());
        transaction.setChargeAmount(chargeAmount);
        transaction.setNarration(walletTransferRequest.getNarration());
        transaction.setInitiatedDate(new Date());
        transaction.setLastUpdatedDate(transaction.getInitiatedDate());
        transaction.setToAccountName(walletTransferRequest.getToAccountName());
        transaction.setPaaroReferenceId(GeneralUtil.generateRandomValueForRequest());
        transaction.setTransactionStatus(TransactionStatus.CUSTOMER_LOGGED_REQUEST);
        transaction.setReceivingBank(walletTransferRequest.getBank());
        transaction.setSortCode(walletTransferRequest.getSortCode());

        if (transaction.getFromCurrency().getType().trim().equals(nairaCurrency)) {
            transaction.setTransferTransactionType(TransferTransactionType.BUYING);
        } else {
            transaction.setTransferTransactionType(TransferTransactionType.SELLING);
        }

        return transaction;
    }

    public WalletTransferTransaction createMappedTransactionForLoggedInUser(WalletTransferRequest walletTransferRequest, TransferSummary transferSummary) {
        WalletTransferTransaction transaction = new WalletTransferTransaction();

        transaction.setChargeAmount(transferSummary.getChargeAmount());
        transaction.setTotalAmount(transferSummary.getTotalAmount());
        transaction.setActualAmount(transferSummary.getEffectiveAmount());
        transaction.setExchangeRate(transferSummary.getExchangeRate());
        transaction.setToAccountNumber(walletTransferRequest.getToAccountNumber());
        transaction.setToCurrency(walletTransferRequest.getToCurrency());
        transaction.setFromCurrency(walletTransferRequest.getFromCurrency());
        transaction.setWallet(walletTransferRequest.getWallet());
        transaction.setNarration(walletTransferRequest.getNarration());
        transaction.setInitiatedDate(new Date());
        transaction.setLastUpdatedDate(transaction.getInitiatedDate());
        transaction.setToAccountName(walletTransferRequest.getToAccountName());
        transaction.setPaaroReferenceId(GeneralUtil.generateRandomValueForRequest());
        transaction.setTransactionStatus(TransactionStatus.COMPLETED);
        transaction.setReceivingBank(walletTransferRequest.getBank());

        if (transaction.getFromCurrency().getType().trim().equals(nairaCurrency)) {
            transaction.setTransferTransactionType(TransferTransactionType.BUYING);
        } else {
            transaction.setTransferTransactionType(TransferTransactionType.SELLING);
        }

        return transaction;
    }

    @Transactional
    public WalletTransferRequestResponse matchTransactionByCustomer(WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest) {

        try {

            if (walletTransferRequest == null) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request");
            }

            WalletTransferTransaction existingWalletTransferTransaction = walletTransferTransactionRepository.findTopByIdAndTransactionStatusIn(walletTransferRequest.getTransactionId(), GeneralUtil.getUnmatchedStatuses());

            if (existingWalletTransferTransaction == null) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Transaction not found");
            }

            WalletTransferRequestResponse walletTransferRequestResponse = transferRequestValidator.validateMatchedTransaction(existingWalletTransferTransaction, walletTransferRequest);

            if (walletTransferRequestResponse.getResponseStatus() != ApiResponseCode.SUCCESSFUL) {
                return walletTransferRequestResponse;
            }
            Wallet wallet = walletTransferRequestResponse.getWallet();

            walletTransferRequest.setExchangeRate(existingWalletTransferTransaction.getExchangeRate());

            TransferSummary transferSummary = computeCharges(walletTransferRequestResponse, false, walletTransferRequest);

            if (wallet.getAvailableAccountBalance().compareTo(transferSummary.getTotalAmount()) < 0) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, String.format("Insufficient balance. You need %.2f of currency %s",transferSummary.getTotalAmount(), wallet.getCurrency().getType()));
            }

            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Unable to get logged in user details");
            }

            walletTransferRequest.setFromCurrency(walletTransferRequestResponse.getFromCurrency());
            walletTransferRequest.setToCurrency(walletTransferRequestResponse.getToCurrency());
            walletTransferRequest.setBank(walletTransferRequestResponse.getBank());
            walletTransferRequest.setWallet(wallet);

            WalletTransferTransaction walletTransferTransaction = createMappedTransactionForLoggedInUser(walletTransferRequest, transferSummary);

            walletTransferTransaction.setManagedUser(userDetailsTokenEnvelope.getManagedUser());
            walletTransferTransaction.setWalletStatus(WalletStatus.FUNDED);
            walletTransferTransaction.setTransactionStatus(TransactionStatus.COMPLETED);
            walletTransferTransactionRepository.save(walletTransferTransaction);

            existingWalletTransferTransaction.setTransactionStatus(TransactionStatus.COMPLETED);
            walletTransferTransactionRepository.save(existingWalletTransferTransaction);

            if (existingWalletTransferTransaction.getWalletStatus().equals(WalletStatus.NOT_FUNDED)) {
                walletEmailService.sendWalletFundingEmailForMatch(existingWalletTransferTransaction);
                walletSmsService.sendWalletFundingSmsForMatch(existingWalletTransferTransaction);
            }

            if (existingWalletTransferTransaction.getFromCurrency().getType().equalsIgnoreCase(nairaCurrency)) {
                mapTransactions(existingWalletTransferTransaction, walletTransferTransaction);
            } else {
                mapTransactions(walletTransferTransaction, existingWalletTransferTransaction);
            }

            BigDecimal newAvailableAccountBalance = wallet.getAvailableAccountBalance().subtract(transferSummary.getTotalAmount());
            wallet.setAvailableAccountBalance(newAvailableAccountBalance);
            walletRepository.save(wallet);

            walletTransferRequestResponse.setMessage("Transfer request successfully mapped by customer");
            walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            walletTransferRequestResponse.setWallet(wallet);
            walletTransferRequestResponse.setTotalAmount(transferSummary.getTotalAmount());
            walletTransferRequestResponse.setTransferSummary(transferSummary);
            walletTransferRequestResponse.setTransferTransactionType(walletTransferTransaction.getTransferTransactionType());

            walletEmailService.sendTransferRequestEmail(walletTransferTransaction);
            auditLogService.saveAudit(null,objectMapper.writeValueAsString(walletTransferRequest), Module.TRANSFER,servletRequest,"User mapped transfer request to customer)");

            return walletTransferRequestResponse;
        } catch (Exception e) {
            logger.error("Error occurred while fetching ongoing transactions with amount and currency type due to ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching ongoing transactions with amount and currency code for user");
        }

    }

    public WalletTransferRequestResponse computeChargesForMatchTransaction(WalletTransferRequest walletTransferRequest) throws Exception {

       WalletTransferRequestResponse walletTransferRequestResponse = transferRequestValidator.validateExistingTransactionForMatch(walletTransferRequest);

       if (walletTransferRequestResponse.getResponseStatus() != ApiResponseCode.SUCCESSFUL) {
           return walletTransferRequestResponse;
       }

       WalletTransferTransaction existingWalletTransferTransaction= walletTransferRequestResponse.getExistingTransaction();
       Currency transactionToCurrency = existingWalletTransferTransaction.getToCurrency();
       Currency transactionFromCurrency = existingWalletTransferTransaction.getFromCurrency();
       walletTransferRequest.setExchangeRate(existingWalletTransferTransaction.getExchangeRate());

        TransferSummary transferSummary = computeCharges(walletTransferRequestResponse, false, walletTransferRequest);


        boolean isNairaHolder;

        isNairaHolder = (transactionToCurrency.getType().equals(nairaCurrency));

        walletTransferRequestResponse.setTransferTransactionType((isNairaHolder) ? TransferTransactionType.BUYING : TransferTransactionType.SELLING);
        walletTransferRequestResponse.setFromCurrency(transactionToCurrency);
        walletTransferRequestResponse.setToCurrency(transactionFromCurrency);
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);

        walletTransferRequestResponse.setTransferSummary(transferSummary);

        return walletTransferRequestResponse;
    }

    public WalletTransferRequestResponse computeChargesAndValuesBeforeLoggingRequest(WalletTransferRequest walletTransferRequest) throws Exception {
        WalletTransferRequestResponse walletTransferRequestResponse = transferRequestValidator.validateRequestForChargesWithoutAccountDetails(walletTransferRequest);

        if (walletTransferRequestResponse.getResponseStatus() != ApiResponseCode.SUCCESSFUL)
            return walletTransferRequestResponse;

        TransferSummary transferSummary = computeCharges(walletTransferRequestResponse, walletTransferRequest.isSendToken(), walletTransferRequest);

        walletTransferRequestResponse.setTransferSummary(transferSummary);

        return walletTransferRequestResponse;
    }


    public WalletTransferRequestResponse settleCustomer(Long id, HttpServletRequest servletRequest) throws JsonProcessingException {

        WalletTransferTransaction transferTransaction = walletTransferTransactionRepository.findOne(id);

        if (transferTransaction == null) return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Transaction not found");
        if (transferTransaction.getSettled()) return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Transaction already setttled");
        if (transferTransaction.getTransactionStatus() != TransactionStatus.SYSTEM_MAPPED_REQUEST && transferTransaction.getTransactionStatus() != TransactionStatus.COMPLETED &&
                transferTransaction.getTransactionStatus() != TransactionStatus.CUSTOMER_LOGGED_REQUEST && transferTransaction.getTransactionStatus() != TransactionStatus.CUSTOMER_MAPPED_REQUEST) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Transaction cannot be settled");
        }

        if (requestService.isRequestPending(Action.SETTLE_CUSTOMER, id)) return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.PENDING_VERIFICATION, "Item is pending verification");
        if (configurationService.isMakerCheckerEnabledForAction(Action.SETTLE_CUSTOMER)) return createSettlementRequestAndAudit(transferTransaction, servletRequest);

        return settleCustomerAndAudit(transferTransaction, servletRequest);

    }

    private WalletTransferRequestResponse settleCustomerAndAudit(WalletTransferTransaction transferTransaction, HttpServletRequest servletRequest) throws JsonProcessingException {

        ManagedUser user = userService.getCurrentLoggedInUser();
        if (user == null) return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "No logged in user found");
        if (transferTransaction.getTotalAmount() == null) return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Total amount cannot be null");

        Wallet wallet = transferTransaction.getWallet();
        if (wallet == null) return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "No Wallet found");
        wallet = effectSettlementInWallet(transferTransaction.getWallet(), transferTransaction.getTotalAmount());
        transferTransaction.setWallet(wallet);
        transferTransaction.setSettled(true);
        transferTransaction.setSettlementVerifiedOrDeclinedBy(user);
        transferTransaction.setDateSettled(new Date());
        transferTransaction = walletTransferTransactionRepository.save(transferTransaction);

        auditLogService.saveAudit(null, objectMapper.writeValueAsString(transferTransaction), Module.TRANSFER, servletRequest, "Transaction settled by Admin user", transferTransaction.getId());
        return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SUCCESSFUL, "Customer has been settled");
    }

    private WalletTransferRequestResponse createSettlementRequestAndAudit(WalletTransferTransaction transferTransaction, HttpServletRequest servletRequest) throws JsonProcessingException {

        String message = requestService.logRequest(transferTransaction.getId(), Action.SETTLE_CUSTOMER, null, null);

        if (!StringUtils.isBlank(message)) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.FAILED, message);
        }

        auditLogService.saveAudit(null, objectMapper.writeValueAsString(transferTransaction), Module.TRANSFER, servletRequest, "Transaction settlement request logged by Admin user", transferTransaction.getId());

        return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SUCCESSFUL, "Transaction settlement request successfully logged");

    }

    public RequestResponse verifySettlement(Request request, HttpServletRequest servletRequest) throws JsonProcessingException {

        if (request == null || request.getEntityId() == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request");
        WalletTransferTransaction transferTransaction = walletTransferTransactionRepository.findOne(request.getEntityId());

        if (transferTransaction == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Transaction not found");
        if (transferTransaction.getSettled()) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Transaction already settled");
        if (transferTransaction.getTransactionStatus() != TransactionStatus.SYSTEM_MAPPED_REQUEST && transferTransaction.getTransactionStatus() != TransactionStatus.COMPLETED &&
                transferTransaction.getTransactionStatus() != TransactionStatus.CUSTOMER_LOGGED_REQUEST && transferTransaction.getTransactionStatus() != TransactionStatus.CUSTOMER_MAPPED_REQUEST) {
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Transaction cannot be settled");
        }

        ManagedUser user = userService.getCurrentLoggedInUser();
        if (user == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "No logged in user found");
        if (transferTransaction.getTotalAmount() == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Total amount cannot be null");

        transferTransaction.setSettled(true);
        transferTransaction.setTransactionStatus(TransactionStatus.COMPLETED);
        transferTransaction.setSettlementVerifiedOrDeclinedBy(user);
        transferTransaction.setDateSettled(new Date());
        Wallet wallet = transferTransaction.getWallet();
        if (wallet == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "No Wallet found");
        wallet = effectSettlementInWallet(transferTransaction.getWallet(), transferTransaction.getTotalAmount());
        transferTransaction.setWallet(wallet);
        transferTransaction = walletTransferTransactionRepository.save(transferTransaction);

        request.setVerifierId(userService.getCurrentLoggedInUser());
        request.setStatus(Status.VERIFIED);
        request.setVerifiedOrDeclinedDate(new Date());
        requestRepository.save(request);

        auditLogService.saveAudit(null, objectMapper.writeValueAsString(transferTransaction), Module.TRANSFER, servletRequest, "Transaction settlement verified by Admin user", transferTransaction.getId());

        return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "Settlement verification successfully verified");

    }

    public Wallet effectSettlementInWallet(Wallet wallet, BigDecimal totalAmount) {
        BigDecimal currentLedgerBalance = (wallet.getLedgerAccountBalance() == null) ? BigDecimal.valueOf(0) : wallet.getLedgerAccountBalance();
        wallet.setLedgerAccountBalance(currentLedgerBalance.subtract(totalAmount));
        return walletRepository.save(wallet);
    }

    public void mapTransactions (WalletTransferTransaction nairaHolderTransaction, WalletTransferTransaction otherCurrencyHolderTransaction) {
        TransferRequestMap transferRequestMap = new TransferRequestMap();
        transferRequestMap.setNairaHolderTransaction(nairaHolderTransaction);
        transferRequestMap.setOtherCurrencyHolderTransaction(otherCurrencyHolderTransaction);
        transferRequestMap.setDateMapped(new Date());
        transferRequestMapRepository.save(transferRequestMap);

        nairaHolderTransaction.setTransferRequestMap(transferRequestMap);
        otherCurrencyHolderTransaction.setTransferRequestMap(transferRequestMap);

        walletTransferTransactionRepository.save(nairaHolderTransaction);
        walletTransferTransactionRepository.save(otherCurrencyHolderTransaction);

        smsMatchAndCompleteTransaction.sendNotificationForMatchedTransaction(nairaHolderTransaction, otherCurrencyHolderTransaction);
        emailMatchAndCompleteTransaction.sendNotificationForMatchedTransaction(nairaHolderTransaction, otherCurrencyHolderTransaction);

        //Settle both by calling the API to fund


    }

    public WalletTransferRequestResponse cancleBid(Long id, HttpServletRequest servletRequest) {

        if (id == null)
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Id cannot be null");

        WalletTransferTransaction transferTransaction = walletTransferTransactionRepository.findOne(id);

        if (transferTransaction == null)
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Transaction not found");

        ManagedUser managedUser = userService.getCurrentLoggedInUser();

        if (managedUser == null)
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Currently loggedin user details not found");

        if (transferTransaction.getManagedUser() == null || Long.compare(transferTransaction.getManagedUser().getId(), managedUser.getId()) != 0)
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "User that logged transaction not found");

        if (transferTransaction.getTransactionStatus() != TransactionStatus.CUSTOMER_LOGGED_REQUEST)
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Transaction cannot be cancelled due to its status");

        return refundCustomer(transferTransaction, servletRequest);

    }


    private WalletTransferRequestResponse refundCustomer(WalletTransferTransaction walletTransferTransaction, HttpServletRequest servletRequest) {

        try {
            Wallet wallet = walletTransferTransaction.getWallet();

            WalletTransferTransaction initialWalletTransferTransaction = walletTransferTransaction;

            if (wallet == null)
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Wallet not found");

            BigDecimal newAvailableBalance = wallet.getAvailableAccountBalance().add(walletTransferTransaction.getTotalAmount());
            wallet.setAvailableAccountBalance(newAvailableBalance);
            wallet = walletRepository.save(wallet);
            walletTransferTransaction.setTransactionStatus(TransactionStatus.BID_CANCELLED);
            walletTransferTransaction.setWallet(wallet);
            walletTransferTransactionRepository.save(walletTransferTransaction);

            createWalletFundingTransaction(walletTransferTransaction, servletRequest);

            auditLogService.saveAudit(objectMapper.writeValueAsString(initialWalletTransferTransaction), objectMapper.writeValueAsString(walletTransferTransaction), Module.TRANSFER, servletRequest, String.format("Wallet transfer request cancelled by user %s", wallet.getManagedUser().getEmail()));

            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SUCCESSFUL, "Transaction successfully cancelled");
        } catch (Exception e) {
            logger.error("Error occurred while refunding customer due to ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred");
        }

    }

    private void createWalletFundingTransaction(WalletTransferTransaction walletTransferTransaction, HttpServletRequest servletRequest) throws Exception {

            WalletFundingTransaction walletFundingTransaction = new WalletFundingTransaction();
            walletFundingTransaction.setTransactionStatus(TransactionStatus.BID_CANCELLING_REFUND);
            walletFundingTransaction.setWallet(walletTransferTransaction.getWallet());
            walletFundingTransaction.setActualAmount(walletTransferTransaction.getTotalAmount());
            walletFundingTransaction.setTotalAmount(walletTransferTransaction.getTotalAmount());
            walletFundingTransaction.setPaaroReferenceId(walletTransferTransaction.getPaaroReferenceId());
            walletFundingTransaction.setThirdPartyReferenceId(walletTransferTransaction.getThirdPartyReferenceId());
            walletFundingTransaction.setManagedUser(walletTransferTransaction.getManagedUser());
            walletFundingTransaction.setDateFunded(new Date());
            walletFundingTransaction.setTransactionId(walletTransferTransaction.getId());
            walletFundingTransaction.setExchangeRate(0d);
            walletFundingTransaction.setNarration("User cancelled bid");
            walletFundingTransaction.setInitiatedDate(new Date());
            walletFundingTransaction.setLastUpdatedDate(new Date());
            walletFundingTransaction.setCurrency(walletTransferTransaction.getFromCurrency());
            walletFundingTransactionRepository.save(walletFundingTransaction);
            auditLogService.saveAudit(null, objectMapper.writeValueAsString(walletFundingTransaction), Module.TRANSFER, servletRequest, String.format("Wallet funded after user %s cancelled bid", walletTransferTransaction.getManagedUser().getEmail()));

    }

    private WalletTransferRequestResponse getSumOfTransactionDoneByUserThisMonth(Long userid, String currency, BigDecimal amount) {

        List<TransactionStatus> transactionStatuses = new ArrayList<>();
        transactionStatuses.add(TransactionStatus.COMPLETED);
        transactionStatuses.add(TransactionStatus.SYSTEM_MAPPED_REQUEST);
        transactionStatuses.add(TransactionStatus.CUSTOMER_LOGGED_REQUEST);
        transactionStatuses.add(TransactionStatus.CUSTOMER_MAPPED_REQUEST);

        BigDecimal summation = walletTransferTransactionRepository.getSumOfUsersTransactionsPendingOrCompleted(GeneralUtil.getStartOfCurrentMonth(), new Date(), currency, transactionStatuses, userid);
        summation = summation == null ? amount : summation.add(amount);

        if (currency.trim().equalsIgnoreCase(nairaCurrency.trim())) {
            if (summation.compareTo(nairaMonthlyTransferLimit) > 0)
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Transfer limit reached for Naira account");
        } else {
            if (summation.compareTo(gbpMonthlyTransferLimit) > 0)
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Transfer limit reached for GBP account");
        }

        return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SUCCESSFUL, "Limit not reached");
    }


}
