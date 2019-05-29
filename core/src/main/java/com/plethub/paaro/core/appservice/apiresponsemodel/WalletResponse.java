package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.Wallet;
import com.plethub.paaro.core.models.WalletFundingTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletResponse {

    private String message;

    private ApiResponseCode responseStatus;

    private Wallet wallet;

    private List<Wallet> walletList = new ArrayList<>();

    List<WalletFundingTransaction> walletFundingTransactions = new ArrayList<>();

    Page<WalletFundingTransaction> walletFundingTransactionPage = new PageImpl<>(new ArrayList<>());

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApiResponseCode getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ApiResponseCode responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public List<Wallet> getWalletList() {
        return walletList;
    }

    public void setWalletList(List<Wallet> walletList) {
        this.walletList = walletList;
    }

    public static WalletResponse returnResponseWithCode(ApiResponseCode responseStatus, String messagea) {

        WalletResponse walletResponse = new WalletResponse();

        walletResponse.setMessage(messagea);
        walletResponse.setResponseStatus(responseStatus);

        return walletResponse;

    }

    public List<WalletFundingTransaction> getWalletFundingTransactions() {
        return walletFundingTransactions;
    }

    public void setWalletFundingTransactions(List<WalletFundingTransaction> walletFundingTransactions) {
        this.walletFundingTransactions = walletFundingTransactions;
    }

    public Page<WalletFundingTransaction> getWalletFundingTransactionPage() {
        return walletFundingTransactionPage;
    }

    public void setWalletFundingTransactionPage(Page<WalletFundingTransaction> walletFundingTransactionPage) {
        this.walletFundingTransactionPage = walletFundingTransactionPage;
    }
}
