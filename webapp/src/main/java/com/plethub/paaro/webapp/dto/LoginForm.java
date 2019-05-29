package com.plethub.paaro.webapp.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class LoginForm {

    private String grant_type;
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;

}
