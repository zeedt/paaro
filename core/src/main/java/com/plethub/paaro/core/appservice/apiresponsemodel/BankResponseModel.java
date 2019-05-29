package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.Bank;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankResponseModel {

    private ApiResponseCode responseCode;

    private String message;

    private List<Bank> bankList;

    private Page<Bank> bankPage;

    private Bank bank;

    private String accountName;

    private String accountNumber;

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

    public List<Bank> getBankList() {
        return bankList;
    }

    public void setBankList(List<Bank> bankList) {
        this.bankList = bankList;
    }

    public static BankResponseModel fromNarration(ApiResponseCode responseCode, String message) {

        BankResponseModel bankResponseModel = new BankResponseModel();
        bankResponseModel.setMessage(message);
        bankResponseModel.setResponseCode(responseCode);
        return bankResponseModel;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public Page<Bank> getBankPage() {
        return bankPage;
    }

    public void setBankPage(Page<Bank> bankPage) {
        this.bankPage = bankPage;
    }
}
