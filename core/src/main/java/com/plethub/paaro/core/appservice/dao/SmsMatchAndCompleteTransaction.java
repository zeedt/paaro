package com.plethub.paaro.core.appservice.dao;

import com.plethub.paaro.core.models.WalletTransferTransaction;

public interface SmsMatchAndCompleteTransaction {

    default void sendNotificationForCompletedTransactions(WalletTransferTransaction firstTransaction, WalletTransferTransaction secondTransaction) {
        throw new UnsupportedOperationException("Method not supported");
    };

    default void sendNotificationForMatchedTransaction(WalletTransferTransaction firstTransaction, WalletTransferTransaction secondTransaction) {
        throw new UnsupportedOperationException("Method not supported");
    };

}
