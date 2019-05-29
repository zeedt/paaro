package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.plethub.paaro.core.appservice.enums.ApiResponseCode;

import java.math.BigDecimal;

public class WalletBalanceDashboardDetails {

    private String nairaAvailableBalance;

    private String foreignAvailableBalance;

    private String foreignLedgerBalance;

    private String nairaLedgerBalance;

    private BigDecimal ledgerBalance;

    private BigDecimal availableBalance;

    private String narration;

    private ApiResponseCode apiResponseCode;

    public WalletBalanceDashboardDetails(BigDecimal availableBalance, BigDecimal ledgerBalance) {
        this.availableBalance = availableBalance;
        this.ledgerBalance = ledgerBalance;
    }

    public WalletBalanceDashboardDetails() {
    }

    public static WalletBalanceDashboardDetails fromNarration (ApiResponseCode apiResponseCode, String narration) {
        WalletBalanceDashboardDetails userDashboardDetails = new WalletBalanceDashboardDetails();
        userDashboardDetails.setNarration(narration);
        userDashboardDetails.setApiResponseCode(apiResponseCode);
        return userDashboardDetails;
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

    public String getNairaAvailableBalance() {
        return nairaAvailableBalance;
    }

    public void setNairaAvailableBalance(String nairaAvailableBalance) {
        this.nairaAvailableBalance = nairaAvailableBalance;
    }

    public String getForeignAvailableBalance() {
        return foreignAvailableBalance;
    }

    public void setForeignAvailableBalance(String foreignAvailableBalance) {
        this.foreignAvailableBalance = foreignAvailableBalance;
    }

    public String getForeignLedgerBalance() {
        return foreignLedgerBalance;
    }

    public void setForeignLedgerBalance(String foreignLedgerBalance) {
        this.foreignLedgerBalance = foreignLedgerBalance;
    }

    public String getNairaLedgerBalance() {
        return nairaLedgerBalance;
    }

    public void setNairaLedgerBalance(String nairaLedgerBalance) {
        this.nairaLedgerBalance = nairaLedgerBalance;
    }

    public BigDecimal getLedgerBalance() {
        return ledgerBalance;
    }

    public void setLedgerBalance(BigDecimal ledgerBalance) {
        this.ledgerBalance = ledgerBalance;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }
}
