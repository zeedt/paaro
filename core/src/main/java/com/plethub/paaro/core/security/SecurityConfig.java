package com.plethub.paaro.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionListener;
import java.util.List;
import java.util.regex.Pattern;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private TokenStore jdbcTokenStore;

    @Autowired
    private List<AuthenticationProvider> authenticationProviders;

    @Bean
    public List<AuthenticationProvider> authenticationProviders() {
        return authenticationProviders;
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        for (AuthenticationProvider a : authenticationProviders) {
            auth.authenticationProvider(a);
        }
    }

    @Override
    @Bean(name = "userAuthenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/user/createUser");
        web.ignoring().antMatchers("/user/forgotPassword");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(
                        new String[]{
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/login/**",
                                "/user/forgotPassword",
                                "/settings",
                                "/regDetails/**",
                                "**/home",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/vendors/**",
                                "/static/**",
                                "/fonts/**",
                                "/font-awesome/**",
                                "/template-js/**",
                                "/template-pages/**",
                                "/template-css/**",
                                "/authority/**",
                                "/favicon.ico",
                                "/image/**",
                                "/assets/**"
                        }
                ).permitAll()
                .antMatchers("/login").permitAll()
                .anyRequest().fullyAuthenticated()
                .and()
                .csrf().ignoringAntMatchers("/user/**")
                .requireCsrfProtectionMatcher(new RequestMatcher() {
                    private Pattern allowedMethods = Pattern.compile("^(GET|POST|HEAD|TRACE|OPTIONS)$");
                    @Override
                    public boolean matches(HttpServletRequest httpServletRequest) {
                        if (allowedMethods.matcher(httpServletRequest.getMethod()).matches())
                            return false;
                        return true;
                    }
                })
                .and().formLogin().loginPage("/login")
                .permitAll()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation().migrateSession()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(new SessionRegistryImpl()).and()
                .sessionAuthenticationStrategy(userConcurrentSessions())
                .and().formLogin()
                .loginPage("/login")
                .and()
                .exceptionHandling().accessDeniedPage("/");
    }


    @Bean
    public SessionRegistry sessionRegistry() {
        SessionRegistry sessionRegistry = new SessionRegistryImpl();
        return sessionRegistry;
    }


    @Bean
    public HttpSessionListener httpSessionListener() {
        return new SessionListener();
    }

    @Bean
    public UserConcurrentSessions userConcurrentSessions(){
        return new UserConcurrentSessions(sessionRegistry(), jdbcTokenStore);

    }
}
