package com.plethub.paaro.core.models;


import com.plethub.paaro.core.appservice.enums.Action;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Action action;

    private String description;

    private boolean makerCheckerEnabled;

    @NotNull(message = "updated date cannot be null")
    private Date updatedDate;


    private Long verifiedBy;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean isMakerCheckerEnabled() {
        return makerCheckerEnabled;
    }

    public void setMakerCheckerEnabled(boolean makerCheckerEnabled) {
        this.makerCheckerEnabled = makerCheckerEnabled;
    }

    public Long getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(Long verifiedBy) {
        this.verifiedBy = verifiedBy;
    }
}
