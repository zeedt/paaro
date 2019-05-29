package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.plethub.paaro.core.appservice.enums.ApiResponseCode;

import java.math.BigDecimal;

public class RevenueDashboardDetails {

    private String dailyNairaRevenue;

    private String dailyForeignRevenue;

    private String weeklyNairaRevenue;

    private String weeklyForeignRevenue;

    private String monthlyNairaRevenue;

    private String monthlyForeignRevenue;

    private String yearlyNairaRevenue;

    private String yearlyForeignRevenue;

    private String narration;

    private ApiResponseCode apiResponseCode;


    public String getDailyNairaRevenue() {
        return dailyNairaRevenue;
    }

    public void setDailyNairaRevenue(String dailyNairaRevenue) {
        this.dailyNairaRevenue = dailyNairaRevenue;
    }

    public String getDailyForeignRevenue() {
        return dailyForeignRevenue;
    }

    public void setDailyForeignRevenue(String dailyForeignRevenue) {
        this.dailyForeignRevenue = dailyForeignRevenue;
    }

    public String getWeeklyNairaRevenue() {
        return weeklyNairaRevenue;
    }

    public void setWeeklyNairaRevenue(String weeklyNairaRevenue) {
        this.weeklyNairaRevenue = weeklyNairaRevenue;
    }

    public String getWeeklyForeignRevenue() {
        return weeklyForeignRevenue;
    }

    public void setWeeklyForeignRevenue(String weeklyForeignRevenue) {
        this.weeklyForeignRevenue = weeklyForeignRevenue;
    }

    public String getMonthlyNairaRevenue() {
        return monthlyNairaRevenue;
    }

    public void setMonthlyNairaRevenue(String monthlyNairaRevenue) {
        this.monthlyNairaRevenue = monthlyNairaRevenue;
    }

    public String getMonthlyForeignRevenue() {
        return monthlyForeignRevenue;
    }

    public void setMonthlyForeignRevenue(String monthlyForeignRevenue) {
        this.monthlyForeignRevenue = monthlyForeignRevenue;
    }

    public String getYearlyNairaRevenue() {
        return yearlyNairaRevenue;
    }

    public void setYearlyNairaRevenue(String yearlyNairaRevenue) {
        this.yearlyNairaRevenue = yearlyNairaRevenue;
    }

    public String getYearlyForeignRevenue() {
        return yearlyForeignRevenue;
    }

    public void setYearlyForeignRevenue(String yearlyForeignRevenue) {
        this.yearlyForeignRevenue = yearlyForeignRevenue;
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

    public static RevenueDashboardDetails fromNarration (ApiResponseCode apiResponseCode, String narration) {

        RevenueDashboardDetails revenueDashboardDetails = new RevenueDashboardDetails();
        revenueDashboardDetails.setNarration(narration);
        revenueDashboardDetails.setApiResponseCode(apiResponseCode);

        return revenueDashboardDetails;

    }
}
