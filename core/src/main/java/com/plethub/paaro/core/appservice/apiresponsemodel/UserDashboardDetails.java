package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.plethub.paaro.core.appservice.enums.ApiResponseCode;

public class UserDashboardDetails {

    private String dailySignupsCount;

    private String weeklySignupsCount;

    private String monthlySignupsCount;

    private String yearlySignupsCount;


    private String narration;

    private ApiResponseCode apiResponseCode;

    public static UserDashboardDetails fromNarration (ApiResponseCode apiResponseCode, String narration) {

        UserDashboardDetails userDashboardDetails = new UserDashboardDetails();
        userDashboardDetails.setNarration(narration);
        userDashboardDetails.setApiResponseCode(apiResponseCode);

        return userDashboardDetails;

    }

    public String getDailySignupsCount() {
        return dailySignupsCount;
    }

    public void setDailySignupsCount(String dailySignupsCount) {
        this.dailySignupsCount = dailySignupsCount;
    }

    public String getWeeklySignupsCount() {
        return weeklySignupsCount;
    }

    public void setWeeklySignupsCount(String weeklySignupsCount) {
        this.weeklySignupsCount = weeklySignupsCount;
    }

    public String getMonthlySignupsCount() {
        return monthlySignupsCount;
    }

    public void setMonthlySignupsCount(String monthlySignupsCount) {
        this.monthlySignupsCount = monthlySignupsCount;
    }

    public String getYearlySignupsCount() {
        return yearlySignupsCount;
    }

    public void setYearlySignupsCount(String yearlySignupsCount) {
        this.yearlySignupsCount = yearlySignupsCount;
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
}
