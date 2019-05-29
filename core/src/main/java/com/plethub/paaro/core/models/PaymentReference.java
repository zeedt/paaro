package com.plethub.paaro.core.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "payment_reference")
public class PaymentReference implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String paymentReferenceNumber;

    @NotNull
    @OneToOne
    private Wallet wallet;

    @NotNull
    private Date dateGenerated;

    @NotNull
    @OneToOne
    private PaaroBankAccount paaroBankAccount;

    private boolean paymentVerified = false;

    private boolean userDeposited = false;

    private boolean userCanDeposit = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentReferenceNumber() {
        return paymentReferenceNumber;
    }

    public void setPaymentReferenceNumber(String paymentReferenceNumber) {
        this.paymentReferenceNumber = paymentReferenceNumber;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Date getDateGenerated() {
        return dateGenerated;
    }

    public void setDateGenerated(Date dateGenerated) {
        this.dateGenerated = dateGenerated;
    }

    public boolean isPaymentVerified() {
        return paymentVerified;
    }

    public void setPaymentVerified(boolean paymentVerified) {
        this.paymentVerified = paymentVerified;
    }

    public PaaroBankAccount getPaaroBankAccount() {
        return paaroBankAccount;
    }

    public void setPaaroBankAccount(PaaroBankAccount paaroBankAccount) {
        this.paaroBankAccount = paaroBankAccount;
    }

    public boolean isUserDeposited() {
        return userDeposited;
    }

    public void setUserDeposited(boolean userDeposited) {
        this.userDeposited = userDeposited;
    }

    public boolean isUserCanDeposit() {
        return userCanDeposit;
    }

    public void setUserCanDeposit(boolean userCanDeposit) {
        this.userCanDeposit = userCanDeposit;
    }
}
