package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.WalletTransferTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardResponseModel {

    private Page<WalletTransferTransaction> onGoingTransactions = new PageImpl<>(new ArrayList<>());

    private Page<WalletTransferTransaction> completedTransactions = new PageImpl<>(new ArrayList<>());

    private Page<WalletTransferTransaction> matchedTransactions = new PageImpl<>(new ArrayList<>());

    private Long totalMatchedBids;

    private BigDecimal totalAmountInNaira;

    private BigDecimal totalAmountInGbp;

    private BigDecimal totalChargeInNaira;

    private BigDecimal totalChargeInGbp;

    private Long noOfOngoing;

    private Long noOfMathed;

    public Long getNoOfCompleted() {
        return noOfCompleted;
    }

    public void setNoOfCompleted(Long noOfCompleted) {
        this.noOfCompleted = noOfCompleted;
    }

    private Long noOfCompleted;

    public Long getNoOfOngoing() {
        return noOfOngoing;
    }

    public void setNoOfOngoing(Long noOfOngoing) {
        this.noOfOngoing = noOfOngoing;
    }

    private ApiResponseCode responseStatus;

    private String message;

    public Page<WalletTransferTransaction> getOnGoingTransactions() {
        return onGoingTransactions;
    }

    public void setOnGoingTransactions(Page<WalletTransferTransaction> onGoingTransactions) {
        this.onGoingTransactions = onGoingTransactions;
    }

    public Page<WalletTransferTransaction> getCompletedTransactions() {
        return completedTransactions;
    }

    public void setCompletedTransactions(Page<WalletTransferTransaction> completedTransactions) {
        this.completedTransactions = completedTransactions;
    }

    public ApiResponseCode getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ApiResponseCode responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public static DashboardResponseModel fromNaration(ApiResponseCode responseCode, String message) {
        DashboardResponseModel dashboardResponseModel = new DashboardResponseModel();
        dashboardResponseModel.setMessage(message);
        dashboardResponseModel.setResponseStatus(responseCode);

        return dashboardResponseModel;
    }

    public Long getTotalMatchedBids() {
        return totalMatchedBids;
    }

    public void setTotalMatchedBids(Long totalMatchedBids) {
        this.totalMatchedBids = totalMatchedBids;
    }

    public BigDecimal getTotalAmountInNaira() {
        return totalAmountInNaira;
    }

    public void setTotalAmountInNaira(BigDecimal totalAmountInNaira) {
        this.totalAmountInNaira = totalAmountInNaira;
    }

    public BigDecimal getTotalAmountInGbp() {
        return totalAmountInGbp;
    }

    public void setTotalAmountInGbp(BigDecimal totalAmountInGbp) {
        this.totalAmountInGbp = totalAmountInGbp;
    }

    public BigDecimal getTotalChargeInNaira() {
        return totalChargeInNaira;
    }

    public void setTotalChargeInNaira(BigDecimal totalChargeInNaira) {
        this.totalChargeInNaira = totalChargeInNaira;
    }

    public BigDecimal getTotalChargeInGbp() {
        return totalChargeInGbp;
    }

    public void setTotalChargeInGbp(BigDecimal totalChargeInGbp) {
        this.totalChargeInGbp = totalChargeInGbp;
    }

    public Page<WalletTransferTransaction> getMatchedTransactions() {
        return matchedTransactions;
    }

    public void setMatchedTransactions(Page<WalletTransferTransaction> matchedTransactions) {
        this.matchedTransactions = matchedTransactions;
    }

    public Long getNoOfMathed() {
        return noOfMathed;
    }

    public void setNoOfMathed(Long noOfMathed) {
        this.noOfMathed = noOfMathed;
    }
}
