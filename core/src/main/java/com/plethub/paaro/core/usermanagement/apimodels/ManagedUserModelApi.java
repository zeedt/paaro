package com.plethub.paaro.core.usermanagement.apimodels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.plethub.paaro.core.usermanagement.enums.ResponseStatus;
import com.plethub.paaro.core.models.Authority;
import com.plethub.paaro.core.models.ManagedUser;
import com.plethub.paaro.core.usermanagement.enums.UserCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public class ManagedUserModelApi {

    @JsonIgnoreProperties(value = {"password"})
    private ManagedUser managedUser;

    private List<Authority> authorityList;

    private ResponseStatus responseStatus;

    private Authority authorityModel;

    private String message;

    private String email;

    private List<String> authorities;

    private String authority;

    private List<ManagedUser> managedUsers;

    private String firstName;

    private String lastName;

    private String password;

    private UserCategory userCategory;

    private Date dateCreated;

    private String phoneNumber;

    private boolean active = false;

    private String base64Image;

    private String displayName;

    private String iniatorComment;

    private String verifierComment;

    private Long id;

    @JsonIgnoreProperties(value = {"password"})
    private Page<ManagedUser> managedUserPage;

    public ManagedUserModelApi (ManagedUser managedUser, List<Authority> authorityList, ResponseStatus responseStatus) {
        this.managedUser = managedUser;
        this.authorityList = authorityList;
        this.responseStatus = responseStatus;
    }
    public ManagedUserModelApi (ManagedUser managedUser, Authority authorityModel, List<Authority> authorityList, ResponseStatus responseStatus) {
        this.managedUser = managedUser;
        this.authorityList = authorityList;
        this.authorityModel = authorityModel;
        this.responseStatus = responseStatus;
    }
    public ManagedUserModelApi (ManagedUser managedUser, Authority authorityModel, ResponseStatus responseStatus) {
        this.managedUser = managedUser;
        this.authorityModel = authorityModel;
        this.responseStatus = responseStatus;
    }
    public ManagedUserModelApi (ManagedUser managedUser, List<Authority> authorityList, ResponseStatus responseStatus, String message) {
        this.managedUser = managedUser;
        this.authorityList = authorityList;
        this.responseStatus = responseStatus;
        this.message = message;
    }

    public ManagedUserModelApi() {
    }

    public ManagedUserModelApi(ResponseStatus responseStatus, String message) {
        this.responseStatus = responseStatus;
        this.message = message;
    }

    public ManagedUser getManagedUser() {
        return managedUser;
    }

    public void setManagedUser(ManagedUser managedUser) {
        this.managedUser = managedUser;
    }

    public List<Authority> getAuthorityList() {
        return authorityList;
    }

    public void setAuthorityList(List<Authority> authorityList) {
        this.authorityList = authorityList;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Authority getAuthorityModel() {
        return authorityModel;
    }

    public void setAuthorityModel(Authority authorityModel) {
        this.authorityModel = authorityModel;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public List<ManagedUser> getManagedUsers() {
        return managedUsers;
    }

    public void setManagedUsers(List<ManagedUser> managedUsers) {
        this.managedUsers = managedUsers;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Page<ManagedUser> getManagedUserPage() {
        return managedUserPage;
    }

    public void setManagedUserPage(Page<ManagedUser> managedUserPage) {
        this.managedUserPage = managedUserPage;
    }

    @Override
    public String toString() {
        return "ManagedUserModelApi{" +
                "managedUser=" + managedUser +
                ", authorityList=" + authorityList +
                ", responseStatus=" + responseStatus +
                ", authorityModel=" + authorityModel +
                ", message='" + message + '\'' +
                ", email='" + email + '\'' +
                ", authorities=" + authorities +
                ", authority='" + authority + '\'' +
                ", managedUsers=" + managedUsers +
                ", managedUserPage=" + managedUserPage +
                '}';
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
