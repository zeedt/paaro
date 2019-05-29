package com.plethub.paaro.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.plethub.paaro.core.appservice.enums.BankType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Bank implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private BankType bankType;

    @NotNull
    @NotEmpty
    @Column(unique = true)
    private String bankName;

    @NotNull
    @NotEmpty
    @Column(unique = true)
    private String bankCode;

    @NotNull
    @NotEmpty
    private String countryCode;

    private String comment;

    private boolean disabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BankType getBankType() {
        return bankType;
    }

    public void setBankType(BankType bankType) {
        this.bankType = bankType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "Bank{" +
                "id=" + id +
                ", bankType=" + bankType +
                ", bankName='" + bankName + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", comment='" + comment + '\'' +
                ", disabled=" + disabled +
                '}';
    }
}
