package com.plethub.paaro.core.appservice.services;

import com.google.common.base.Strings;
import com.plethub.paaro.core.appservice.apirequestmodel.WalletTransferRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.KycResponseModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.WalletTransferRequestResponse;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.KycStatus;
import com.plethub.paaro.core.infrastructure.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.models.*;
import com.plethub.paaro.core.appservice.repository.*;
import com.plethub.paaro.core.infrastructure.utils.GeneralUtil;
import com.plethub.paaro.core.models.ManagedUser;
import com.plethub.paaro.core.usermanagement.repository.ManagedUserRepository;
//import com.plethub.paaro.core.security.UserDetailsTokenEnvelope;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TransferRequestValidator {


    @Value("${currency.naira-type:NGN}")
    private String nairaCurrency;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ManagedUserRepository managedUserRepository;

    @Autowired
    private BankService bankService;

    @Autowired
    private KycService kycService;

    @Autowired
    private WalletTransferTransactionRepository walletTransferTransactionRepository;

    @Autowired
    private CurrencyDetailsRepository currencyDetailsRepository;

    public WalletTransferRequestResponse validateRequestForCharges(WalletTransferRequest walletTransferRequest) throws IOException {

        CurrencyDetails fromCurrencyDetails = null;
        CurrencyDetails toCurrencyDetails = null;

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();

        String fromCurrency = walletTransferRequest.getFromCurrencyType();
        String toCurrency = walletTransferRequest.getToCurrencyType();

        TransferTransactionType transferTransactionType = null;

        BigDecimal amount = walletTransferRequest.getActualAmount();
        Double userRate = walletTransferRequest.getExchangeRate();

        if (walletTransferRequest.getToAccountNumber() == null || StringUtils.isBlank(walletTransferRequest.getToAccountNumber())  || StringUtils.isBlank(walletTransferRequest.getNarration()) ) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request. Narration and Account number cannot be blank");
        }

        if ( amount == null || amount.compareTo(BigDecimal.valueOf(0)) <= 0) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Amount must be greater than 0");
        }

        if ( userRate == null || userRate <= 0) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "User rate must be greater than 0");
        }

        if (fromCurrency.trim().equalsIgnoreCase(toCurrency.trim())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "From currency and to currency cannot be equal");
        }

        if (!fromCurrency.trim().equalsIgnoreCase(nairaCurrency.trim()) && !toCurrency.trim().equalsIgnoreCase(nairaCurrency.trim())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "From currency or to currency must be Naira");
        }


        Wallet wallet = walletService.getCurrentLoggedInUserWalletByCurrencyType(walletTransferRequest.getFromCurrencyType());

        if (wallet == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Wallet of the specified from currency type not found for user");
        }

        Currency fromCurrencyObject = wallet.getCurrency();

        Currency toCurrencyObject = currencyRepository.findCurrencyByType(toCurrency.trim());

        if (toCurrencyObject == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "To currency not found");
        }

        if (toCurrencyObject.isDisabled()) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, String.format("Currency %s has been disabled", toCurrencyObject.getType()));
        }

        if (fromCurrencyObject.isDisabled()) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, String.format("Currency %s has been disabled", fromCurrencyObject.getType()));
        }

        fromCurrencyDetails = currencyDetailsRepository.findByCurrencyCode(fromCurrency);
        toCurrencyDetails = currencyDetailsRepository.findByCurrencyCode(toCurrency);

        if (Strings.nullToEmpty(toCurrencyObject.getType()).trim().equalsIgnoreCase(nairaCurrency)) {
            transferTransactionType = TransferTransactionType.SELLING;
        }

        if (Strings.nullToEmpty(fromCurrencyObject.getType()).trim().equalsIgnoreCase(nairaCurrency)) {
            transferTransactionType = TransferTransactionType.BUYING;
        }


        if (fromCurrencyDetails == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, String.format("No currency set details set up for this currency %s", fromCurrency));
        }

        if (toCurrencyDetails == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, String.format("No currency set details set up for this currency %s", fromCurrency));
        }

        Bank bank = bankRepository.getOne(walletTransferRequest.getBankId());

        if (bank == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Bank not found");
        }

        if (bank.isDisabled()) return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Bank has been disabled");

        String accountName = null;
        if (toCurrency.equalsIgnoreCase(nairaCurrency)) {
            accountName = bankService.validateAccountWithFlutterwave(walletTransferRequest.getToAccountNumber(), bank);
        } else {
            accountName = StringUtils.isBlank(walletTransferRequest.getToAccountName()) ? "FOREIGN ACCOUNT NUMBER" : walletTransferRequest.getToAccountName();
            if (org.springframework.util.StringUtils.isEmpty(walletTransferRequest.getSortCode())) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Sort code is required for non naira beneficiary");
            }
        }

        if (StringUtils.isBlank(accountName)) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Account not found");
        }

        KycResponseModel kycResponseModel = kycService.getUserKycFlagByCurrency(wallet.getCurrency().getType());

        if (kycResponseModel == null || (!kycResponseModel.getBvnKycStatus().equals(KycStatus.VERIFIED) && transferTransactionType.equals(TransferTransactionType.BUYING))  || !kycResponseModel.getPassportPhotoKycStatus().equals(KycStatus.VERIFIED) ||
                !kycResponseModel.getUtilityBillKycStatus().equals(KycStatus.VERIFIED) || !kycResponseModel.getValidIdKycStatus().equals(KycStatus.VERIFIED)) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.UNABLE_TO_PROCESS, "All uploaded KYC has not been verified");
        }

        walletTransferRequest.setToAccountName(accountName);

        walletTransferRequestResponse.setWallet(wallet);
        walletTransferRequestResponse.setBank(bank);
        walletTransferRequestResponse.setFromCurrencyDetails(fromCurrencyDetails);
        walletTransferRequestResponse.setToCurrencyDetails(toCurrencyDetails);
        walletTransferRequestResponse.setFromCurrency(fromCurrencyObject);
        walletTransferRequestResponse.setToCurrency(toCurrencyObject);
        walletTransferRequestResponse.setToCurrency(toCurrencyObject);
        walletTransferRequestResponse.setAmount(amount);
        walletTransferRequestResponse.setExchangeRate(userRate);
        walletTransferRequestResponse.setTransferTransactionType(transferTransactionType);
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        return walletTransferRequestResponse;

    }


    public WalletTransferRequestResponse validateRequestForChargesWithoutAccountDetails(WalletTransferRequest walletTransferRequest) throws IOException {

        CurrencyDetails fromCurrencyDetails = null;
        CurrencyDetails toCurrencyDetails = null;

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();

        String fromCurrency = walletTransferRequest.getFromCurrencyType();
        String toCurrency = walletTransferRequest.getToCurrencyType();

        TransferTransactionType transferTransactionType = null;

        BigDecimal amount = walletTransferRequest.getActualAmount();
        Double userRate = walletTransferRequest.getExchangeRate();

        if (walletTransferRequest == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request.");
        }

        if ( amount == null || amount.compareTo(BigDecimal.valueOf(0)) <= 0) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Amount must be greater than 0");
        }

        if ( userRate == null || userRate <= 0) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "User rate must be greater than 0");
        }

        if (fromCurrency.trim().equalsIgnoreCase(toCurrency.trim())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "From currency and to currency cannot be equal");
        }

        if (!fromCurrency.trim().equalsIgnoreCase(nairaCurrency.trim()) && !toCurrency.trim().equalsIgnoreCase(nairaCurrency.trim())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "From currency or to currency must be Naira");
        }


        Wallet wallet = walletService.getCurrentLoggedInUserWalletByCurrencyType(walletTransferRequest.getFromCurrencyType());

        if (wallet == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Wallet of the specified from currency type not found for user");
        }

        Currency fromCurrencyObject = wallet.getCurrency();

        Currency toCurrencyObject = currencyRepository.findCurrencyByType(toCurrency.trim());

        if (toCurrencyObject == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "To currency not found");
        }
        fromCurrencyDetails = currencyDetailsRepository.findByCurrencyCode(fromCurrency);
        toCurrencyDetails = currencyDetailsRepository.findByCurrencyCode(toCurrency);

        if (Strings.nullToEmpty(toCurrencyObject.getType()).trim().equalsIgnoreCase(nairaCurrency)) {
            transferTransactionType = TransferTransactionType.SELLING;
        }

        if (Strings.nullToEmpty(fromCurrencyObject.getType()).trim().equalsIgnoreCase(nairaCurrency)) {
            transferTransactionType = TransferTransactionType.BUYING;
        }


        if (fromCurrencyDetails == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, String.format("No currency set details set up for this currency %s", fromCurrency));
        }

        if (toCurrencyDetails == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, String.format("No currency set details set up for this currency %s", fromCurrency));
        }

        walletTransferRequestResponse.setWallet(wallet);
        walletTransferRequestResponse.setFromCurrencyDetails(fromCurrencyDetails);
        walletTransferRequestResponse.setToCurrencyDetails(toCurrencyDetails);
        walletTransferRequestResponse.setFromCurrency(fromCurrencyObject);
        walletTransferRequestResponse.setToCurrency(toCurrencyObject);
        walletTransferRequestResponse.setToCurrency(toCurrencyObject);
        walletTransferRequestResponse.setAmount(amount);
        walletTransferRequestResponse.setExchangeRate(userRate);
        walletTransferRequestResponse.setTransferTransactionType(transferTransactionType);
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        return walletTransferRequestResponse;

    }


    public WalletTransferRequestResponse validateMatchedTransaction(WalletTransferTransaction existingWalletTransferTransaction, WalletTransferRequest walletTransferRequest) {

        boolean isNairaHolder = false;

        Currency transactionFromCurrency = existingWalletTransferTransaction.getFromCurrency();
        Currency transactionToCurrency = existingWalletTransferTransaction.getToCurrency();

        if (StringUtils.isBlank(walletTransferRequest.getNarration() )) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Narration cannot be blank");
        }

        if (transactionFromCurrency == null || StringUtils.isBlank(transactionFromCurrency.getType())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "From Currency not found for the transaction");
        }

        if (transactionToCurrency == null || StringUtils.isBlank(transactionToCurrency.getType())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "To Currency not found for the transaction");
        }

        if (transactionFromCurrency.getType().equals(transactionToCurrency.getType())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "To and From Currency cannot be the same");
        }
        if (!transactionFromCurrency.getType().equals(nairaCurrency) && !transactionToCurrency.getType().equals(nairaCurrency) ) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "To or From Currency must be naira");
        }

        Bank bank = bankRepository.getOne(walletTransferRequest.getBankId());

        if (bank == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Bank not found");
        }

        if (bank.isDisabled()) return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Bank disabled");

        String accountName = null;
        if (existingWalletTransferTransaction.getFromCurrency().getType().equalsIgnoreCase(nairaCurrency)) {
            accountName = bankService.validateAccountWithFlutterwave(walletTransferRequest.getToAccountNumber(), bank);
        } else {
            accountName = StringUtils.isBlank(walletTransferRequest.getToAccountName()) ? "FOREIGN ACCOUNT NUMBER" : walletTransferRequest.getToAccountName();
        }

        if (StringUtils.isBlank(accountName)) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Account not found");
        }

        walletTransferRequest.setToAccountName(accountName);

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long userId = (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null) ? null : userDetailsTokenEnvelope.getManagedUser().getId();

        if (userId == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "User not logged in");
        }

        ManagedUser managedUser = managedUserRepository.getOne(userId);

        if (managedUser == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "User not found in repository");
        }



        isNairaHolder = (transactionToCurrency.getType().equals(nairaCurrency)) ? true : false;

        Wallet wallet = walletRepository.findByManagedUser_EmailAndCurrency_Type(managedUser.getEmail(), transactionToCurrency.getType());

        if (wallet == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, String.format("User does not have the wallet for currency %s", transactionToCurrency.getType()));
        }

        BigDecimal equivalentAmountForMatcher;

        equivalentAmountForMatcher = (isNairaHolder) ? existingWalletTransferTransaction.getTotalAmount().multiply(BigDecimal.valueOf(existingWalletTransferTransaction.getExchangeRate()))
                : existingWalletTransferTransaction.getTotalAmount().divide(BigDecimal.valueOf(existingWalletTransferTransaction.getExchangeRate()), 2, RoundingMode.HALF_EVEN);

//        if (wallet.getAvailableAccountBalance().compareTo(equivalentAmountForMatcher) < 0) {
//            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, String.format("Insufficient balance. You need %.2f of currency %s",equivalentAmountForMatcher, transactionToCurrency.getType()));
//        }

        CurrencyDetails fromCurrencyDetails = currencyDetailsRepository.findByCurrencyCode(transactionToCurrency.getType());
        CurrencyDetails toCurrencyDetails = currencyDetailsRepository.findByCurrencyCode(transactionFromCurrency.getType());

        if (fromCurrencyDetails == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, String.format("No currency details found for %s", transactionToCurrency.getType()));
        }
        if (toCurrencyDetails == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, String.format("No currency details found for %s", transactionFromCurrency.getType()));
        }

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setTotalAmount(equivalentAmountForMatcher);
        walletTransferRequestResponse.setFromCurrencyDetails(fromCurrencyDetails);
        walletTransferRequestResponse.setToCurrencyDetails(toCurrencyDetails);
        walletTransferRequestResponse.setExchangeRate(walletTransferRequest.getExchangeRate());
        walletTransferRequestResponse.setTransferTransactionType((isNairaHolder) ? TransferTransactionType.BUYING : TransferTransactionType.SELLING);
        walletTransferRequestResponse.setWallet(wallet);
        walletTransferRequestResponse.setBank(bank);
        walletTransferRequestResponse.setFromCurrency(transactionToCurrency);
        walletTransferRequestResponse.setToCurrency(transactionFromCurrency);
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);

        walletTransferRequest.setActualAmount(equivalentAmountForMatcher);
        walletTransferRequestResponse.setAmount(equivalentAmountForMatcher);

        return walletTransferRequestResponse;
    }


    public WalletTransferRequestResponse validateExistingTransactionForMatch(WalletTransferRequest walletTransferRequest) {

        if (walletTransferRequest == null || walletTransferRequest.getTransactionId() == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Transaction Id must be passed");
        }

        WalletTransferTransaction existingWalletTransferTransaction = walletTransferTransactionRepository.findTopByIdAndTransactionStatusIn(walletTransferRequest.getTransactionId(), GeneralUtil.getUnmatchedStatuses());

        if (existingWalletTransferTransaction == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Transaction not found");
        }

        if (existingWalletTransferTransaction.getReceivingBank() == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Receiving bank found");
        }

        Currency transactionFromCurrency = existingWalletTransferTransaction.getFromCurrency();
        Currency transactionToCurrency = existingWalletTransferTransaction.getToCurrency();

        if (transactionFromCurrency == null || StringUtils.isBlank(transactionFromCurrency.getType())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "From Currency not found for the transaction");
        }

        if (transactionToCurrency == null || StringUtils.isBlank(transactionToCurrency.getType())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "To Currency not found for the transaction");
        }

        if (transactionFromCurrency.getType().equals(transactionToCurrency.getType())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "To and From Currency cannot be the same");
        }
        if (!transactionFromCurrency.getType().equals(nairaCurrency) && !transactionToCurrency.getType().equals(nairaCurrency) ) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "To or From Currency must be naira");
        }


        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long userId = (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null) ? null : userDetailsTokenEnvelope.getManagedUser().getId();

        if (userId == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "User not logged in");
        }

        Wallet wallet = walletRepository.findByManagedUser_IdAndCurrency_Type(userId, transactionToCurrency.getType());


        if (wallet == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, String.format("User does not have the wallet for currency %s", transactionToCurrency.getType()));
        }
        CurrencyDetails fromCurrencyDetails = currencyDetailsRepository.findByCurrencyCode(transactionToCurrency.getType());
        CurrencyDetails toCurrencyDetails = currencyDetailsRepository.findByCurrencyCode(transactionFromCurrency.getType());

        if (fromCurrencyDetails == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, String.format("No currency details found for %s", transactionToCurrency.getType()));
        }
        if (toCurrencyDetails == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, String.format("No currency details found for %s", transactionFromCurrency.getType()));
        }

        Bank bank = bankRepository.getOne(existingWalletTransferTransaction.getReceivingBank().getId());

        if (bank == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Bank not found");
        }

        if (bank.isDisabled()) return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Bank dsabled");

        boolean isNairaHolder;

        isNairaHolder = (transactionToCurrency.getType().equals(nairaCurrency)) ? true : false;

        BigDecimal equivalentAmountForMatcher;

        equivalentAmountForMatcher = (isNairaHolder) ? existingWalletTransferTransaction.getTotalAmount().multiply(BigDecimal.valueOf(existingWalletTransferTransaction.getExchangeRate()))
                : existingWalletTransferTransaction.getTotalAmount().divide(BigDecimal.valueOf(existingWalletTransferTransaction.getExchangeRate()), 2, RoundingMode.HALF_EVEN);


        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setExistingTransaction(existingWalletTransferTransaction);
        walletTransferRequestResponse.setTotalAmount(equivalentAmountForMatcher);
        walletTransferRequestResponse.setExchangeRate(walletTransferRequest.getExchangeRate());
        walletTransferRequestResponse.setTransferTransactionType((isNairaHolder) ? TransferTransactionType.BUYING : TransferTransactionType.SELLING);
        walletTransferRequestResponse.setWallet(wallet);
        walletTransferRequestResponse.setFromCurrencyDetails(fromCurrencyDetails);
        walletTransferRequestResponse.setToCurrencyDetails(toCurrencyDetails);
        walletTransferRequestResponse.setBank(bank);
        walletTransferRequestResponse.setFromCurrency(transactionToCurrency);
        walletTransferRequestResponse.setToCurrency(transactionFromCurrency);
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);

        walletTransferRequest.setActualAmount(equivalentAmountForMatcher);
        walletTransferRequestResponse.setAmount(equivalentAmountForMatcher);

        return walletTransferRequestResponse;
    }

}
