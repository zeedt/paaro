package com.plethub.paaro.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class TransferRequestMap {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnoreProperties(value = {"transferRequestMap"})
    @OneToOne
    private WalletTransferTransaction nairaHolderTransaction;

    @JsonIgnoreProperties(value = {"transferRequestMap"})
    @OneToOne
    private WalletTransferTransaction otherCurrencyHolderTransaction;

    @NotNull
    private Date dateMapped;

    private Date dateSettled;

    private boolean settled = false;

    public WalletTransferTransaction getNairaHolderTransaction() {
        return nairaHolderTransaction;
    }

    public void setNairaHolderTransaction(WalletTransferTransaction nairaHolderTransaction) {
        this.nairaHolderTransaction = nairaHolderTransaction;
    }

    public WalletTransferTransaction getOtherCurrencyHolderTransaction() {
        return otherCurrencyHolderTransaction;
    }

    public void setOtherCurrencyHolderTransaction(WalletTransferTransaction otherCurrencyHolderTransaction) {
        this.otherCurrencyHolderTransaction = otherCurrencyHolderTransaction;
    }

    public Date getDateMapped() {
        return dateMapped;
    }

    public void setDateMapped(Date dateMapped) {
        this.dateMapped = dateMapped;
    }

    public Date getDateSettled() {
        return dateSettled;
    }

    public void setDateSettled(Date dateSettled) {
        this.dateSettled = dateSettled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isSettled() {
        return settled;
    }

    public void setSettled(boolean settled) {
        this.settled = settled;
    }
}
