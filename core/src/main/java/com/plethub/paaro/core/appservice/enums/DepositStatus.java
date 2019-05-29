package com.plethub.paaro.core.appservice.enums;

import java.util.Arrays;
import java.util.List;

public enum DepositStatus {

    PAYMENT_AWAITING_VERIFICATION,
    PAYMENT_VERIFIED,
    PAYMENT_DECLINED;

    public static List<DepositStatus> getDepositStatus(){
        return Arrays.asList(DepositStatus.values());
    }

}
