package com.plethub.paaro.webapp.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plethub.paaro.webapp.ApiModel.UserActivityResponse;
import com.plethub.paaro.webapp.ApiModel.UserDetails;
import com.plethub.paaro.webapp.dto.ChangePassword;
import com.plethub.paaro.webapp.dto.ForgotPassword;
import com.plethub.paaro.webapp.dto.LoginForm;
import com.plethub.paaro.webapp.exception.PaaroException;
import com.plethub.paaro.webapp.service.AuthService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${api.base.url:http://159.65.53.209:8011/}")
    private String apiUrl;
    @Value("${api.base.code:cGFhcm8tc2VydmljZTpzZWNyZXQ=}")
    private String appCode;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String login(LoginForm loginForm, HttpSession httpSession) {

        try {
            UserDetails userDetails = performLogin(loginForm);
            httpSession.setAttribute("userDetails", userDetails);
            return "Login Successful";

        }catch (PaaroException ibe){
            throw new PaaroException(ibe.getMessage());
        }
    }

    @Override
    public UserDetails getCurrentUser(HttpServletRequest hsr){
        UserDetails userDetails = (UserDetails) hsr.getSession().getAttribute("userDetails");
        if (null != userDetails) {
            return userDetails;
        }
        return (null) ;
    }

    @Override
    public String forgotPassword(ForgotPassword forgotPassword) {
        try {
            UserActivityResponse response = performForgotPassword(forgotPassword);
            return response.getMessage();

        }catch (PaaroException ibe){
            throw new PaaroException(ibe.getMessage());
        }
    }

    @Override
    public String logout(UserDetails userDetails) {
        try {
            UserActivityResponse response  = performLogout(userDetails);
            return response.getMessage();

        }catch (PaaroException ibe){
            throw new PaaroException(ibe.getMessage());
        }
    }

    @Override
    public String changePassword(ChangePassword changePassword, HttpSession httpSession ) {
        try {
            UserDetails userDetails = (UserDetails) httpSession.getAttribute("userDetails");
            UserActivityResponse response  = resetPassword(changePassword, userDetails);
            return response.getMessage();

        }catch (PaaroException ibe){
            throw new PaaroException(ibe.getMessage());
        }
    }


    private UserDetails performLogin(LoginForm loginForm) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            String loginUrl = apiUrl + "/oauth/token?grant_type=password&username=" + loginForm.getUsername() + "&password=" + loginForm.getPassword();
            httpHeaders.set("Authorization", "Basic " + appCode);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<String> re = restTemplate.exchange(loginUrl, HttpMethod.POST, entity, String.class);

            logger.info("url ------>{}", loginUrl);
            logger.info("auth response ------>{}", re.getBody());
            return mapper.readValue(re.getBody(), UserDetails.class);

        } catch (HttpClientErrorException ex) {
            logger.info("response code {}", ex.getStatusCode().toString());
            logger.info("error is {}", ex.getMessage());
            JSONObject jsonObj = new JSONObject(ex.getResponseBodyAsString());
            throw new PaaroException(jsonObj.getString("error_description"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new PaaroException(e.getMessage());
        }
    }

    private UserActivityResponse performForgotPassword(ForgotPassword forgotPassword) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            String loginUrl = apiUrl + "/user/forgotPassword";
            httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
            String body = mapper.writeValueAsString(forgotPassword) ;
            HttpEntity<String> entity = new HttpEntity<>(body, httpHeaders);
            ResponseEntity<String> re = restTemplate.exchange(loginUrl, HttpMethod.POST, entity, String.class);

            logger.info("req body ------>{}", body);
            logger.info("auth response ------>{}", re.getBody());
            UserActivityResponse userActivityResponse = new UserActivityResponse();
            JSONObject jsonObj = new JSONObject(re.getBody());
            userActivityResponse.setMessage(jsonObj.getString("message"));
            userActivityResponse.setResponseStatus(jsonObj.getString("responseStatus"));
            return userActivityResponse;

        } catch (HttpClientErrorException ex) {
            logger.info("response code {}", ex.getStatusCode().toString());
            logger.info("error is {}", ex.getResponseBodyAsString());
            JSONObject jsonObj = new JSONObject(ex.getResponseBodyAsString());
            throw new PaaroException(jsonObj.getString("error_description"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new PaaroException(e.getMessage());
        }
    }


    private UserActivityResponse performLogout(UserDetails userDetails) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            String loginUrl = apiUrl + "/user/logout";
            httpHeaders.set("Authorization", "Bearer " + userDetails.getAccess_token());
            httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<String> re = restTemplate.exchange(loginUrl, HttpMethod.GET, entity, String.class);

            logger.info("url ------>{}", loginUrl);
            logger.info("auth response ------>{}", re.getBody());
            UserActivityResponse userActivityResponse = new UserActivityResponse();
            JSONObject jsonObj = new JSONObject(re.getBody());
            userActivityResponse.setMessage(jsonObj.getString("message"));
            userActivityResponse.setResponseStatus(jsonObj.getString("responseStatus"));
            return userActivityResponse;

        } catch (HttpClientErrorException ex) {
            logger.info("response code {}", ex.getStatusCode().toString());
            logger.info("error is {}", ex.getMessage());
            JSONObject jsonObj = new JSONObject(ex.getResponseBodyAsString());
            throw new PaaroException(jsonObj.getString("error_description"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new PaaroException(e.getMessage());
        }
    }


    private UserActivityResponse resetPassword(ChangePassword changePassword, UserDetails userDetails) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            String loginUrl = apiUrl + "/user/reset_user_password";
            httpHeaders.set("Authorization", "Bearer " + userDetails.getAccess_token());
            httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
            String body = mapper.writeValueAsString(changePassword) ;
            HttpEntity<String> entity = new HttpEntity<>(body, httpHeaders);
            ResponseEntity<String> re = restTemplate.exchange(loginUrl, HttpMethod.POST, entity, String.class);

            logger.info("url ------>{}", loginUrl);
            logger.info("auth response ------>{}", re.getBody());
            UserActivityResponse userActivityResponse = new UserActivityResponse();
            JSONObject jsonObj = new JSONObject(re.getBody());
            userActivityResponse.setMessage(jsonObj.getString("message"));
            userActivityResponse.setResponseStatus(jsonObj.getString("responseStatus"));
            return userActivityResponse;

        } catch (HttpClientErrorException ex) {
            logger.info("response code {}", ex.getStatusCode().toString());
            logger.info("error is {}", ex.getMessage());
            JSONObject jsonObj = new JSONObject(ex.getResponseBodyAsString());
            throw new PaaroException(jsonObj.getString("error_description"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new PaaroException(e.getMessage());
        }
    }
}
