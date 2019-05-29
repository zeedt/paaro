package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.plethub.paaro.core.appservice.enums.ApiResponseCode;

public class TransferDashboardDetails {

    private String dailyTransfersCount;

    private String weeklyTransfersCount;

    private String monthlyTransfersCount;

    private String yearlyTransfersCount;

    private String dailyMatchedTransfersCount;

    private String weeklyMatchedTransfersCount;

    private String monthlyMatchedTransfersCount;

    private String yearlyMatchedTransfersCount;


    private String narration;

    private ApiResponseCode apiResponseCode;

    public static TransferDashboardDetails fromNarration (ApiResponseCode apiResponseCode, String narration) {

        TransferDashboardDetails userDashboardDetails = new TransferDashboardDetails();
        userDashboardDetails.setNarration(narration);
        userDashboardDetails.setApiResponseCode(apiResponseCode);

        return userDashboardDetails;

    }

    public String getDailyTransfersCount() {
        return dailyTransfersCount;
    }

    public void setDailyTransfersCount(String dailyTransfersCount) {
        this.dailyTransfersCount = dailyTransfersCount;
    }

    public String getWeeklyTransfersCount() {
        return weeklyTransfersCount;
    }

    public void setWeeklyTransfersCount(String weeklyTransfersCount) {
        this.weeklyTransfersCount = weeklyTransfersCount;
    }

    public String getMonthlyTransfersCount() {
        return monthlyTransfersCount;
    }

    public void setMonthlyTransfersCount(String monthlyTransfersCount) {
        this.monthlyTransfersCount = monthlyTransfersCount;
    }

    public String getYearlyTransfersCount() {
        return yearlyTransfersCount;
    }

    public void setYearlyTransfersCount(String yearlyTransfersCount) {
        this.yearlyTransfersCount = yearlyTransfersCount;
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

    public String getDailyMatchedTransfersCount() {
        return dailyMatchedTransfersCount;
    }

    public void setDailyMatchedTransfersCount(String dailyMatchedTransfersCount) {
        this.dailyMatchedTransfersCount = dailyMatchedTransfersCount;
    }

    public String getWeeklyMatchedTransfersCount() {
        return weeklyMatchedTransfersCount;
    }

    public void setWeeklyMatchedTransfersCount(String weeklyMatchedTransfersCount) {
        this.weeklyMatchedTransfersCount = weeklyMatchedTransfersCount;
    }

    public String getMonthlyMatchedTransfersCount() {
        return monthlyMatchedTransfersCount;
    }

    public void setMonthlyMatchedTransfersCount(String monthlyMatchedTransfersCount) {
        this.monthlyMatchedTransfersCount = monthlyMatchedTransfersCount;
    }

    public String getYearlyMatchedTransfersCount() {
        return yearlyMatchedTransfersCount;
    }

    public void setYearlyMatchedTransfersCount(String yearlyMatchedTransfersCount) {
        this.yearlyMatchedTransfersCount = yearlyMatchedTransfersCount;
    }
}
