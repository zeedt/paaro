package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.BeneficiaryAccount;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeneficiaryAccountResponseModel {

    private ApiResponseCode responseCode;

    private String message;

    private List<BeneficiaryAccount> beneficiaryAccountList;

    private BeneficiaryAccount beneficiaryAccount;

    public static BeneficiaryAccountResponseModel fromNarration(ApiResponseCode responseCode, String message) {
        BeneficiaryAccountResponseModel beneficiaryAccountResponseModel = new BeneficiaryAccountResponseModel();
        beneficiaryAccountResponseModel.setMessage(message);
        beneficiaryAccountResponseModel.setResponseCode(responseCode);

        return beneficiaryAccountResponseModel;
    }

    public ApiResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ApiResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<BeneficiaryAccount> getBeneficiaryAccountList() {
        return beneficiaryAccountList;
    }

    public void setBeneficiaryAccountList(List<BeneficiaryAccount> beneficiaryAccountList) {
        this.beneficiaryAccountList = beneficiaryAccountList;
    }

    public BeneficiaryAccount getBeneficiaryAccount() {
        return beneficiaryAccount;
    }

    public void setBeneficiaryAccount(BeneficiaryAccount beneficiaryAccount) {
        this.beneficiaryAccount = beneficiaryAccount;
    }
}
