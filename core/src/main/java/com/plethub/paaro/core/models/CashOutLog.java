package com.plethub.paaro.core.models;

import com.plethub.paaro.core.appservice.enums.CashOutStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class CashOutLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @NotNull
    private Wallet wallet;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Date requestDate;

    private Date verifiedDate;

    private String accountName;

    private String accountNumber;

    private Long bankId;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private CashOutStatus cashOutStatus;

    private String customerNarration;

    private String adminNarration;

    @OneToOne
    private ManagedUser verifiedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getVerifiedDate() {
        return verifiedDate;
    }

    public void setVerifiedDate(Date verifiedDate) {
        this.verifiedDate = verifiedDate;
    }

    public CashOutStatus getCashOutStatus() {
        return cashOutStatus;
    }

    public void setCashOutStatus(CashOutStatus cashOutStatus) {
        this.cashOutStatus = cashOutStatus;
    }

    public String getCustomerNarration() {
        return customerNarration;
    }

    public void setCustomerNarration(String customerNarration) {
        this.customerNarration = customerNarration;
    }

    public ManagedUser getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(ManagedUser verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public String getAdminNarration() {
        return adminNarration;
    }

    public void setAdminNarration(String adminNarration) {
        this.adminNarration = adminNarration;
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
