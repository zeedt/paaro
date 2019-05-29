package com.plethub.paaro.core.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class Wallet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Currency currency;

    @NotNull
    private BigDecimal availableAccountBalance = BigDecimal.valueOf(0);

    @NotNull
    private BigDecimal ledgerAccountBalance = BigDecimal.valueOf(0);

    private boolean isActive = true;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties(value = {"password","base64Image","hibernateLazyInitializer", "handler"})
    private ManagedUser managedUser;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getAvailableAccountBalance() {
        return availableAccountBalance;
    }

    public void setAvailableAccountBalance(BigDecimal availableAccountBalance) {
        this.availableAccountBalance = availableAccountBalance;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public BigDecimal getLedgerAccountBalance() {
        return ledgerAccountBalance;
    }

    public void setLedgerAccountBalance(BigDecimal ledgerAccountBalance) {
        this.ledgerAccountBalance = ledgerAccountBalance;
    }

    public ManagedUser getManagedUser() {
        return managedUser;
    }

    public void setManagedUser(ManagedUser managedUser) {
        this.managedUser = managedUser;
    }
}
