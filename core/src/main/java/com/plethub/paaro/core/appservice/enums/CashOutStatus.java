package com.plethub.paaro.core.appservice.enums;

import java.util.Arrays;
import java.util.List;

public enum CashOutStatus {

    DECLINED,
    VERIFIED,
    PENDING;
    public static List<CashOutStatus> getCashOutStatus(){
        return Arrays.asList(CashOutStatus.values());
    }


}
