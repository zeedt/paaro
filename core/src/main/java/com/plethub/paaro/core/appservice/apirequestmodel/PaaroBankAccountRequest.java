package com.plethub.paaro.core.appservice.apirequestmodel;

import com.plethub.paaro.core.models.PaaroBankAccount;

public class PaaroBankAccountRequest extends PaaroBankAccount {

    private String currencyType;

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }
}
