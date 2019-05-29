package com.plethub.paaro.core.appservice.apiresponsemodel;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.Currency;

import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CurrencyResponse {

    private ApiResponseCode apiResponseCode;

    private Currency currency;

    private List<Currency> currencyList;

    private String message;

    public ApiResponseCode getApiResponseCode() {
        return apiResponseCode;
    }

    public void setApiResponseCode(ApiResponseCode apiResponseCode) {
        this.apiResponseCode = apiResponseCode;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public List<Currency> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<Currency> currencyList) {
        this.currencyList = currencyList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public static CurrencyResponse returnResponseWithCode(ApiResponseCode apiResponseCode, String message) {
        CurrencyResponse currencyResponse = new CurrencyResponse();
        currencyResponse.setMessage(message);
        currencyResponse.setApiResponseCode(apiResponseCode);
        return currencyResponse;
    }
}
