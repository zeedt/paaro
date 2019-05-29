package com.plethub.paaro.webapp.ApiModel;

import lombok.Data;

@Data
public class ManagedUser {

    private String id;

    private String lastName;

    private String userCategory;

    private String phoneNumber;

    private String email;

    private String active;

    private String dateCreated;

    private String displayName;

    private String base64Image;

    private String firstName;

    private String verifierComment;

    private String iniatorComment;

}
