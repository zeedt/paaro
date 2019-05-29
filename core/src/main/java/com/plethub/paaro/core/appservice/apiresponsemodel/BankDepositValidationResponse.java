package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.plethub.paaro.core.models.PaymentReference;
import com.plethub.paaro.core.models.Wallet;

import java.math.BigDecimal;

public class BankDepositValidationResponse {

    private Wallet wallet;

    private PaymentReference paymentReference;

    private BankDepositResponseModel bankDepositResponseModel;

    public BankDepositValidationResponse(Wallet wallet, PaymentReference paymentReference, BankDepositResponseModel bankDepositResponseModel) {
        this.wallet = wallet;
        this.paymentReference = paymentReference;
        this.bankDepositResponseModel = bankDepositResponseModel;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public PaymentReference getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(PaymentReference paymentReference) {
        this.paymentReference = paymentReference;
    }

    public BankDepositResponseModel getBankDepositResponseModel() {
        return bankDepositResponseModel;
    }

    public void setBankDepositResponseModel(BankDepositResponseModel bankDepositResponseModel) {
        this.bankDepositResponseModel = bankDepositResponseModel;
    }
}
