package com.plethub.paaro.webapp.ApiModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class UserActivityResponse {

    private String message;

    private ManagedUser managedUser;

    private String responseStatus;

}
