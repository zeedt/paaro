package com.plethub.paaro.core.appservice.apirequestmodel;

import com.plethub.paaro.core.appservice.enums.PaymentMethod;
import com.plethub.paaro.core.appservice.enums.TransactionStatus;

import java.math.BigDecimal;
import java.util.Date;

public class WalletTransactionSearchRequestModel {

    private String accountName;

    private String accountNumber;

    private String paaroReferenceId;

    private TransactionStatus transactionStatus;

    private Date fromDate;

    private Date toDate;

    private BigDecimal actualAmount;

    private String currency;

    private Integer pageNo;

    private Integer pageSize;

    private Long id;

    private Long userId;

    private Boolean isSettled;

    private PaymentMethod paymentMethod;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPaaroReferenceId() {
        return paaroReferenceId;
    }

    public void setPaaroReferenceId(String paaroReferenceId) {
        this.paaroReferenceId = paaroReferenceId;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }


    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getSettled() {
        return isSettled;
    }

    public void setSettled(Boolean settled) {
        isSettled = settled;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public WalletTransactionSearchRequestModel() {
    }

    public WalletTransactionSearchRequestModel(String paaroReferenceId, TransactionStatus transactionStatus, Date fromDate, Date toDate, String currency, Long userId, Boolean isSettled, PaymentMethod paymentMethod) {
        this.paaroReferenceId = paaroReferenceId;
        this.transactionStatus = transactionStatus;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.currency = currency;
        this.userId = userId;
        this.isSettled = isSettled;
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "WalletTransactionSearchRequestModel{" +
                "accountName='" + accountName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", paaroReferenceId='" + paaroReferenceId + '\'' +
                ", transactionStatus=" + transactionStatus +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", actualAmount=" + actualAmount +
                ", currency='" + currency + '\'' +
                ", pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", id=" + id +
                ", userId=" + userId +
                ", isSettled=" + isSettled +
                ", paymentMethod=" + paymentMethod +
                '}';
    }
}
