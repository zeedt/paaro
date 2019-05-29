package com.plethub.paaro.webapp.ApiModel;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Bank {

    private Long id;
    private String bankType;
    private String bankName;
    private String bankCode;
    private String countryCode;
    private String comment;
    private Boolean disabled;
}
