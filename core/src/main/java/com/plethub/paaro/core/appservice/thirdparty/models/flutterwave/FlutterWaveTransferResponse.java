package com.plethub.paaro.core.appservice.thirdparty.models.flutterwave;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlutterWaveTransferResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private FlutterWaveTransferDataResponse data;

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("data")
    public FlutterWaveTransferDataResponse getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(FlutterWaveTransferDataResponse data) {
        this.data = data;
    }

}