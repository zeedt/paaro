package com.plethub.paaro.core.audit.controller;

import com.plethub.paaro.core.appservice.apirequestmodel.AuditLogRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.AuditResponseModel;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.audit.service.AuditLogOperationsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audit-log")
public class AuditController {

    @Autowired
    private AuditLogOperationsService auditLogOperationsService;

    private Logger logger = LoggerFactory.getLogger(AuditController.class.getName());


    @PostMapping("/fetch-by-or")
    public AuditResponseModel fetchAuditLogByOr(@RequestBody AuditLogRequestModel logRequestModel){

        try {
            return auditLogOperationsService.fetchAuditLogWithOrParameters(logRequestModel);
        } catch (Exception e) {
            logger.error("System error occurred while fetching audit log by or due to ", e);
            return AuditResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching audit log");
        }
    }


    @PostMapping("/fetch-by-and")
    public AuditResponseModel fetchAuditLogByAnd(@RequestBody AuditLogRequestModel logRequestModel){

        try {
            return auditLogOperationsService.fetchAuditLogWithAndParameters(logRequestModel);
        } catch (Exception e) {
            logger.error("System error occurred while fetching audit log by and due to ", e);
            return AuditResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching audit log");
        }
    }

}
