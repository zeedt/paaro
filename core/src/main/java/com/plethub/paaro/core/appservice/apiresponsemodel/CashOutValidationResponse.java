package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.plethub.paaro.core.models.Wallet;

public class CashOutValidationResponse {

    private String errorMessage;

    private Wallet wallet;

    public CashOutValidationResponse(String errorMessage, Wallet wallet) {
        this.errorMessage = errorMessage;
        this.wallet = wallet;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}
