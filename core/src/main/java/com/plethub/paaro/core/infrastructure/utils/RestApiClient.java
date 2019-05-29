package com.plethub.paaro.core.infrastructure.utils;

import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;

@Service
@SuppressWarnings("Duplicates")
public class RestApiClient {

    public <T,V> ResponseEntity<T> apiPostAndGetResponseEntity (String url, Class<T> claz, Object requestData,
                                                        HashMap<String,String> headers, boolean authenticate) throws Exception {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity httpEntity = getHttpEntityForRequest(requestData,headers, authenticate);
            ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.POST,httpEntity,claz);
            return responseEntity;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    public <T,V> T apiPostAndGetClass (String url, Class<T> claz, Object requestData,
                                                        HashMap<String,String> headers, boolean authenticate) throws Exception {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity httpEntity = getHttpEntityForRequest(requestData,headers, authenticate);
            ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.POST,httpEntity,claz);
            return (responseEntity!=null) ? responseEntity.getBody() : null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    public <T,V> ResponseEntity<T> apiGetAndGetResponseEntity (String url, Class<T> claz, Object requestData,
                                                        HashMap<String,String> headers, boolean authenticate) throws Exception {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity httpEntity = getHttpEntityForRequest(requestData,headers, authenticate);
            ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.GET,httpEntity,claz);
            return responseEntity;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    public <T,V> T apiGetAndGetClass (String url, Class<T> claz, Object requestData,
                                                        HashMap<String,String> headers, boolean authenticate) throws Exception {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity httpEntity = getHttpEntityForRequest(requestData,headers, authenticate);
            ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.GET,httpEntity,claz);
            return (responseEntity!=null) ? responseEntity.getBody() : null;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }




    public <T,V> T apiPostResponse (String url, Class<T> claz, Object requestData,
                                                        HashMap<String,String> headers, boolean authenticate) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity httpEntity = getHttpEntityForRequest(requestData,headers, authenticate);

        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.POST,httpEntity,claz);
        return responseEntity.getBody();
    }

    public <T,V> T apiPostResponseWithCLass (String url, Class<T> claz, Object requestData,
                                                        HashMap<String,String> headers) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.entrySet().stream().forEach(headers1->{
            httpHeaders.set(headers1.getKey(),headers1.getValue());
        });
        HttpEntity httpEntity = new HttpEntity(requestData,httpHeaders);

        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.POST,httpEntity,claz);
        return responseEntity.getBody();
    }

    public <T> ResponseEntity<T> apiGetWithHttpEntity (String url, Class<T> claz, Object requestData,
                                                        HashMap<String,String> headers) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.entrySet().stream().forEach(headers1->{
            httpHeaders.set(headers1.getKey(),headers1.getValue());
        });
        HttpEntity httpEntity = new HttpEntity(requestData,httpHeaders);

        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.GET,httpEntity,claz);
        return responseEntity;
    }

    public <T> HttpEntity getHttpEntityForRequest(Object requestData, HashMap<String,String> headers, boolean authenticate){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (authenticate) {
            httpHeaders.set(HttpHeaders.AUTHORIZATION, OAuth2AccessToken.BEARER_TYPE + " " + getBearerToken());
        }
        if (headers!=null) {
            headers.entrySet().stream().forEach(headers1->{
                httpHeaders.set(headers1.getKey(),headers1.getValue());
            });
        }
        if (requestData == null) {
            return new HttpEntity(httpHeaders);
        }
        HttpEntity httpEntity = new HttpEntity(requestData,httpHeaders);
        return httpEntity;
    }

    public String getBearerToken() {
        OAuth2Authentication auth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationDetails auth2AuthenticationDetails = (OAuth2AuthenticationDetails) auth2Authentication.getDetails();
        return auth2AuthenticationDetails.getTokenValue();
    }



    public <T> ResponseEntity<T> apiPostWithHttpEntityt(String url, HttpEntity httpEntity, Class<T> claz){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.POST,httpEntity,claz,new Object[0]);
        return responseEntity;
    }

}
