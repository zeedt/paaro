package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.CurrencyDetails;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyDetailsresponse {

    private ApiResponseCode apiResponseCode;

    private String message;

    private BigDecimal gbpAverageExchangeRate;

    List<CurrencyDetails> currencyDetailsList;

    CurrencyDetails currencyDetails;

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

    public List<CurrencyDetails> getCurrencyDetailsList() {
        return currencyDetailsList;
    }

    public void setCurrencyDetailsList(List<CurrencyDetails> currencyDetailsList) {
        this.currencyDetailsList = currencyDetailsList;
    }

    public CurrencyDetails getCurrencyDetails() {
        return currencyDetails;
    }

    public void setCurrencyDetails(CurrencyDetails currencyDetails) {
        this.currencyDetails = currencyDetails;
    }


    public static CurrencyDetailsresponse fromNarration(ApiResponseCode responseCode, String message) {

        CurrencyDetailsresponse currencyDetailsresponse = new CurrencyDetailsresponse();
        currencyDetailsresponse.setApiResponseCode(responseCode);
        currencyDetailsresponse.setMessage(message);

        return currencyDetailsresponse;

    }

    public BigDecimal getGbpAverageExchangeRate() {
        return gbpAverageExchangeRate;
    }

    public void setGbpAverageExchangeRate(BigDecimal gbpAverageExchangeRate) {
        this.gbpAverageExchangeRate = gbpAverageExchangeRate;
    }
}
