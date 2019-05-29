package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferSummary {

    private BigDecimal totalAmount;

    private BigDecimal effectiveAmount;

    private BigDecimal chargeAmount;

    private Double chargePercentage;

    private Double exchangeRate;

    private Double flatRate;

    private String equivalentCurrency;

    public String getEquivalentCurrency() {
        return equivalentCurrency;
    }

    public void setEquivalentCurrency(String equivalentCurrency) {
        this.equivalentCurrency = equivalentCurrency;
    }

    public BigDecimal getEquivalentAmountInToCurrency() {
        return equivalentAmountInToCurrency;
    }

    public void setEquivalentAmountInToCurrency(BigDecimal equivalentAmountInToCurrency) {
        this.equivalentAmountInToCurrency = equivalentAmountInToCurrency;
    }

    private BigDecimal equivalentAmountInToCurrency;

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getEffectiveAmount() {
        return effectiveAmount;
    }

    public void setEffectiveAmount(BigDecimal effectiveAmount) {
        this.effectiveAmount = effectiveAmount;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public Double getChargePercentage() {
        return chargePercentage;
    }

    public void setChargePercentage(Double chargePercentage) {
        this.chargePercentage = chargePercentage;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Double getFlatRate() {
        return flatRate;
    }

    public void setFlatRate(Double flatRate) {
        this.flatRate = flatRate;
    }
}
