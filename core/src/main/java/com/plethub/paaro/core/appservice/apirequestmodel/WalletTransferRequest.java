package com.plethub.paaro.core.appservice.apirequestmodel;



import com.plethub.paaro.core.appservice.enums.TransactionStatus;
import com.plethub.paaro.core.models.Bank;
import com.plethub.paaro.core.models.Currency;
import com.plethub.paaro.core.models.Wallet;

import java.math.BigDecimal;

public class WalletTransferRequest {

    private String email;

    private String paaroTransactionReferenceId;

    private TransactionStatus transactionStatus;

    private String narration;

    private BigDecimal actualAmount;

    private String fromCurrencyType;

    private String toCurrencyType;

    private String toAccountNumber;

    private String toAccountName;

    private long bankId;

    private Long beneficiaryId;

    private boolean sendToken = false;

    private Bank bank;

    private Double exchangeRate;

    private Currency fromCurrency;

    private Currency toCurrency;

    private Wallet wallet;

    private String currency;

    private String filter;

    private String token;

    private String sortCode;

    public BigDecimal getFromAmount() {
        return fromAmount;
    }

    public void setFromAmount(BigDecimal fromAmount) {
        this.fromAmount = fromAmount;
    }

    public BigDecimal getToAmount() {
        return toAmount;
    }

    public void setToAmount(BigDecimal toAmount) {
        this.toAmount = toAmount;
    }

    private int pageNo = 0;

    private int pageSize = 10;

    private BigDecimal fromAmount;

    private BigDecimal toAmount;

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    private Long transactionId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaaroTransactionReferenceId() {
        return paaroTransactionReferenceId;
    }

    public void setPaaroTransactionReferenceId(String paaroTransactionReferenceId) {
        this.paaroTransactionReferenceId = paaroTransactionReferenceId;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }


    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getFromCurrencyType() {
        return fromCurrencyType;
    }

    public void setFromCurrencyType(String fromCurrencyType) {
        this.fromCurrencyType = fromCurrencyType;
    }

    public String getToCurrencyType() {
        return toCurrencyType;
    }

    public void setToCurrencyType(String toCurrencyType) {
        this.toCurrencyType = toCurrencyType;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
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

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getToAccountName() {
        return toAccountName;
    }

    public void setToAccountName(String toAccountName) {
        this.toAccountName = toAccountName;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getBankId() {
        return bankId;
    }

    public void setBankId(long bankId) {
        this.bankId = bankId;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public boolean isSendToken() {
        return sendToken;
    }

    public void setSendToken(boolean sendToken) {
        this.sendToken = sendToken;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }
}
