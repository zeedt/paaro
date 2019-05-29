package com.plethub.paaro.core.appservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plethub.paaro.core.appservice.apirequestmodel.CurrencyRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.CurrencyDetailsresponse;
import com.plethub.paaro.core.appservice.apiresponsemodel.CurrencyResponse;
import com.plethub.paaro.core.appservice.enums.Action;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.Status;
import com.plethub.paaro.core.appservice.repository.RequestRepository;
import com.plethub.paaro.core.configuration.service.ConfigurationService;
import com.plethub.paaro.core.models.*;
import com.plethub.paaro.core.appservice.repository.CurrencyRepository;
import com.plethub.paaro.core.request.apimodel.RequestResponse;
import com.plethub.paaro.core.request.service.RequestService;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import com.plethub.paaro.core.usermanagement.service.UserService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private RequestRepository requestRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RequestService requestService;

    private Logger logger = LoggerFactory.getLogger(CurrencyService.class.getName());

    @Transactional
    public CurrencyResponse addCurrency(CurrencyRequest currencyRequest, HttpServletRequest servletRequest) throws JsonProcessingException {

        if (currencyRequest == null ) {
            return CurrencyResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request");
        }

        if (StringUtils.isEmpty(currencyRequest.getType()) || StringUtils.isEmpty(currencyRequest.getDescription())) {

            return CurrencyResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type and description cannot be blank.");

        }

        Currency currency = currencyRepository.findCurrencyByType(currencyRequest.getType().trim());

        if (currency != null) {
            return CurrencyResponse.returnResponseWithCode(ApiResponseCode.ALREADY_EXIST, "Currency already exist");
        }

        if (configurationService.isMakerCheckerEnabledForAction(Action.ADD_CURRENCY)) {
            return saveAddCurrencyRequestForVerification(currencyRequest, servletRequest);
        }

        return saveCurrencyAndAudit(currencyRequest,servletRequest);

    }



    public CurrencyResponse disableCurrency(String currencyType, HttpServletRequest servletRequest) throws JsonProcessingException {

        if (StringUtils.isEmpty(currencyType)) return CurrencyResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank.");


        Currency currency = currencyRepository.findCurrencyByType(currencyType.trim());

        if (currency == null) return CurrencyResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Currency not found");

        if (requestService.isRequestPending(Action.DISABLE_CURRENCY, currency.getId())) return CurrencyResponse.returnResponseWithCode(ApiResponseCode.PENDING_VERIFICATION, "There's a pending verification on this item");

        if (currency.isDisabled()) return CurrencyResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency already disabled");



        if (configurationService.isMakerCheckerEnabledForAction(Action.DISABLE_CURRENCY)) return saveDisableCurrencyRequestForVerification(currency, servletRequest);


        Currency newCurrency = currency;
        newCurrency.setDisabled(true);

        auditLogService.saveAudit(objectMapper.writeValueAsString(currency),objectMapper.writeValueAsString(newCurrency), Module.CURRENCY,servletRequest,"User disabled currency");

        currencyRepository.save(newCurrency);

        return CurrencyResponse.returnResponseWithCode(ApiResponseCode.SUCCESSFUL, "Currency disabled");
    }

    public CurrencyResponse enableCurrency(String currencyType, HttpServletRequest servletRequest) throws JsonProcessingException {

        if (StringUtils.isEmpty(currencyType)) return CurrencyResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank.");


        Currency currency = currencyRepository.findCurrencyByType(currencyType.trim());

        if (currency == null) return CurrencyResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "Currency not found");

        if (requestService.isRequestPending(Action.ENABLE_CURRENCY, currency.getId())) return CurrencyResponse.returnResponseWithCode(ApiResponseCode.PENDING_VERIFICATION, "There's a pending verification on this item");

        if (!currency.isDisabled()) return CurrencyResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency already enabled");



        if (configurationService.isMakerCheckerEnabledForAction(Action.ENABLE_CURRENCY)) return saveEnableCurrencyRequestForVerification(currency, servletRequest);


        Currency newCurrency = currency;
        newCurrency.setDisabled(false);

        auditLogService.saveAudit(objectMapper.writeValueAsString(currency),objectMapper.writeValueAsString(newCurrency), Module.CURRENCY,servletRequest,"User enabled currency");

        currencyRepository.save(newCurrency);

        return CurrencyResponse.returnResponseWithCode(ApiResponseCode.SUCCESSFUL, "Currency enabled");
    }


    private CurrencyResponse saveDisableCurrencyRequestForVerification(Currency currency, HttpServletRequest servletRequest) {

        String message = requestService.logRequest(currency.getId(), Action.DISABLE_CURRENCY, Boolean.toString(currency.isDisabled()), Boolean.toString(false));

        if (!StringUtils.isEmpty(message)) {
            return CurrencyResponse.returnResponseWithCode(ApiResponseCode.FAILED, message);
        }

        auditLogService.saveAudit(Boolean.toString(currency.isDisabled()), Boolean.toString(false), Module.CURRENCY, servletRequest, "User disable currency for verification");

        return CurrencyResponse.returnResponseWithCode(ApiResponseCode.SUCCESSFUL, "Disable request successfully created");


    }

    private CurrencyResponse saveEnableCurrencyRequestForVerification(Currency currency, HttpServletRequest servletRequest) {

        String message = requestService.logRequest(currency.getId(), Action.ENABLE_CURRENCY, Boolean.toString(currency.isDisabled()), Boolean.toString(false));

        if (!StringUtils.isEmpty(message)) {
            return CurrencyResponse.returnResponseWithCode(ApiResponseCode.FAILED, message);
        }

        auditLogService.saveAudit(Boolean.toString(currency.isDisabled()), Boolean.toString(true), Module.CURRENCY, servletRequest, "User enable currency for verification", currency.getId());

        return CurrencyResponse.returnResponseWithCode(ApiResponseCode.SUCCESSFUL, "Enable request successfully created");


    }

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public List<Currency> getActiveCurrencies() {
        return currencyRepository.findByDisabled(false);
    }

    public Page<Currency> getAllCurrencies(Pageable pageable) {
        return currencyRepository.findAll(pageable);
    }

    public CurrencyResponse saveCurrencyAndAudit(CurrencyRequest currencyRequest, HttpServletRequest servletRequest) throws JsonProcessingException {
        Currency newCurrency = new Currency();
        newCurrency.setDescription(currencyRequest.getDescription());
        newCurrency.setType(currencyRequest.getType());

        currencyRepository.save(newCurrency);
        CurrencyResponse currencyResponse = new CurrencyResponse();
        currencyResponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        currencyResponse.setCurrency(newCurrency);
        currencyResponse.setMessage("Successfully added currency");

        auditLogService.saveAudit(null,objectMapper.writeValueAsString(newCurrency), Module.CURRENCY,servletRequest,"User Added new currency");

        return currencyResponse;
    }

    private CurrencyResponse saveAddCurrencyRequestForVerification(CurrencyRequest currencyRequest, HttpServletRequest servletRequest) {

        try {

            String newData = objectMapper.writeValueAsString(currencyRequest);

            String message = requestService.logRequest(null,newData, Action.ADD_CURRENCY, null);

            if (!StringUtils.isEmpty(message)) {
                return CurrencyResponse.returnResponseWithCode(ApiResponseCode.FAILED, message);
            }

            auditLogService.saveAudit(null, newData, Module.CURRENCY, servletRequest, "User added currency for verification");

            return CurrencyResponse.returnResponseWithCode(ApiResponseCode.SUCCESSFUL, "Successfully created currency request for verification");
        } catch (Exception e) {
            logger.error("Error occurred while adding authorities to user ", e);
            return CurrencyResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while creating currency request");

        }


    }

    public CurrencyResponse getCurrencyByCode(String currencyCode) {

        try {
            if (StringUtils.isEmpty(currencyCode)) {
                return CurrencyResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency code cannot be blank");
            }

            Currency currency = currencyRepository.findCurrencyByType(currencyCode);

            if (currency == null) {
                return CurrencyResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, String.format("Currency details not found for the code %s supplied",currencyCode));
            }

            CurrencyResponse currencyDetailsresponse = new CurrencyResponse();
            currencyDetailsresponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
            currencyDetailsresponse.setCurrency(currency);

            return currencyDetailsresponse;
        } catch (Exception e) {
            logger.error("Error occurred while fetching currency details by code due to ", e);
            return CurrencyResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching currency details by code");
        }

    }

    public RequestResponse verifyCurrencyAddition(Request request, HttpServletRequest servletRequest) {

        try {
            CurrencyRequest currencyRequest = objectMapper.readValue(request.getNewData(), CurrencyRequest.class);
            if (currencyRequest == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Request invalid");

            Currency currency = new Currency();
            currency.setDescription(currencyRequest.getDescription());
            currency.setType(currencyRequest.getType());
            currency = currencyRepository.save(currency);
            request.setStatus(Status.VERIFIED);

            ManagedUser currentlyLoggedInUser = userService.getCurrentLoggedInUser();

            if (currentlyLoggedInUser == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "No current user found");

            request.setVerifierId(currentlyLoggedInUser);
            requestRepository.save(request);
            auditLogService.saveAudit(null,objectMapper.writeValueAsString(currency), Module.CURRENCY,servletRequest,"User Verified currency", currency.getId());
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "Currency successfully verified");

        } catch (Exception e) {

            logger.error("Error occurred while verifying currency addition due to ", e);
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "System error occurred while verifying currency");

        }

    }

    public RequestResponse verifyCurrencyDeactivationRequest(Request request, HttpServletRequest servletRequest) {

        try {
            if (request == null || request.getEntityId() == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request");

            Currency currency  = currencyRepository.findOne(request.getEntityId());

            if (currency == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Currency not found");

            currency.setDisabled(true);
            auditLogService.saveAudit("","",Module.CURRENCY, servletRequest, "Currency deactivated successfully", request.getEntityId());
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "Currency deactivation successfully verified");
        } catch (Exception e) {
            logger.error("Error occurred while verifying currency deactivation request due to ", e);
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while verifying currency deactivation");
        }

    }
    public RequestResponse verifyCurrencyActivationRequest(Request request, HttpServletRequest servletRequest) {

        try {
            if (request == null || request.getEntityId() == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request");

            Currency currency  = currencyRepository.findOne(request.getEntityId());

            if (currency == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Currency not found");

            currency.setDisabled(false);
            auditLogService.saveAudit("","",Module.CURRENCY, servletRequest, "Currency activated successfully", request.getEntityId());
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "Currency activation successfully verified");
        } catch (Exception e) {
            logger.error("Error occurred while verifying currency activation request due to ", e);
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while verifying currency activation");
        }

    }

}
