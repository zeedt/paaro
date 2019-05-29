package com.plethub.paaro.core.models;


import com.plethub.paaro.core.appservice.enums.Status;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class GeneralSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private boolean enableEmailAlert;

    @NotNull
    private boolean enableSmsAlert;

    @NotNull
    private double chargeRate;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Status status;

    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEnableEmailAlert() {
        return enableEmailAlert;
    }

    public void setEnableEmailAlert(boolean enableEmailAlert) {
        this.enableEmailAlert = enableEmailAlert;
    }

    public boolean isEnableSmsAlert() {
        return enableSmsAlert;
    }

    public void setEnableSmsAlert(boolean enableSmsAlert) {
        this.enableSmsAlert = enableSmsAlert;
    }

    public double getChargeRate() {
        return chargeRate;
    }

    public void setChargeRate(double chargeRate) {
        this.chargeRate = chargeRate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
