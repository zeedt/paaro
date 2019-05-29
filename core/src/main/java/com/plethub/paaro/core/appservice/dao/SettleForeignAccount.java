package com.plethub.paaro.core.appservice.dao;

import com.plethub.paaro.core.models.WalletTransferTransaction;

public interface SettleForeignAccount {

    default void settleForeignAccount(WalletTransferTransaction walletTransferTransaction) {
        throw new UnsupportedOperationException("Implementation is compulsory");
    }

}
