package com.plethub.paaro.core.appservice.enums;

import java.util.Arrays;
import java.util.List;

public enum TransactionStatus {

    PENDING,
    COMPLETED,
    SUCCESSFUL,
    INITIATOR_LOGGED_REQUEST,
    ADMIN_VERIFIED_LOGGED_REQUEST,
    FAILED,
    WALLET_FUNDING_FAILED,
    CUSTOMER_LOGGED_REQUEST,
    SYSTEM_MAPPED_REQUEST,
    CUSTOMER_MAPPED_REQUEST,
    AWAITING_BANK_PAYMENT_VERIFICATION,
    BID_EXPIRED,
    BID_CANCELLED,
    BID_CANCELLING_REFUND
    ;

    public static List<TransactionStatus> getTransactionStatus(){
        return Arrays.asList(TransactionStatus.values());
    }

}
