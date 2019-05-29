package com.plethub.paaro.core.appservice.apirequestmodel;

import com.plethub.paaro.core.appservice.enums.TransactionStatus;

import java.math.BigDecimal;
import java.util.Date;

public class FundingTransactionSearchRequestModel {

    private String accountName;

    private String accountNumber;

    private String paaroReferenceId;

    private TransactionStatus transactionStatus;

    private Date fromDate;

    private Date toDate;

    private BigDecimal actualAmount;

    private String fromCurrency;

    private String toCurrency;

}
