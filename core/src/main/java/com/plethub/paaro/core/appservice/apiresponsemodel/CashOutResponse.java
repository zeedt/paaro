package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.CashOutLog;
import com.plethub.paaro.core.models.Wallet;
import org.springframework.data.domain.Page;

import java.util.List;

public class CashOutResponse {

    private String narration;

    private ApiResponseCode apiResponseCode;

    private CashOutLog cashOutLog;

    private List<CashOutLog> cashOutLogList;

    private Page<CashOutLog> cashOutLogPage;

    public static CashOutResponse fromCodeAndNarration(ApiResponseCode apiResponseCode, String narration) {

        CashOutResponse cashOutResponse = new CashOutResponse();
        cashOutResponse.setNarration(narration);
        cashOutResponse.setApiResponseCode(apiResponseCode);

        return cashOutResponse;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public ApiResponseCode getApiResponseCode() {
        return apiResponseCode;
    }

    public void setApiResponseCode(ApiResponseCode apiResponseCode) {
        this.apiResponseCode = apiResponseCode;
    }

    public CashOutLog getCashOutLog() {
        return cashOutLog;
    }

    public void setCashOutLog(CashOutLog cashOutLog) {
        this.cashOutLog = cashOutLog;
    }

    public List<CashOutLog> getCashOutLogList() {
        return cashOutLogList;
    }

    public void setCashOutLogList(List<CashOutLog> cashOutLogList) {
        this.cashOutLogList = cashOutLogList;
    }

    public Page<CashOutLog> getCashOutLogPage() {
        return cashOutLogPage;
    }

    public void setCashOutLogPage(Page<CashOutLog> cashOutLogPage) {
        this.cashOutLogPage = cashOutLogPage;
    }

}
