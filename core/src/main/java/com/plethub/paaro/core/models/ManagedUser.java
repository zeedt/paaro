package com.plethub.paaro.core.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.plethub.paaro.core.usermanagement.enums.UserCategory;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user")
public class ManagedUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Column(name = "last_name")
    private String lastName;

    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be blank")
    @Column(name = "password")
    @JsonIgnore
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "user_category")
    private UserCategory userCategory;

    @Column(name = "date_created")
    private Date dateCreated;


    @NotNull
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column
    @NotNull
    private boolean active = false;

    @Lob
    private String base64Image;

    @Column(unique = true, nullable = false)
    private String displayName;

    @NotNull
    @Column(name = "email")
    private String email;

    private boolean accountActivatedViaLink = false;

    private String activationCode;


    @Transient
    private String iniatorComment;

    @Transient
    private String verifierComment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserCategory getUserCategory() {
        return userCategory;
    }

    public void setUserCategory(UserCategory userCategory) {
        this.userCategory = userCategory;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIniatorComment() {
        return iniatorComment;
    }

    public void setIniatorComment(String iniatorComment) {
        this.iniatorComment = iniatorComment;
    }

    public String getVerifierComment() {
        return verifierComment;
    }

    public void setVerifierComment(String verifierComment) {
        this.verifierComment = verifierComment;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public boolean isAccountActivatedViaLink() {
        return accountActivatedViaLink;
    }

    public void setAccountActivatedViaLink(boolean accountActivatedViaLink) {
        this.accountActivatedViaLink = accountActivatedViaLink;
    }
}
