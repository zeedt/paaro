package com.plethub.paaro.core.models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("wallet_transfer")
public class WalletTransferTransaction extends Transaction {

    @Transient
    public boolean mapped = false;

}
