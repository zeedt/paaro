package com.plethub.paaro.core.appservice.dao;

import com.plethub.paaro.core.models.WalletTransferTransaction;

public interface SettleNairaAccount {

    default void settleNairaAccount (WalletTransferTransaction walletTransferTransaction) {
        throw new UnsupportedOperationException("Implementation is compulsory");
    }

}
