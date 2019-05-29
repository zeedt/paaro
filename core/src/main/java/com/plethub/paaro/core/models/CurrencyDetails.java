package com.plethub.paaro.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "currency_details")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CurrencyDetails implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String currencyCode;

    @NotNull
    private Double buyingRate;

    @NotNull
    private Double sellingRate;

    @NotNull
    private Double serviceChargeCap;

    @NotNull
    private boolean calculateChargeInNaira = true;

    @NotNull
    private Double flatChargeRate;

    @NotNull
    private Double percentageChargeRate = 0d;

    @NotNull
    private Double rateSearchDeviation;

    @NotNull
    private Double transactionFeePerInternationalCurrencyInNaira;

    @NotNull
    private boolean flatChargeEnabled = false;

    private Double amountSearchDeviation;

    public boolean isFlatChargeEnabled() {
        return flatChargeEnabled;
    }

    public void setFlatChargeEnabled(boolean flatChargeEnabled) {
        this.flatChargeEnabled = flatChargeEnabled;
    }

    public Double getBuyingRate() {
        return buyingRate;
    }

    public void setBuyingRate(Double buyingRate) {
        this.buyingRate = buyingRate;
    }

    public Double getSellingRate() {
        return sellingRate;
    }

    public void setSellingRate(Double sellingRate) {
        this.sellingRate = sellingRate;
    }

    public Double getServiceChargeCap() {
        return serviceChargeCap;
    }

    public void setServiceChargeCap(Double serviceChargeCap) {
        this.serviceChargeCap = serviceChargeCap;
    }


    public boolean isCalculateChargeInNaira() {
        return calculateChargeInNaira;
    }

    public void setCalculateChargeInNaira(boolean calculateChargeInNaira) {
        this.calculateChargeInNaira = calculateChargeInNaira;
    }

    public Double getFlatChargeRate() {
        return flatChargeRate;
    }

    public void setFlatChargeRate(Double flatChargeRate) {
        this.flatChargeRate = flatChargeRate;
    }

    public Double getPercentageChargeRate() {
        return percentageChargeRate;
    }

    public void setPercentageChargeRate(Double percentageChargeRate) {
        this.percentageChargeRate = percentageChargeRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Double getRateSearchDeviation() {
        return rateSearchDeviation;
    }

    public void setRateSearchDeviation(Double rateSearchDeviation) {
        this.rateSearchDeviation = rateSearchDeviation;
    }

    public Double getTransactionFeePerInternationalCurrencyInNaira() {
        return transactionFeePerInternationalCurrencyInNaira;
    }

    public void setTransactionFeePerInternationalCurrencyInNaira(Double transactionFeePerInternationalCurrencyInNaira) {
        this.transactionFeePerInternationalCurrencyInNaira = transactionFeePerInternationalCurrencyInNaira;
    }

    public Double getAmountSearchDeviation() {
        return amountSearchDeviation;
    }

    public void setAmountSearchDeviation(Double amountSearchDeviation) {
        this.amountSearchDeviation = amountSearchDeviation;
    }

    @Override
    public String toString() {
        return "CurrencyDetails{" +
                "id=" + id +
                ", currencyCode='" + currencyCode + '\'' +
                ", buyingRate=" + buyingRate +
                ", sellingRate=" + sellingRate +
                ", serviceChargeCap=" + serviceChargeCap +
                ", calculateChargeInNaira=" + calculateChargeInNaira +
                ", flatChargeRate=" + flatChargeRate +
                ", percentageChargeRate=" + percentageChargeRate +
                ", rateSearchDeviation=" + rateSearchDeviation +
                ", transactionFeePerInternationalCurrencyInNaira=" + transactionFeePerInternationalCurrencyInNaira +
                ", flatChargeEnabled=" + flatChargeEnabled +
                ", amountSearchDeviation=" + amountSearchDeviation +
                '}';
    }
}
