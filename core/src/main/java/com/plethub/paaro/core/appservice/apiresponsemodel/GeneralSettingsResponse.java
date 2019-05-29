package com.plethub.paaro.core.appservice.apiresponsemodel;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.GeneralSetting;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralSettingsResponse {

    private GeneralSetting generalSetting;

    private String message;

    private ApiResponseCode responseStatus;


    public GeneralSetting getGeneralSetting() {
        return generalSetting;
    }

    public void setGeneralSetting(GeneralSetting generalSetting) {
        this.generalSetting = generalSetting;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApiResponseCode getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ApiResponseCode responseStatus) {
        this.responseStatus = responseStatus;
    }

    public static GeneralSettingsResponse returnResponseWithCode(ApiResponseCode apiResponseCode, String message) {
        GeneralSettingsResponse generalSettingsResponse = new GeneralSettingsResponse();
        generalSettingsResponse.setMessage(message);
        generalSettingsResponse.setResponseStatus(apiResponseCode);
        return generalSettingsResponse;
    }
}
