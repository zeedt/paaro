package com.plethub.paaro.core.appservice.apirequestmodel;



import com.plethub.paaro.core.appservice.enums.PaymentMethod;
import com.plethub.paaro.core.appservice.enums.TransactionStatus;
import com.plethub.paaro.core.models.Currency;
import com.plethub.paaro.core.models.PaymentReference;
import com.plethub.paaro.core.models.Wallet;

import java.math.BigDecimal;

public class WalletRequest {

    private String currencyType;

    private String email;

    private String paaroTransactionReferenceId;

    private String thirdPartyTransactionId;

    private TransactionStatus transactionStatus;

    private String narration;

    private BigDecimal chargeAmount;

    private BigDecimal totalAmount;

    private BigDecimal actualAmount;

    private BigDecimal equivalentAmount;

    private String fromCurrencyType;

    private String toCurrencyType;

    private String fromAccountNumber;

    private BigDecimal exchangeRate;

    private Currency currency;

    private Wallet wallet;

    private PaymentReference paymentReference;

    private Long transactionId;

    private int pageNo = 0;

    private int pageSize = 10;

    private String filter;

    private PaymentMethod paymentMethod;

    private Long paymentReferenceId;

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

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

    public String getThirdPartyTransactionId() {
        return thirdPartyTransactionId;
    }

    public void setThirdPartyTransactionId(String thirdPartyTransactionId) {
        this.thirdPartyTransactionId = thirdPartyTransactionId;
    }

    public TransactionStatus getTransactionStatus() {
        return this.transactionStatus;
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

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public BigDecimal getEquivalentAmount() {
        return equivalentAmount;
    }

    public void setEquivalentAmount(BigDecimal equivalentAmount) {
        this.equivalentAmount = equivalentAmount;
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

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Long getPaymentReferenceId() {
        return paymentReferenceId;
    }

    public void setPaymentReferenceId(Long paymentReferenceId) {
        this.paymentReferenceId = paymentReferenceId;
    }

    public PaymentReference getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(PaymentReference paymentReference) {
        this.paymentReference = paymentReference;
    }
}
