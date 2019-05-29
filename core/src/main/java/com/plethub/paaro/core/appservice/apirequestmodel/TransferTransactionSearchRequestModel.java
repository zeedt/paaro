package com.plethub.paaro.core.appservice.apirequestmodel;

import com.plethub.paaro.core.appservice.enums.TransactionStatus;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TransferTransactionSearchRequestModel {

    private String accountName;

    private String accountNumber;

    private String paaroReferenceId;

    private BigDecimal actualAmount;

    private TransactionStatus transactionStatus;

    private Date fromDate;

    private Date toDate;

    private String fromCurrency;

    private String toCurrency;

    private Integer pageNo;

    private Integer pageSize;

    private Long id;

    private Long userId;

    private String email;

    private Boolean isSettled;

    private Boolean isMapped;

    private BigDecimal totalAmount;

    private List<TransactionStatus> statuses;

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

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
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

    public Boolean getMapped() {
        return isMapped;
    }

    public void setMapped(Boolean mapped) {
        isMapped = mapped;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TransferTransactionSearchRequestModel() {
    }

    public TransferTransactionSearchRequestModel(String accountName, String accountNumber, String paaroReferenceId, BigDecimal actualAmount, TransactionStatus transactionStatus, Date fromDate, Date toDate, String fromCurrency, String toCurrency, String email, Boolean isSettled, Boolean isMapped, Long userId) {
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.paaroReferenceId = paaroReferenceId;
        this.actualAmount = actualAmount;
        this.transactionStatus = transactionStatus;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.email = email;
        this.isSettled = isSettled;
        this.isMapped = isMapped;
        this.userId = userId;
    }

    public TransferTransactionSearchRequestModel(List<TransactionStatus> statuses) {
        this.statuses = statuses;
    }

    public List<TransactionStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<TransactionStatus> statuses) {
        this.statuses = statuses;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
