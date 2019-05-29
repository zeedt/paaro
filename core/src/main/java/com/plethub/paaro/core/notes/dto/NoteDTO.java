package com.plethub.paaro.core.notes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.plethub.paaro.core.models.ManagedUser;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class NoteDTO {

    private Long id;

    private ManagedUser customer;

    private Long customerId;

    private String comment;

    private Date madeOn;

    private ManagedUser admin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public ManagedUser getCustomer() {
        return customer;
    }

    public void setCustomer(ManagedUser customer) {
        this.customer = customer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getMadeOn() {
        return madeOn;
    }

    public void setMadeOn(Date madeOn) {
        this.madeOn = madeOn;
    }

    public ManagedUser getAdmin() {
        return admin;
    }

    public void setAdmin(ManagedUser admin) {
        this.admin = admin;
    }
}
