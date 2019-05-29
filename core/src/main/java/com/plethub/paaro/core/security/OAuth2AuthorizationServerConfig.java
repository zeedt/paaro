package com.plethub.paaro.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableAuthorizationServer
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    @Qualifier("passwordEncoder")
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    @Autowired
    @Qualifier("userAuthenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private OauthClientDetailsService oauthClientDetailsService;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.realm("/oauth/tokenKey").passwordEncoder(passwordEncoder).tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(oauthClientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .tokenEnhancer(tokenEnhancerChain())
                .tokenStore(jdbcTokenStore())
                .authorizationCodeServices(authorizationCodeServices())
                .accessTokenConverter(accessTokenConverter())
                .userApprovalHandler(userApprovalHandler());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource("secret.jks"), "secret".toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("usermanagement"));
        return converter;
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    @Bean
    public JdbcTokenStore jdbcTokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    public JdbcClientDetailsService clientDetailsService() {
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        clientDetailsService.setPasswordEncoder(passwordEncoder);
        return clientDetailsService;
    }

    @Bean
    public UserApprovalHandler userApprovalHandler() {
        return new ManagedUserApprovalHandler();
    }

    @Bean
    public TokenEnhancerChain tokenEnhancerChain(){
        List<TokenEnhancer> tokenEnhancerList = new ArrayList<>();
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerList.add(tokenEnhancer());
        tokenEnhancerList.add(accessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancerList);
        return tokenEnhancerChain;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new UserTokenEnchancer();
    }

}
