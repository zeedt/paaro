package com.plethub.paaro.core.models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("wallet_fund")
public class WalletFundingTransaction extends Transaction {

}
