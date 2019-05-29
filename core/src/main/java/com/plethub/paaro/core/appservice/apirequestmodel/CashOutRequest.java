package com.plethub.paaro.core.appservice.apirequestmodel;

import com.plethub.paaro.core.appservice.enums.CashOutStatus;
import com.plethub.paaro.core.models.Wallet;

import java.math.BigDecimal;
import java.util.Date;

public class CashOutRequest {

    private Long walletId;

    private BigDecimal amount;

    private String narration;

    private Long cashOutLogId;

    private String firstName;

    private String lastName;

    private String currencyType;

    private Date fromDate;

    private Date toDate;

    private Integer pageNo;

    private Integer pageSize;

    private String email;

    private CashOutStatus cashOutStatus;

    private String accountName;

    private String accountNumber;

    private Long bankId;

    public CashOutRequest() {
    }

    public CashOutRequest(Long walletId, String firstName, String lastName, String currencyType, Date fromDate, Date toDate, String email, CashOutStatus cashOutStatus) {
        this.walletId = walletId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.currencyType = currencyType;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.email = email;
        this.cashOutStatus = cashOutStatus;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public Long getCashOutLogId() {
        return cashOutLogId;
    }

    public void setCashOutLogId(Long cashOutLogId) {
        this.cashOutLogId = cashOutLogId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CashOutStatus getCashOutStatus() {
        return cashOutStatus;
    }

    public void setCashOutStatus(CashOutStatus cashOutStatus) {
        this.cashOutStatus = cashOutStatus;
    }

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

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }
}
