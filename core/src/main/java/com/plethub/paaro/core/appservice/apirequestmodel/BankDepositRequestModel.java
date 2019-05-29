package com.plethub.paaro.core.appservice.apirequestmodel;

import com.plethub.paaro.core.appservice.enums.DepositStatus;

import java.math.BigDecimal;

public class BankDepositRequestModel {

    private String currencyType;

    private BigDecimal amount;

    private String tellerNumber;

    private String paymentReferenceNo;

    private Long paymentReferenceId;

    private Integer pageNo = 0;

    private Integer pageSize = 15;

    private DepositStatus depositStatus;

    private String userEmail;

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTellerNumber() {
        return tellerNumber;
    }

    public void setTellerNumber(String tellerNumber) {
        this.tellerNumber = tellerNumber;
    }

    public Long getPaymentReferenceId() {
        return paymentReferenceId;
    }

    public void setPaymentReferenceId(Long paymentReferenceId) {
        this.paymentReferenceId = paymentReferenceId;
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

    public DepositStatus getDepositStatus() {
        return depositStatus;
    }

    public void setDepositStatus(DepositStatus depositStatus) {
        this.depositStatus = depositStatus;
    }

    public String getPaymentReferenceNo() {
        return paymentReferenceNo;
    }

    public void setPaymentReferenceNo(String paymentReferenceNo) {
        this.paymentReferenceNo = paymentReferenceNo;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public BankDepositRequestModel() {
    }

    public BankDepositRequestModel(String currencyType, BigDecimal amount, String tellerNumber, String paymentReferenceNo, Long paymentReferenceId, DepositStatus depositStatus, String userEmail) {
        this.currencyType = currencyType;
        this.amount = amount;
        this.tellerNumber = tellerNumber;
        this.paymentReferenceNo = paymentReferenceNo;
        this.paymentReferenceId = paymentReferenceId;
        this.depositStatus = depositStatus;
        this.userEmail = userEmail;
    }
}
