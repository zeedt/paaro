package com.plethub.paaro.core.security;

import com.plethub.paaro.core.models.ManagedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public interface AuthenticationProvider<T extends ManagedUser> {

    Authentication authenticate(Authentication var1) throws AuthenticationException;

    boolean supports(Class<?> var1);

}
