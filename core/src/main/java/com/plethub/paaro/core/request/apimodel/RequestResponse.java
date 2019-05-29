package com.plethub.paaro.core.request.apimodel;

import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.Request;
import org.springframework.data.domain.Page;

import java.util.List;

public class RequestResponse {

    private Page<Request> requestPage;

    private Request request;

    private List<Request> requestList;

    private String message;

    private ApiResponseCode apiResponseCode;

    public static RequestResponse fromCodeAndNarration(ApiResponseCode apiResponseCode, String message) {
        RequestResponse requestResponse = new RequestResponse();
        requestResponse.setMessage(message);
        requestResponse.setApiResponseCode(apiResponseCode);

        return requestResponse;
    }

    public Page<Request> getRequestPage() {
        return requestPage;
    }

    public void setRequestPage(Page<Request> requestPage) {
        this.requestPage = requestPage;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public List<Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<Request> requestList) {
        this.requestList = requestList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApiResponseCode getApiResponseCode() {
        return apiResponseCode;
    }

    public void setApiResponseCode(ApiResponseCode apiResponseCode) {
        this.apiResponseCode = apiResponseCode;
    }
}
