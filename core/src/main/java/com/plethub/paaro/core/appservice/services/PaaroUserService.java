package com.plethub.paaro.core.appservice.services;

import com.plethub.paaro.core.usermanagement.apimodels.ManagedUserModelApi;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.models.ManagedUser;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import com.plethub.paaro.core.infrastructure.utils.RestApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaaroUserService {

    @Autowired
    private AuditLogService auditLogService;

    @Value("${paaro.cliend.id:paaro-service:secret}")
    private String clientId;

    @Value("${user.management.host:http://127.0.0.1:8011/oauth/token}")
    private String userTokenUrl;

    RestApiClient restApiClient = new RestApiClient();

    public Object login(ManagedUserModelApi managedUserModelApi, HttpServletRequest servletRequest) throws Exception {
        try {
            String base64encodedString = Base64.getEncoder().encodeToString(clientId.getBytes());
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Basic " + base64encodedString);
            MultiValueMap<String, Object> request = new LinkedMultiValueMap();
            request.add("username", managedUserModelApi.getEmail());
            request.add("password", managedUserModelApi.getPassword());
            request.add("grant_type", "password");
            String url = userTokenUrl + "?grant_type=password&username=" + managedUserModelApi.getEmail() + "&password=" + managedUserModelApi.getPassword();
            Object object = restApiClient.apiPostAndGetClass(url,OAuth2AccessToken.class,request,headers, false);
            auditLogService.saveAudit(null,null, Module.USER_MANAGEMENT, servletRequest, String.format("User %s logged in",managedUserModelApi.getEmail()));
            return object;
        } catch (Exception e) {
            Map<String,String> message = new HashMap<>();
            message.put("message", "unable to fetch login details");
            message.put("cause", e.getMessage());
            auditLogService.saveAudit(null,null, Module.USER_MANAGEMENT, servletRequest, String.format("User %s tried to log in but failed due to %s",managedUserModelApi.getEmail(), e.getCause()));
            return message;
        }
    }

}
