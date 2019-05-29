package com.plethub.paaro.core.appservice.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankMigrationClassHolder {

    private List<BankDetails> data;

    private String status;

    private String message;

    public BankMigrationClassHolder() {
    }

    public BankMigrationClassHolder(List<BankDetails> data, String status, String message) {
        this.data = data;
        this.status = status;
        this.message = message;
    }

    public List<BankDetails> getData() {
        return data;
    }

    public void setData(List<BankDetails> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



}


