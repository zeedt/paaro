package com.plethub.paaro.core.appservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plethub.paaro.core.appservice.apirequestmodel.CurrencyDetailsRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.CurrencyDetailsresponse;
import com.plethub.paaro.core.appservice.enums.Action;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.Status;
import com.plethub.paaro.core.appservice.repository.CurrencyRepository;
import com.plethub.paaro.core.appservice.repository.RequestRepository;
import com.plethub.paaro.core.appservice.repository.WalletTransferTransactionRepository;
import com.plethub.paaro.core.configuration.service.ConfigurationService;
import com.plethub.paaro.core.infrastructure.utils.GeneralUtil;
import com.plethub.paaro.core.models.Currency;
import com.plethub.paaro.core.models.CurrencyDetails;
import com.plethub.paaro.core.appservice.repository.CurrencyDetailsRepository;
import com.plethub.paaro.core.models.ManagedUser;
import com.plethub.paaro.core.models.Request;
import com.plethub.paaro.core.request.apimodel.RequestResponse;
import com.plethub.paaro.core.request.service.RequestService;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import com.plethub.paaro.core.usermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
public class CurrencyDetailsService {

    @Autowired
    private CurrencyDetailsRepository currencyDetailsRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestService requestService;

    @Autowired
    private WalletTransferTransactionRepository transferTransactionRepository;

    @Value("${currency.naira-type:GBP}")
    private String gbpCurrency;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Logger logger = LoggerFactory.getLogger(CurrencyDetailsService.class.getName());

    @Autowired
    private CurrencyRepository currencyRepository;

    public CurrencyDetailsresponse getCurrencydetailsByCode(String currencyCode) {

        try {
            if (StringUtils.isEmpty(currencyCode)) {
                return CurrencyDetailsresponse.fromNarration(ApiResponseCode.INVALID_REQUEST, "Currency code cannot be blank");
            }

            CurrencyDetails currencyDetails = currencyDetailsRepository.findByCurrencyCode(currencyCode);

            if (currencyDetails == null) {
                return CurrencyDetailsresponse.fromNarration(ApiResponseCode.NULL_RESPONSE, String.format("Currency details not found for the code %s supplied",currencyCode));
            }

            CurrencyDetailsresponse currencyDetailsresponse = new CurrencyDetailsresponse();
            currencyDetailsresponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
            currencyDetailsresponse.setCurrencyDetails(currencyDetails);

            return currencyDetailsresponse;
        } catch (Exception e) {
            logger.error("Error occurred while fetching currency details by code due to ", e);
            return CurrencyDetailsresponse.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching currency details by code");
        }

    }

    public CurrencyDetailsresponse getAllCurrencydetails() {

        try {
            List<CurrencyDetails> currencyDetails = currencyDetailsRepository.findAll();

            if (CollectionUtils.isEmpty(currencyDetails)) {
                return CurrencyDetailsresponse.fromNarration(ApiResponseCode.NULL_RESPONSE, "No currency details found");
            }

            CurrencyDetailsresponse currencyDetailsresponse = new CurrencyDetailsresponse();
            currencyDetailsresponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
            currencyDetailsresponse.setCurrencyDetailsList(currencyDetails);

            return currencyDetailsresponse;
        } catch (Exception e) {
            logger.error("Error occurred while fetching currency details due to ", e);
            return CurrencyDetailsresponse.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching currency details ");
        }

    }

    public CurrencyDetailsresponse addCurrencyDetailsForCurrency(CurrencyDetailsRequest currencyDetailsRequest, HttpServletRequest servletRequest) {

        try {

            CurrencyDetailsresponse validationresponse = validateNewCurrencyDetailsRequest(currencyDetailsRequest);

            if (validationresponse != null) return validationresponse;

            if (configurationService.isMakerCheckerEnabledForAction(Action.ADD_CURRENCY_DETAILS)) return createAddCurrencyDetailsRequestAndAudit(currencyDetailsRequest, servletRequest);

            return createCurrencyDetailsAndAudit(currencyDetailsRequest, servletRequest);

        } catch (Exception e) {
            logger.error("Error occurred while adding currency details due to ", e);
            return CurrencyDetailsresponse.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while adding currency details ");
        }

    }

    public CurrencyDetailsresponse updateCurrencyDetailsForCurrency(CurrencyDetailsRequest currencyDetailsRequest, HttpServletRequest servletRequest) {

        try {

            CurrencyDetailsresponse validationresponse = validateCurrencyDetailsUpdateRequest(currencyDetailsRequest);

            if (validationresponse != null && validationresponse.getApiResponseCode() != ApiResponseCode.SUCCESSFUL) return validationresponse;

            if (requestService.isRequestPending(Action.UPDATE_CURRENCY_DETAILS, validationresponse.getCurrencyDetails().getId())) return CurrencyDetailsresponse.fromNarration(ApiResponseCode.PENDING_VERIFICATION, "Item already pending verification");

            if (configurationService.isMakerCheckerEnabledForAction(Action.UPDATE_CURRENCY_DETAILS)) return createUpdateCurrencyDetailsRequestAndAudit(currencyDetailsRequest, validationresponse.getCurrencyDetails(), servletRequest);

            return updateCurrencyDetailsAndAudit(currencyDetailsRequest, validationresponse.getCurrencyDetails(), servletRequest);

        } catch (Exception e) {
            logger.error("Error occurred while updating currency details due to ", e);
            return CurrencyDetailsresponse.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while updating currency details ");
        }

    }



    private CurrencyDetailsresponse createAddCurrencyDetailsRequestAndAudit(CurrencyDetailsRequest currencyDetailsRequest, HttpServletRequest servletRequest) throws JsonProcessingException {

        String message = requestService.logRequest(null, objectMapper.writeValueAsString(currencyDetailsRequest), Action.ADD_CURRENCY_DETAILS, null);

        if (!StringUtils.isEmpty(message)) return CurrencyDetailsresponse.fromNarration(ApiResponseCode.FAILED, message);

        auditLogService.saveAudit(null,objectMapper.writeValueAsString(currencyDetailsRequest), Module.CURRENCY_DETAILS, servletRequest,"User Added currency details for verification");

        return CurrencyDetailsresponse.fromNarration(ApiResponseCode.SUCCESSFUL, "Currency details added for verification");

    }


    private CurrencyDetailsresponse createUpdateCurrencyDetailsRequestAndAudit(CurrencyDetailsRequest currencyDetailsRequest, CurrencyDetails existingCurrencyDetails, HttpServletRequest servletRequest) throws JsonProcessingException {

        String message = requestService.logRequest(existingCurrencyDetails.getId(), Action.UPDATE_CURRENCY_DETAILS, objectMapper.writeValueAsString(existingCurrencyDetails), objectMapper.writeValueAsString(currencyDetailsRequest));

        if (!StringUtils.isEmpty(message)) return CurrencyDetailsresponse.fromNarration(ApiResponseCode.FAILED, message);

        auditLogService.saveAudit(objectMapper.writeValueAsString(existingCurrencyDetails),objectMapper.writeValueAsString(currencyDetailsRequest), Module.CURRENCY_DETAILS, servletRequest,"User updated currency details for verification", existingCurrencyDetails.getId());

        return CurrencyDetailsresponse.fromNarration(ApiResponseCode.SUCCESSFUL, "Currency details updated for verification");

    }

    private CurrencyDetailsresponse createCurrencyDetailsAndAudit(CurrencyDetailsRequest currencyDetailsRequest, HttpServletRequest servletRequest) throws JsonProcessingException {

        CurrencyDetails currencyDetails = new CurrencyDetails();

        currencyDetails.setBuyingRate(currencyDetailsRequest.getBuyingRate());
        currencyDetails.setAmountSearchDeviation(currencyDetailsRequest.getAmountSearchDeviation());
        currencyDetails.setSellingRate(currencyDetailsRequest.getSellingRate());
        currencyDetails.setCurrencyCode(currencyDetailsRequest.getCurrencyCode());
        currencyDetails.setFlatChargeEnabled(currencyDetailsRequest.isFlatChargeEnabled());
        currencyDetails.setFlatChargeRate(currencyDetailsRequest.getFlatChargeRate());
        currencyDetails.setTransactionFeePerInternationalCurrencyInNaira(currencyDetailsRequest.getTransactionFeePerInternationalCurrencyInNaira());
        currencyDetails.setRateSearchDeviation(currencyDetailsRequest.getRateSearchDeviation());
        currencyDetails.setServiceChargeCap(currencyDetailsRequest.getServiceChargeCap());

        currencyDetailsRepository.save(currencyDetails);
        auditLogService.saveAudit(null,objectMapper.writeValueAsString(currencyDetails), Module.CURRENCY_DETAILS, servletRequest,"User Added currency details", currencyDetails.getId());

        return CurrencyDetailsresponse.fromNarration(ApiResponseCode.SUCCESSFUL, "Successfully added currency details for currency");

    }

    private CurrencyDetailsresponse updateCurrencyDetailsAndAudit(CurrencyDetailsRequest currencyDetailsRequest, CurrencyDetails currencyDetails, HttpServletRequest servletRequest) throws JsonProcessingException {

        CurrencyDetails oldCurrencyDetails = currencyDetails;

        currencyDetails.setBuyingRate(currencyDetailsRequest.getBuyingRate());
        currencyDetails.setAmountSearchDeviation(currencyDetailsRequest.getAmountSearchDeviation());
        currencyDetails.setSellingRate(currencyDetailsRequest.getSellingRate());
        currencyDetails.setFlatChargeEnabled(currencyDetailsRequest.isFlatChargeEnabled());
        currencyDetails.setFlatChargeRate(currencyDetailsRequest.getFlatChargeRate());
        currencyDetails.setPercentageChargeRate(currencyDetailsRequest.getPercentageChargeRate());
        currencyDetails.setTransactionFeePerInternationalCurrencyInNaira(currencyDetailsRequest.getTransactionFeePerInternationalCurrencyInNaira());
        currencyDetails.setRateSearchDeviation(currencyDetailsRequest.getRateSearchDeviation());
        currencyDetails.setServiceChargeCap(currencyDetailsRequest.getServiceChargeCap());
        currencyDetails.setCalculateChargeInNaira(currencyDetailsRequest.isCalculateChargeInNaira());

        currencyDetailsRepository.save(currencyDetails);
        auditLogService.saveAudit(objectMapper.writeValueAsString(oldCurrencyDetails),objectMapper.writeValueAsString(currencyDetails), Module.CURRENCY_DETAILS, servletRequest,"User updated currency details", currencyDetails.getId());

        return CurrencyDetailsresponse.fromNarration(ApiResponseCode.SUCCESSFUL, "Successfully updated currency details for currency");

    }

    private CurrencyDetailsresponse validateNewCurrencyDetailsRequest(CurrencyDetailsRequest currencyDetailsRequest) {

        if (currencyDetailsRequest == null) return CurrencyDetailsresponse.fromNarration(ApiResponseCode.INVALID_REQUEST, "Request cannot be null");

        if (currencyDetailsRequest.getBuyingRate() == null || currencyDetailsRequest.getSellingRate() == null || currencyDetailsRequest.getServiceChargeCap() == null
                || currencyDetailsRequest.getFlatChargeRate() == null || StringUtils.isEmpty(currencyDetailsRequest.getCurrencyCode()) ||
                currencyDetailsRequest.getAmountSearchDeviation() == null || currencyDetailsRequest.getTransactionFeePerInternationalCurrencyInNaira() == null
                || currencyDetailsRequest.getRateSearchDeviation() == null
                )
            return CurrencyDetailsresponse.fromNarration(ApiResponseCode.INVALID_REQUEST, "Buying rate, selling rate, service charge cap, flat charge rate, currency code, transaction fee, amount search deviation and rate search deviation cannot be null");

        if (currencyDetailsRequest.getBuyingRate() < 1d || currencyDetailsRequest.getSellingRate() < 1d || currencyDetailsRequest.getServiceChargeCap() < 1d
                || currencyDetailsRequest.getFlatChargeRate() < 1d || currencyDetailsRequest.getAmountSearchDeviation() < 1d
                || currencyDetailsRequest.getTransactionFeePerInternationalCurrencyInNaira() < 0d || currencyDetailsRequest.getRateSearchDeviation() < 1d
                )
            return CurrencyDetailsresponse.fromNarration(ApiResponseCode.INVALID_REQUEST, "Buying rate, selling rate, service charge cap, flat charge rate," +
                    " amount search deviation and rate search deviation cannot be less than 1. Also, transaction fee must be greater than 0");


        Currency currency = currencyRepository.findCurrencyByType(currencyDetailsRequest.getCurrencyCode().trim());

        if (currency == null) return CurrencyDetailsresponse.fromNarration(ApiResponseCode.NOT_FOUND, "Specified currency not found");

        CurrencyDetails currencyDetails = currencyDetailsRepository.findByCurrencyCode(currencyDetailsRequest.getCurrencyCode().trim());

        if (currencyDetails != null)
            return CurrencyDetailsresponse.fromNarration(ApiResponseCode.ALREADY_EXIST, "Currency details already exist for currency. Kindly update if necessary");

        return null;

    }
    private CurrencyDetailsresponse validateCurrencyDetailsUpdateRequest(CurrencyDetailsRequest currencyDetailsRequest) {

        if (currencyDetailsRequest == null) return CurrencyDetailsresponse.fromNarration(ApiResponseCode.INVALID_REQUEST, "Request cannot be null");

        if (currencyDetailsRequest.getBuyingRate() == null || currencyDetailsRequest.getSellingRate() == null || currencyDetailsRequest.getServiceChargeCap() == null
                || currencyDetailsRequest.getFlatChargeRate() == null || currencyDetailsRequest.getRateSearchDeviation() == null ||
                currencyDetailsRequest.getAmountSearchDeviation() == null || currencyDetailsRequest.getTransactionFeePerInternationalCurrencyInNaira() == null
                )
            return CurrencyDetailsresponse.fromNarration(ApiResponseCode.INVALID_REQUEST, "ID, Buying rate, selling rate, service charge cap, flat charge rate, transaction fee, amount search deviation and rate search deviation cannot be null");

        if (currencyDetailsRequest.getBuyingRate() < 1d || currencyDetailsRequest.getSellingRate() < 1d || currencyDetailsRequest.getServiceChargeCap() < 1d
                || currencyDetailsRequest.getFlatChargeRate() < 1d || currencyDetailsRequest.getAmountSearchDeviation() < 1d
                || currencyDetailsRequest.getTransactionFeePerInternationalCurrencyInNaira() < 0d || currencyDetailsRequest.getRateSearchDeviation() < 1d
                )
            return CurrencyDetailsresponse.fromNarration(ApiResponseCode.INVALID_REQUEST, "Buying rate, selling rate, service charge cap, flat charge rate, " +
                    " amount search deviation and rate search deviation cannot be less than 1. Also, transaction fee must be greater than 0");


        CurrencyDetails currencyDetails = currencyDetailsRepository.findByCurrencyCode(currencyDetailsRequest.getCurrencyCode().trim());

        if (currencyDetails == null)
            return CurrencyDetailsresponse.fromNarration(ApiResponseCode.NOT_FOUND, "Currency details not found. Kindly create if necessary");


        CurrencyDetailsresponse currencyDetailsresponse = new CurrencyDetailsresponse();
        currencyDetailsresponse.setCurrencyDetails(currencyDetails);
        currencyDetailsresponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        currencyDetailsRequest.setId(currencyDetails.getId());

        return currencyDetailsresponse;

    }

    public RequestResponse verifyCurrencyDetailsAddition(Request request, HttpServletRequest servletRequest) {

        try {
            CurrencyDetails currencyDetails = new CurrencyDetails();
            CurrencyDetailsRequest currencyDetailsRequest = objectMapper.readValue(request.getNewData(), CurrencyDetailsRequest.class);

            BeanUtils.copyProperties(currencyDetailsRequest, currencyDetails);

            if (currencyDetails == null || StringUtils.isEmpty(currencyDetails.getCurrencyCode())) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Invalid object");

            if (currencyDetailsRepository.findByCurrencyCode(currencyDetails.getCurrencyCode()) != null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.ALREADY_EXIST, "Currency details already exists for currency");

            currencyDetails = currencyDetailsRepository.save(currencyDetails);
            ManagedUser currentlyLoggedInUser = userService.getCurrentLoggedInUser();

            if (currentlyLoggedInUser == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "No current user found");

            request.setVerifierId(currentlyLoggedInUser);
            request.setStatus(Status.VERIFIED);
            requestRepository.save(request);
            auditLogService.saveAudit(null,objectMapper.writeValueAsString(currencyDetails), Module.CURRENCY_DETAILS, servletRequest,"User verified currency details addition", currencyDetails.getId());

            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "User successfully verified currency details addition request");

        } catch (IOException e) {
            logger.error("System error occurred while verifying currency details addition due to ", e);
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred");
        }

    }
    public RequestResponse verifyCurrencyDetailsUpdate(Request request, HttpServletRequest servletRequest) {

        try {
            CurrencyDetailsRequest currencyDetailsRequest = objectMapper.readValue(request.getNewData(), CurrencyDetailsRequest.class);

            if (currencyDetailsRequest == null || currencyDetailsRequest.getId() == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Invalid object");

            CurrencyDetails currencyDetails = currencyDetailsRepository.getOne(currencyDetailsRequest.getId());
            CurrencyDetails oldCurrencyDetails = currencyDetails;
            BeanUtils.copyProperties(currencyDetailsRequest, currencyDetails);

            if (currencyDetails == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Currency details not found");

            currencyDetails = currencyDetailsRepository.save(currencyDetails);
            ManagedUser currentlyLoggedInUser = userService.getCurrentLoggedInUser();

            if (currentlyLoggedInUser == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "No current user found");

            request.setVerifierId(currentlyLoggedInUser);
            request.setStatus(Status.VERIFIED);
            requestRepository.save(request);
            auditLogService.saveAudit(objectMapper.writeValueAsString(oldCurrencyDetails),objectMapper.writeValueAsString(currencyDetails), Module.CURRENCY_DETAILS, servletRequest,"User verified currency details addition", currencyDetails.getId());

            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "User successfully verified currency details update request");

        } catch (IOException e) {
            logger.error("System error occurred while verifying currency details update due to ", e);
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred");
        }

    }

    public CurrencyDetailsresponse getGbpAverageConversionRate() {

        try {
            CurrencyDetailsresponse currencyDetailsresponse = new CurrencyDetailsresponse();
            BigDecimal averageRate = transferTransactionRepository.getDailyAverageExchangeRateByCurrency(gbpCurrency, GeneralUtil.getStartOfCurrentDay(), new Date());
            averageRate = (averageRate != null) ? averageRate.setScale(2, RoundingMode.HALF_EVEN) : BigDecimal.valueOf(0);
            currencyDetailsresponse.setGbpAverageExchangeRate(averageRate);
            currencyDetailsresponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);

            return currencyDetailsresponse;
        } catch (Exception e) {
            logger.error("Error occurred while getting average exchange rate for gbp due to ", e);
            return CurrencyDetailsresponse.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while getting average exchange rate for gbp");
        }
    }

}
