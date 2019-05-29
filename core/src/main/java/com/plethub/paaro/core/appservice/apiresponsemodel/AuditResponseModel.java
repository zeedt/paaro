package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.AuditLog;
import org.springframework.data.domain.Page;

import java.util.List;

public class AuditResponseModel {

    private AuditLog auditLog;

    private List<AuditLog> auditLogList;

    private Page<AuditLog> auditLogPage;

    private String narration;

    private ApiResponseCode apiResponseCode;

    public static AuditResponseModel fromNarration (ApiResponseCode apiResponseCode, String narration) {

        AuditResponseModel auditResponseModel = new AuditResponseModel();
        auditResponseModel.setNarration(narration);
        auditResponseModel.setApiResponseCode(apiResponseCode);

        return auditResponseModel;

    }


    public AuditLog getAuditLog() {
        return auditLog;
    }

    public void setAuditLog(AuditLog auditLog) {
        this.auditLog = auditLog;
    }

    public List<AuditLog> getAuditLogList() {
        return auditLogList;
    }

    public void setAuditLogList(List<AuditLog> auditLogList) {
        this.auditLogList = auditLogList;
    }

    public Page<AuditLog> getAuditLogPage() {
        return auditLogPage;
    }

    public void setAuditLogPage(Page<AuditLog> auditLogPage) {
        this.auditLogPage = auditLogPage;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public ApiResponseCode getApiResponseCode() {
        return apiResponseCode;
    }

    public void setApiResponseCode(ApiResponseCode apiResponseCode) {
        this.apiResponseCode = apiResponseCode;
    }
}
