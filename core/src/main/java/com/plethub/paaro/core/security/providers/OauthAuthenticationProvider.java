package com.plethub.paaro.core.security.providers;

import com.plethub.paaro.core.infrastructure.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.infrastructure.exception.PaaroAuthenticationException;
import com.plethub.paaro.core.models.Authority;
import com.plethub.paaro.core.models.ManagedUser;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.usermanagement.repository.ManagedUserAuthorityRepository;
import com.plethub.paaro.core.usermanagement.repository.ManagedUserRepository;
//import com.plethub.paaro.core.security.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import com.plethub.paaro.core.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OauthAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ManagedUserRepository managedUserRepository;

    @Autowired
    private ManagedUserAuthorityRepository managedUserAuthorityRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();

        ManagedUser managedUser = managedUserRepository.findManagedUserByEmailOrDisplayName(userName);

        if (managedUser == null) {
            throw new PaaroAuthenticationException("User not found");
        }

        if (!managedUser.isActive()) {
            throw new PaaroAuthenticationException("User has been deactivated");
        }

        if (!managedUser.isAccountActivatedViaLink()) {
            throw new PaaroAuthenticationException("Account has not been activated");
        }

        if (passwordEncoder.matches(authentication.getCredentials().toString(),managedUser.getPassword())) {

            List<Authority> authorities = userService.getAuthoritiesByUserId(managedUser.getId());

            UserDetailsTokenEnvelope userDetailsTokenEnvelope = new UserDetailsTokenEnvelope(authorities,managedUser);
            UsernamePasswordAuthenticationToken authenticationDetails = new UsernamePasswordAuthenticationToken(userDetailsTokenEnvelope,null,authorities);

            HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            auditLogService.saveLoginAudit(userName, Module.USER_MANAGEMENT, servletRequest);

            return authenticationDetails;
        } else{
            throw new BadCredentialsException("Username/password incorrect");
        }

    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}
