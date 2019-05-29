package com.plethub.paaro.core.appservice.kudisms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class KudiSmsApi {


    @Value("${kudi.sms-url:https://account.kudisms.net/api/?}")
    private String kudiSmsBaseEndpoint;

    @Value("${kudi.sms-username:wsowunmi@plethub.com}")
    private String kudiSmsUsername;

    @Value("${kudi.sms-password:P@55w0rd!}")
    private String kudiSmsPassword;

    @Value("${kudi.sms-sender:Paaro Trade}")
    private String kudiSmsSender;

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void init() {
        restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());
    }


    private Logger logger = LoggerFactory.getLogger(KudiSmsApi.class.getName());

    public void sendSms(Smsrequest smsrequest) throws IOException {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(new MediaType[]{MediaType.ALL}));
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);
        String concatenatedNumbers = concatenatePhoneNumbers(smsrequest);

        if (StringUtils.isEmpty(concatenatedNumbers) || StringUtils.isEmpty(smsrequest.getMessage())) {
            return;
        }
        concatenatedNumbers = concatenatedNumbers.replaceFirst(",","");
        String message = smsrequest.getMessage();
        String fullUrl = String.format("%susername=%s&password=%s&sender=%s&message=%s&mobiles=%s",kudiSmsBaseEndpoint,kudiSmsUsername,kudiSmsPassword,kudiSmsSender,message,concatenatedNumbers);

        SmsResponse smsResponse = restTemplate.getForObject(fullUrl,SmsResponse.class);

        logger.info(String.format("Status response is %s", smsResponse.getStatus()));
        logger.info(String.format("Error response is %s", smsResponse.getError()));

    }

    private String concatenatePhoneNumbers(Smsrequest smsrequest) {

        String concatenatedNos = "";

        if (smsrequest == null || CollectionUtils.isEmpty(smsrequest.getPhoneNumbers()) ) {
            return null;
        }

        for (String number : smsrequest.getPhoneNumbers()) {
            if (number == null) {
                continue;
            }
            concatenatedNos = String.format("%s,%s",concatenatedNos,number);
        }

        return concatenatedNos;
    }

}
