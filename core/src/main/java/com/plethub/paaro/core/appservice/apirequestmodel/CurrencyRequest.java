package com.plethub.paaro.core.appservice.apirequestmodel;

public class CurrencyRequest {

    private String type;

    private Double rateToNaira;

    private String description;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getRateToNaira() {
        return rateToNaira;
    }

    public void setRateToNaira(Double rateToNaira) {
        this.rateToNaira = rateToNaira;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CurrencyRequest{" +
                "type='" + type + '\'' +
                ", rateToNaira=" + rateToNaira +
                ", description='" + description + '\'' +
                '}';
    }
}
