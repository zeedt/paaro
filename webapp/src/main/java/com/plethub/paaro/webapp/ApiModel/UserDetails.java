package com.plethub.paaro.webapp.ApiModel;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserDetails {

    private String lastName;

    private String category;

    private String scope;

    private Authorities[] authorities;

    private String email;

    private String jti;

    private String expires_in;

    private String token_type;

    private String firstName;

    private String access_token;

}
