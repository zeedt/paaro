package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.WalletTransferTransaction;

import java.util.List;

public class BidDashboardDetails {

    private List<WalletTransferTransaction> dailyExistingUsers;

    private List<WalletTransferTransaction> dailyNewUsers;

    private List<WalletTransferTransaction> weeklyExistingUsers;

    private List<WalletTransferTransaction> weeklyNewUsers;

    private List<WalletTransferTransaction> monthlyExistingUsers;

    private List<WalletTransferTransaction> monthlyNewUsers;

    private List<WalletTransferTransaction> yearlyExistingUsers;

    private List<WalletTransferTransaction> yearlyNewUsers;

    private String narration;

    private ApiResponseCode apiResponseCode;

    public List<WalletTransferTransaction> getDailyExistingUsers() {
        return dailyExistingUsers;
    }

    public void setDailyExistingUsers(List<WalletTransferTransaction> dailyExistingUsers) {
        this.dailyExistingUsers = dailyExistingUsers;
    }

    public List<WalletTransferTransaction> getDailyNewUsers() {
        return dailyNewUsers;
    }

    public void setDailyNewUsers(List<WalletTransferTransaction> dailyNewUsers) {
        this.dailyNewUsers = dailyNewUsers;
    }

    public List<WalletTransferTransaction> getWeeklyExistingUsers() {
        return weeklyExistingUsers;
    }

    public void setWeeklyExistingUsers(List<WalletTransferTransaction> weeklyExistingUsers) {
        this.weeklyExistingUsers = weeklyExistingUsers;
    }

    public List<WalletTransferTransaction> getWeeklyNewUsers() {
        return weeklyNewUsers;
    }

    public void setWeeklyNewUsers(List<WalletTransferTransaction> weeklyNewUsers) {
        this.weeklyNewUsers = weeklyNewUsers;
    }

    public List<WalletTransferTransaction> getMonthlyExistingUsers() {
        return monthlyExistingUsers;
    }

    public void setMonthlyExistingUsers(List<WalletTransferTransaction> monthlyExistingUsers) {
        this.monthlyExistingUsers = monthlyExistingUsers;
    }

    public List<WalletTransferTransaction> getMonthlyNewUsers() {
        return monthlyNewUsers;
    }

    public void setMonthlyNewUsers(List<WalletTransferTransaction> monthlyNewUsers) {
        this.monthlyNewUsers = monthlyNewUsers;
    }

    public List<WalletTransferTransaction> getYearlyExistingUsers() {
        return yearlyExistingUsers;
    }

    public void setYearlyExistingUsers(List<WalletTransferTransaction> yearlyExistingUsers) {
        this.yearlyExistingUsers = yearlyExistingUsers;
    }

    public List<WalletTransferTransaction> getYearlyNewUsers() {
        return yearlyNewUsers;
    }

    public void setYearlyNewUsers(List<WalletTransferTransaction> yearlyNewUsers) {
        this.yearlyNewUsers = yearlyNewUsers;
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

    public static BidDashboardDetails fromNarration (ApiResponseCode apiResponseCode, String narration) {

        BidDashboardDetails bidDashboardDetails = new BidDashboardDetails();
        bidDashboardDetails.setNarration(narration);
        bidDashboardDetails.setApiResponseCode(apiResponseCode);

        return bidDashboardDetails;

    }
}
