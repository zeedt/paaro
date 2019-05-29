package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.BankDeposit;
import org.springframework.data.domain.Page;

import java.util.List;

public class BankDepositResponseModel {

    private ApiResponseCode apiResponseCode;

    private BankDeposit bankDeposit;

    private List<BankDeposit> bankDeposits;

    private Page<BankDeposit> bankDepositPage;

    private String narration;

    public ApiResponseCode getApiResponseCode() {
        return apiResponseCode;
    }

    public void setApiResponseCode(ApiResponseCode apiResponseCode) {
        this.apiResponseCode = apiResponseCode;
    }

    public BankDeposit getBankDeposit() {
        return bankDeposit;
    }

    public void setBankDeposit(BankDeposit bankDeposit) {
        this.bankDeposit = bankDeposit;
    }

    public List<BankDeposit> getBankDeposits() {
        return bankDeposits;
    }

    public void setBankDeposits(List<BankDeposit> bankDeposits) {
        this.bankDeposits = bankDeposits;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }


    public static BankDepositResponseModel fromNarration (ApiResponseCode apiResponseCode, String narration) {

        BankDepositResponseModel bankDepositResponseModel = new BankDepositResponseModel();
        bankDepositResponseModel.setNarration(narration);
        bankDepositResponseModel.setApiResponseCode(apiResponseCode);

        return bankDepositResponseModel;

    }

    public Page<BankDeposit> getBankDepositPage() {
        return bankDepositPage;
    }

    public void setBankDepositPage(Page<BankDeposit> bankDepositPage) {
        this.bankDepositPage = bankDepositPage;
    }

}
