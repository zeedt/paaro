package com.plethub.paaro.webapp.ApiModel;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Data
@ToString
public class BankResponse {

    private String message;

    private String responseCode;

    private String accountNumber;

    private String accountName;

    private PageImpl<Bank> bankPage;

    private Bank bank;

    private List<Bank> bankList;

}
