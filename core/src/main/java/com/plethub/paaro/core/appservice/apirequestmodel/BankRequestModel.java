package com.plethub.paaro.core.appservice.apirequestmodel;


import com.plethub.paaro.core.models.Bank;

public class BankRequestModel extends Bank {

    private Long bankId;

    private String accountNumber;

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String toString() {
        return "BankRequestModel{" +
                "bankId=" + bankId +
                ", accountNumber='" + accountNumber + '\'' +
                "} " + super.toString();
    }
}
