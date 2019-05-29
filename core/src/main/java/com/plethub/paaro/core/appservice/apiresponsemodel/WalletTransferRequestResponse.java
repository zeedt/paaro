package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.WalletStatus;
import com.plethub.paaro.core.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletTransferRequestResponse {

    private String message;

    private BigDecimal charges;

    private Double chargeRate;

    private Double rate;

    private BigDecimal equivalentAmount;

    private BigDecimal totalAmount;

    private BigDecimal amount;

    private Double exchangeRate;

    private ApiResponseCode responseStatus;

    private WalletStatus walletStatus;

    private WalletTransferTransaction existingTransaction;

    @JsonIgnoreProperties(value = {"walletList","id"},allowSetters = true)
    private Currency fromCurrency;

    @JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
    private Bank bank;

    @JsonIgnoreProperties(value = {"walletList","id"},allowSetters = true)
    private Currency toCurrency;

    private TransferTransactionType transferTransactionType;

    private CurrencyDetails fromCurrencyDetails;

    private CurrencyDetails toCurrencyDetails;

    private TransferSummary transferSummary;

    private boolean closeMatch;

    public TransferSummary getTransferSummary() {
        return transferSummary;
    }

    public void setTransferSummary(TransferSummary transferSummary) {
        this.transferSummary = transferSummary;
    }

    private List<WalletTransferTransaction> walletTransferTransactions = new ArrayList<>();

    private Page<WalletTransferTransaction> walletTransferTransactionPage = new PageImpl<>(new ArrayList<>());

    private Page<WalletTransferTransaction> exactMatchTransactionPage = new PageImpl<>(new ArrayList<>());

    private Page<WalletTransferTransaction> closeMatchTransferTransactionPage = new PageImpl<>(new ArrayList<>());

    private WalletTransferTransaction walletTransferTransaction = new WalletTransferTransaction();

    @JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
    private Wallet wallet;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApiResponseCode getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ApiResponseCode responseStatus) {
        this.responseStatus = responseStatus;
    }

    public List<WalletTransferTransaction> getWalletTransferTransactions() {
        return walletTransferTransactions;
    }

    public void setWalletTransferTransactions(List<WalletTransferTransaction> walletTransferTransactions) {
        this.walletTransferTransactions = walletTransferTransactions;
    }

    public WalletTransferTransaction getWalletTransferTransaction() {
        return walletTransferTransaction;
    }

    public void setWalletTransferTransaction(WalletTransferTransaction walletTransferTransaction) {
        this.walletTransferTransaction = walletTransferTransaction;
    }

    public static WalletTransferRequestResponse returnResponseWithCode(ApiResponseCode responseStatus, String message) {
        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setMessage(message);
        walletTransferRequestResponse.setResponseStatus(responseStatus);
        return walletTransferRequestResponse;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Page<WalletTransferTransaction> getWalletTransferTransactionPage() {
        return walletTransferTransactionPage;
    }

    public void setWalletTransferTransactionPage(Page<WalletTransferTransaction> walletTransferTransactionPage) {
        this.walletTransferTransactionPage = walletTransferTransactionPage;
    }

    public BigDecimal getCharges() {
        return charges;
    }

    public void setCharges(BigDecimal charges) {
        this.charges = charges;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public BigDecimal getEquivalentAmount() {
        return equivalentAmount;
    }

    public void setEquivalentAmount(BigDecimal equivalentAmount) {
        this.equivalentAmount = equivalentAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Double getChargeRate() {
        return chargeRate;
    }

    public void setChargeRate(Double chargeRate) {
        this.chargeRate = chargeRate;
    }

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(Currency fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(Currency toCurrency) {
        this.toCurrency = toCurrency;
    }

    public TransferTransactionType getTransferTransactionType() {
        return transferTransactionType;
    }

    public void setTransferTransactionType(TransferTransactionType transferTransactionType) {
        this.transferTransactionType = transferTransactionType;
    }

    public CurrencyDetails getFromCurrencyDetails() {
        return fromCurrencyDetails;
    }

    public void setFromCurrencyDetails(CurrencyDetails fromCurrencyDetails) {
        this.fromCurrencyDetails = fromCurrencyDetails;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public WalletStatus getWalletStatus() {
        return walletStatus;
    }

    public void setWalletStatus(WalletStatus walletStatus) {
        this.walletStatus = walletStatus;
    }

    public WalletTransferTransaction getExistingTransaction() {
        return existingTransaction;
    }

    public void setExistingTransaction(WalletTransferTransaction existingTransaction) {
        this.existingTransaction = existingTransaction;
    }

    public CurrencyDetails getToCurrencyDetails() {
        return toCurrencyDetails;
    }

    public void setToCurrencyDetails(CurrencyDetails toCurrencyDetails) {
        this.toCurrencyDetails = toCurrencyDetails;
    }

    public boolean isCloseMatch() {
        return closeMatch;
    }

    public void setCloseMatch(boolean closeMatch) {
        this.closeMatch = closeMatch;
    }

    public Page<WalletTransferTransaction> getExactMatchTransactionPage() {
        return exactMatchTransactionPage;
    }

    public void setExactMatchTransactionPage(Page<WalletTransferTransaction> exactMatchTransactionPage) {
        this.exactMatchTransactionPage = exactMatchTransactionPage;
    }

    public Page<WalletTransferTransaction> getCloseMatchTransferTransactionPage() {
        return closeMatchTransferTransactionPage;
    }

    public void setCloseMatchTransferTransactionPage(Page<WalletTransferTransaction> closeMatchTransferTransactionPage) {
        this.closeMatchTransferTransactionPage = closeMatchTransferTransactionPage;
    }
}
