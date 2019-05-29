package com.plethub.paaro.core.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "oauth_client_details")
public class OauthClientDetails implements ClientDetails {

    private static final long serialVersionUID = -1000000000000L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @NotNull
    @NotBlank
    @Column(name = "client_name")
    private String clientName;

    @NotNull
    @NotBlank
    @Column(name = "client_id")
    private String clientId;

    @NotNull
    @NotBlank
    @Column(name = "client_secret")
    private String clientSecret;

    private String scope = "profile";

    @NotNull
    @NotBlank
    @Column(name = "resource_ids")
    private String resourceIds;    // comma separated list of accessible resource server ids

    @Column(name = "authorized_grant_types")
    private String authorizedGrantTypes = "implicit,client_credentials,password";

    @Column(name = "web_server_redirect_uri")
    private String webServerRedirectUris;

    @Column(name = "autoapprove")
    private String autoApproveScopes;

    private String authorities;

    @Column(name = "access_token_validity")
    private Integer accessTokenValiditySeconds = 86400;    // 24 hour token validity

    @Column(name = "refresh_token_validity")
    private Integer refreshTokenValiditySeconds;

    @Column(name = "additional_information")
    private String additionalInformation;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public Set<String> getResourceIds() {
        return new HashSet<>(Arrays.asList(this.resourceIds.split(",")));
    }

    public void setResourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
    }

    @Override
    public boolean isSecretRequired() {
        return this.clientSecret != null;
    }

    @Override
    public String getClientSecret() {
        return this.clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    public boolean isScoped() {
        return this.scope != null && !this.scope.isEmpty();
    }

    @Override
    public Set<String> getScope() {
        return new HashSet<String>(Arrays.asList(this.scope.split(",")));
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return new HashSet<String>(Arrays.asList(this.authorizedGrantTypes.split(",")));
    }

    public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return new HashSet<String>(Arrays.asList(this.webServerRedirectUris.split(",")));
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        String[] auths = this.authorities.split(",");
        List<SimpleGrantedAuthority> gAuths = new ArrayList<>();
        for (String authority : auths) {
            gAuths.add(new SimpleGrantedAuthority(authority));
        }
        return new ArrayList<>(gAuths);

    }

    public String getAuthorizedGrantTypesAsString() {
        return this.authorizedGrantTypes;
    }

    public String getResourceIdsAsString() {
        return this.resourceIds;
    }


    public String getAuthoritiesAsString() {
        return this.authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return this.accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return this.refreshTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return false;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return null;
    }


    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getWebServerRedirectUris() {
        return webServerRedirectUris;
    }

    public void setWebServerRedirectUris(String webServerRedirectUris) {
        this.webServerRedirectUris = webServerRedirectUris;
    }

    public String getAutoApproveScopes() {
        return autoApproveScopes;
    }

    public void setAutoApproveScopes(String autoApproveScopes) {
        this.autoApproveScopes = autoApproveScopes;
    }


    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getAdditionalInformationn(){
        return this.additionalInformation;
    }

    @Override
    public String toString() {
        return "OauthClientDetails{" +
                "id=" + id +
                ", clientName='" + clientName + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", scope='" + scope + '\'' +
                ", resourceIds='" + resourceIds + '\'' +
                ", authorizedGrantTypes='" + authorizedGrantTypes + '\'' +
                ", webServerRedirectUris='" + webServerRedirectUris + '\'' +
                ", autoApproveScopes='" + autoApproveScopes + '\'' +
                ", authorities='" + authorities + '\'' +
                ", accessTokenValiditySeconds=" + accessTokenValiditySeconds +
                ", refreshTokenValiditySeconds=" + refreshTokenValiditySeconds +
                '}';
    }


    public String getScopeAsString() {
        return this.scope;
    }
}

