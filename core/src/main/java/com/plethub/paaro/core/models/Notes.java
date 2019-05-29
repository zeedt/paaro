package com.plethub.paaro.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class Notes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties(value = {"password","base64Image","hibernateLazyInitializer", "handler"})
    private ManagedUser customer;

    private String comment;

    private Date madeOn;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties(value = {"password","base64Image","hibernateLazyInitializer", "handler"})
    private ManagedUser admin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
