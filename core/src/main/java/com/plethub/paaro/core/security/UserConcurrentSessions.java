package com.plethub.paaro.core.security;

import com.plethub.paaro.core.infrastructure.UserDetailsTokenEnvelope;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class UserConcurrentSessions implements SessionAuthenticationStrategy {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private final SessionRegistry sessionRegistry;
    //token store
    private JdbcTokenStore tokenStore;

    public UserConcurrentSessions(SessionRegistry sessionRegistry, TokenStore tokenStore) {
        Assert.notNull(sessionRegistry, "The sessionRegistry cannot be null");
        this.sessionRegistry = sessionRegistry;
        this.tokenStore = (JdbcTokenStore) tokenStore;
    }

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest httpServletRequest,
                                 HttpServletResponse httpServletResponse) throws SessionAuthenticationException {

        //clear previous sessions and tokens for the user
        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) authentication.getPrincipal();
        Collection<OAuth2AccessToken> accessTokens = tokenStore.findTokensByUserName(userDetailsTokenEnvelope.getUsername());
        if(!CollectionUtils.isEmpty(accessTokens )){
            for (OAuth2AccessToken oAuth2AccessToken:accessTokens) {
                tokenStore.removeAccessToken(oAuth2AccessToken);
            }
        }
    }
}
