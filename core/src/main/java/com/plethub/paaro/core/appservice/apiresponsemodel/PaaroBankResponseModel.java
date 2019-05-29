package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.Currency;
import com.plethub.paaro.core.models.PaaroBankAccount;

import java.util.List;

public class PaaroBankResponseModel {

    private PaaroBankAccount paaroBankAccount;

    private ApiResponseCode apiResponseCode;

    private String message;

    private List<PaaroBankAccount> paaroBankAccounts;

    private Currency currency;


    public PaaroBankAccount getPaaroBankAccount() {
        return paaroBankAccount;
    }

    public void setPaaroBankAccount(PaaroBankAccount paaroBankAccount) {
        this.paaroBankAccount = paaroBankAccount;
    }

    public ApiResponseCode getApiResponseCode() {
        return apiResponseCode;
    }

    public void setApiResponseCode(ApiResponseCode apiResponseCode) {
        this.apiResponseCode = apiResponseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public static PaaroBankResponseModel fromNarration(ApiResponseCode apiResponseCode, String message) {
        PaaroBankResponseModel paaroBankResponseModel = new PaaroBankResponseModel();
        paaroBankResponseModel.setMessage(message);
        paaroBankResponseModel.setApiResponseCode(apiResponseCode);

        return paaroBankResponseModel;
    }

    public List<PaaroBankAccount> getPaaroBankAccounts() {
        return paaroBankAccounts;
    }

    public void setPaaroBankAccounts(List<PaaroBankAccount> paaroBankAccounts) {
        this.paaroBankAccounts = paaroBankAccounts;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
