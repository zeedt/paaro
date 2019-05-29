package com.plethub.paaro.core.appservice.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plethub.paaro.core.appservice.apirequestmodel.GeneralSettingRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.GeneralSettingsResponse;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.Status;
import com.plethub.paaro.core.models.GeneralSetting;
import com.plethub.paaro.core.appservice.repository.GeneralSettingRepository;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.models.AuditLog;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public class GeneralSettingService {

    private Logger logger = LoggerFactory.getLogger(GeneralSettingService.class.getName());

    @Autowired
    private AuditLogService auditLogService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private GeneralSettingRepository generalSettingRepository;

    public GeneralSetting fetchSetting () {

        return generalSettingRepository.findTopByIdIsNotNull();

    }

    public GeneralSettingsResponse createOrUpdateGeneralSettings(GeneralSetting generalSetting, HttpServletRequest servletRequest) {

        try {
            GeneralSetting existingGeneralSetting = fetchSetting();

            GeneralSettingsResponse generalSettingsResponse = new GeneralSettingsResponse();

            if (existingGeneralSetting == null) {
                generalSetting.setStatus(Status.SETTING_AWAITING_VERIFICATION);
                generalSettingRepository.save(generalSetting);
                generalSettingsResponse.setGeneralSetting(generalSetting);
                generalSettingsResponse.setMessage("Successfully updated settings");
                generalSettingsResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
                String newData = objectMapper.writeValueAsString(generalSetting);
                auditLogService.saveAudit(null,newData, Module.SETTINGS,servletRequest,"User created general settings");
            } else {
                String oldData = objectMapper.writeValueAsString(existingGeneralSetting);
                existingGeneralSetting.setStatus(Status.SETTING_AWAITING_VERIFICATION);
                existingGeneralSetting.setChargeRate(generalSetting.getChargeRate());
                existingGeneralSetting.setComment(generalSetting.getComment());
                existingGeneralSetting.setEnableEmailAlert(generalSetting.isEnableEmailAlert());
                existingGeneralSetting.setEnableSmsAlert(generalSetting.isEnableSmsAlert());
                generalSettingRepository.save(existingGeneralSetting);
                String newData = objectMapper.writeValueAsString(existingGeneralSetting);

                generalSettingsResponse.setGeneralSetting(existingGeneralSetting);
                generalSettingsResponse.setMessage("Successfully updated settings");
                generalSettingsResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
                auditLogService.saveAudit(oldData,newData, Module.SETTINGS,servletRequest,"User updated general settings");
            }

            return generalSettingsResponse;
        } catch (Exception e) {
            logger.error("Error occurred due to ", e);
            return GeneralSettingsResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR,"System error occurred");
        }

    }

    public GeneralSettingsResponse verifyGeneralSettings(GeneralSettingRequest generalSettingRequest, HttpServletRequest servletRequest) {

        try {
            if (generalSettingRequest == null || StringUtils.isEmpty(generalSettingRequest.getComment())) {
                return GeneralSettingsResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "COmment cannot be null");
            }

            GeneralSetting generalSetting = fetchSetting();

            if (generalSetting == null) {
                return GeneralSettingsResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "No settings found");
            }

            if (generalSetting.getStatus() != Status.SETTING_AWAITING_VERIFICATION) {
                return GeneralSettingsResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Setting not awaiting verification");
            }
            String oldData = objectMapper.writeValueAsString(generalSetting);
            generalSetting.setComment(generalSettingRequest.getComment());
            generalSetting.setStatus(Status.VERIFIED);

            generalSettingRepository.save(generalSetting);
            String newData = objectMapper.writeValueAsString(generalSetting);
            GeneralSettingsResponse generalSettingsResponse = new GeneralSettingsResponse();

            generalSettingsResponse.setGeneralSetting(generalSetting);
            generalSettingsResponse.setMessage("Successfully Verified settings");
            generalSettingsResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            auditLogService.saveAudit(oldData,newData, Module.SETTINGS,servletRequest,"User verified general settings");

            return generalSettingsResponse;
        } catch (JsonProcessingException e) {
            logger.error("Error occurred due to ", e);
            return GeneralSettingsResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System  error occurred while verifying settings");
        }

    }

    public GeneralSettingsResponse declineGeneralSettings(GeneralSettingRequest generalSettingRequest, HttpServletRequest servletRequest) {

        try {
            if (generalSettingRequest == null || StringUtils.isEmpty(generalSettingRequest.getComment())) {
                return GeneralSettingsResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Comment cannot be null");
            }

            GeneralSetting generalSetting = fetchSetting();

            if (generalSetting == null) {
                return GeneralSettingsResponse.returnResponseWithCode(ApiResponseCode.NOT_FOUND, "No settings found");
            }

            if (generalSetting.getStatus() != Status.SETTING_AWAITING_VERIFICATION) {
                return GeneralSettingsResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Setting not awaiting verification");
            }
            String oldData = objectMapper.writeValueAsString(generalSetting);
            GeneralSetting auditedData = getLastAuditedGeneralSetting();
            if (auditedData == null) {
                return GeneralSettingsResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "Unable to retrieve the last audited settings");
            }
            generalSetting.setComment(generalSettingRequest.getComment());
            generalSetting.setEnableSmsAlert(auditedData.isEnableSmsAlert());
            generalSetting.setEnableEmailAlert(auditedData.isEnableEmailAlert());
            generalSetting.setChargeRate(auditedData.getChargeRate());
            generalSetting.setStatus(Status.DECLINED);

            generalSettingRepository.save(generalSetting);
            String newData = objectMapper.writeValueAsString(generalSetting);
            GeneralSettingsResponse generalSettingsResponse = new GeneralSettingsResponse();

            generalSettingsResponse.setGeneralSetting(generalSetting);
            generalSettingsResponse.setMessage("Successfully declined settings");
            generalSettingsResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
            auditLogService.saveAudit(oldData,newData, Module.SETTINGS,servletRequest,"User declined general settings");

            return generalSettingsResponse;
        } catch (JsonProcessingException e) {
            logger.error("Error occurred due to ", e);
            return GeneralSettingsResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System  error occurred while declining settings");
        }

    }

    public GeneralSetting getLastAuditedGeneralSetting() {

        try {
            AuditLog auditLog = auditLogService.getLastAuditedByModule(Module.SETTINGS);

            if (auditLog == null) {
                return null;
            }

            String initialData = auditLog.getInitialData();

            return objectMapper.readValue(initialData,GeneralSetting.class);

        } catch (IOException e) {
            logger.error("Error ocuured due to ", e);
            return null;
        }

    }

}
