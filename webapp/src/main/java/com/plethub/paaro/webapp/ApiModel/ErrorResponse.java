package com.plethub.paaro.webapp.ApiModel;

import lombok.Data;

@Data
public class ErrorResponse {

    private String error;
    private String error_description;

}
