package com.plethub.paaro.core.appservice.apirequestmodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KycRequestModel {

    private String bvnOrIdentificationNumber;

    private String base64file;

    private String currencyType;

    public String getBvnOrIdentificationNumber() {
        return bvnOrIdentificationNumber;
    }

    public void setBvnOrIdentificationNumber(String bvnOrIdentificationNumber) {
        this.bvnOrIdentificationNumber = bvnOrIdentificationNumber;
    }

    public String getBase64file() {
        return base64file;
    }

    public void setBase64file(String base64file) {
        this.base64file = base64file;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }
}
