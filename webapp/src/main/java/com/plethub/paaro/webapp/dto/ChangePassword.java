package com.plethub.paaro.webapp.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@ToString
public class ChangePassword {

    @NotEmpty
    private String oldPassword;
    @NotEmpty
    private String newPassword;
    @NotEmpty
    private String confirmPassword;

}
