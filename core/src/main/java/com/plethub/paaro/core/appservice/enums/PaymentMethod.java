package com.plethub.paaro.core.appservice.enums;

import java.util.Arrays;
import java.util.List;

public enum  PaymentMethod {

    ONLINE_PAYMENT,
    BANK_DEPOSIT;

    public static List<PaymentMethod> getPaymentMethods(){
        return Arrays.asList(PaymentMethod.values());
    }


}
