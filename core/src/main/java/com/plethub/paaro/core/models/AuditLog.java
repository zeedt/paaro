package com.plethub.paaro.core.models;

import com.plethub.paaro.core.usermanagement.enums.Module;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Entity
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String action;

    private String performedBy;

    private String actionDescription;

    @Lob
    private String initialData;

    @Lob
    private String finalData;

    private String ipAddress;

    private Date datePerformed;

    @Enumerated(EnumType.STRING)
    private Module module;

    private Long entityId;


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public String getInitialData() {
        return initialData;
    }

    public void setInitialData(String initialData) {
        this.initialData = initialData;
    }

    public String getFinalData() {
        return finalData;
    }

    public void setFinalData(String finalData) {
        this.finalData = finalData;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getDatePerformed() {
        return datePerformed;
    }

    public void setDatePerformed(Date datePerformed) {
        this.datePerformed = datePerformed;
    }

    public static String getIpAddressOfMachine() {
        RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
        if (attribs instanceof NativeWebRequest) {
            HttpServletRequest request = (HttpServletRequest)((NativeWebRequest)attribs).getNativeRequest();
            return request.getRemoteAddr();
        } else {
            return "-";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
