package com.plethub.paaro.core.models;

import com.plethub.paaro.core.appservice.enums.DepositStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class BankDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String tellerNumber;

    @OneToOne
    @NotNull
    private PaymentReference paymentReference;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private DepositStatus transactionStatus;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Date loggedDate;

    private Date verificationOrDeclinedDate;

    @OneToOne
    private ManagedUser verfifiedBy;

    @OneToOne
    private ManagedUser verificationInitiatedBy;

    @OneToOne
    private ManagedUser declinedBy;

    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTellerNumber() {
        return tellerNumber;
    }

    public void setTellerNumber(String tellerNumber) {
        this.tellerNumber = tellerNumber;
    }

    public PaymentReference getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(PaymentReference paymentReference) {
        this.paymentReference = paymentReference;
    }

    public DepositStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(DepositStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getLoggedDate() {
        return loggedDate;
    }

    public void setLoggedDate(Date loggedDate) {
        this.loggedDate = loggedDate;
    }

    public Date getVerificationOrDeclinedDate() {
        return verificationOrDeclinedDate;
    }

    public void setVerificationOrDeclinedDate(Date verificationOrDeclinedDate) {
        this.verificationOrDeclinedDate = verificationOrDeclinedDate;
    }

    public ManagedUser getVerfifiedBy() {
        return verfifiedBy;
    }

    public void setVerfifiedBy(ManagedUser verfifiedBy) {
        this.verfifiedBy = verfifiedBy;
    }

    public ManagedUser getVerificationInitiatedBy() {
        return verificationInitiatedBy;
    }

    public void setVerificationInitiatedBy(ManagedUser verificationInitiatedBy) {
        this.verificationInitiatedBy = verificationInitiatedBy;
    }

    public ManagedUser getDeclinedBy() {
        return declinedBy;
    }

    public void setDeclinedBy(ManagedUser declinedBy) {
        this.declinedBy = declinedBy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
