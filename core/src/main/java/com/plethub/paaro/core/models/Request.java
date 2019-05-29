package com.plethub.paaro.core.models;


import com.plethub.paaro.core.appservice.enums.Action;
import com.plethub.paaro.core.appservice.enums.Status;
import com.plethub.paaro.core.models.ManagedUser;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Action action;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Date initiatedDate;

    private Date verifiedOrDeclinedDate;

    @OneToOne
    private ManagedUser initiatorId;

    @OneToOne
    private ManagedUser verifierId;

    @Lob
    private String oldData;

    @Lob
    private String newData;

    private Long entityId;

    private String iniatorComment;

    private String declineComment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getInitiatedDate() {
        return initiatedDate;
    }

    public void setInitiatedDate(Date initiatedDate) {
        this.initiatedDate = initiatedDate;
    }

    public Date getVerifiedOrDeclinedDate() {
        return verifiedOrDeclinedDate;
    }

    public void setVerifiedOrDeclinedDate(Date verifiedOrDeclinedDate) {
        this.verifiedOrDeclinedDate = verifiedOrDeclinedDate;
    }

    public ManagedUser getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(ManagedUser initiatorId) {
        this.initiatorId = initiatorId;
    }

    public ManagedUser getVerifierId() {
        return verifierId;
    }

    public void setVerifierId(ManagedUser verifierId) {
        this.verifierId = verifierId;
    }

    public String getOldData() {
        return oldData;
    }

    public void setOldData(String oldData) {
        this.oldData = oldData;
    }

    public String getNewData() {
        return newData;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }

    public String getIniatorComment() {
        return iniatorComment;
    }

    public void setIniatorComment(String iniatorComment) {
        this.iniatorComment = iniatorComment;
    }

    public String getDeclineComment() {
        return declineComment;
    }

    public void setDeclineComment(String declineComment) {
        this.declineComment = declineComment;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
