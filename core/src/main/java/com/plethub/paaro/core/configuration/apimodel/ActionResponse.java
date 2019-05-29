package com.plethub.paaro.core.configuration.apimodel;


import com.plethub.paaro.core.appservice.enums.ApiResponseCode;

import java.util.List;

public class ActionResponse {

    private List<ActionWrapper> actionWrapperList;

    private ActionWrapper actionWrapper;

    private ApiResponseCode apiResponseCode;

    private String message;

    public static ActionResponse fromCodeAndNarration(ApiResponseCode apiResponseCode, String message) {
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setApiResponseCode(apiResponseCode);
        actionResponse.setMessage(message);

        return actionResponse;
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

    public List<ActionWrapper> getActionWrapperList() {
        return actionWrapperList;
    }

    public void setActionWrapperList(List<ActionWrapper> actionWrapperList) {
        this.actionWrapperList = actionWrapperList;
    }

    public ActionWrapper getActionWrapper() {
        return actionWrapper;
    }

    public void setActionWrapper(ActionWrapper actionWrapper) {
        this.actionWrapper = actionWrapper;
    }
}
