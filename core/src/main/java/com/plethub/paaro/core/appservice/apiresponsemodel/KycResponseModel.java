package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.KycStatus;

public class KycResponseModel {

    private String message;

    private Long id;

    private String bvnOrIdentificationNo;

    private String utilityBillData;

    private String validIdData;

    private String passportPhotoData;

    private Boolean isBvnOrIdentificationNumberUploaded = false;

    private Boolean isValidIdUploaded = false;

    private Boolean isUtilityBillUploaded = false;

    private Boolean isPassportPhotoUploaded = false;

    private KycStatus bvnKycStatus;

    private KycStatus validIdKycStatus;

    private KycStatus utilityBillKycStatus;

    private KycStatus passportPhotoKycStatus;

    private ApiResponseCode apiResponseCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApiResponseCode getApiResponseCode() {
        return apiResponseCode;
    }

    public void setApiResponseCode(ApiResponseCode apiResponseCode) {
        this.apiResponseCode = apiResponseCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static KycResponseModel fromNarration(ApiResponseCode apiResponseCode, String message) {
        KycResponseModel kycResponseModel = new KycResponseModel();
        kycResponseModel.setMessage(message);
        kycResponseModel.setApiResponseCode(apiResponseCode);
        return kycResponseModel;
    }

    public String getBvnOrIdentificationNo() {
        return bvnOrIdentificationNo;
    }

    public void setBvnOrIdentificationNo(String bvnOrIdentificationNo) {
        this.bvnOrIdentificationNo = bvnOrIdentificationNo;
    }

    public String getUtilityBillData() {
        return utilityBillData;
    }

    public void setUtilityBillData(String utilityBillData) {
        this.utilityBillData = utilityBillData;
    }

    public String getValidIdData() {
        return validIdData;
    }

    public void setValidIdData(String validIdData) {
        this.validIdData = validIdData;
    }

    public Boolean getBvnOrIdentificationNumberUploaded() {
        return isBvnOrIdentificationNumberUploaded;
    }

    public void setBvnOrIdentificationNumberUploaded(Boolean bvnOrIdentificationNumberUploaded) {
        isBvnOrIdentificationNumberUploaded = bvnOrIdentificationNumberUploaded;
    }

    public Boolean getValidIdUploaded() {
        return isValidIdUploaded;
    }

    public void setValidIdUploaded(Boolean validIdUploaded) {
        isValidIdUploaded = validIdUploaded;
    }

    public Boolean getUtilityBillUploaded() {
        return isUtilityBillUploaded;
    }

    public void setUtilityBillUploaded(Boolean utilityBillUploaded) {
        isUtilityBillUploaded = utilityBillUploaded;
    }

    public KycStatus getBvnKycStatus() {
        return bvnKycStatus;
    }

    public void setBvnKycStatus(KycStatus bvnKycStatus) {
        this.bvnKycStatus = bvnKycStatus;
    }

    public KycStatus getValidIdKycStatus() {
        return validIdKycStatus;
    }

    public void setValidIdKycStatus(KycStatus validIdKycStatus) {
        this.validIdKycStatus = validIdKycStatus;
    }

    public KycStatus getUtilityBillKycStatus() {
        return utilityBillKycStatus;
    }

    public void setUtilityBillKycStatus(KycStatus utilityBillKycStatus) {
        this.utilityBillKycStatus = utilityBillKycStatus;
    }

    public String getPassportPhotoData() {
        return passportPhotoData;
    }

    public void setPassportPhotoData(String passportPhotoData) {
        this.passportPhotoData = passportPhotoData;
    }

    public Boolean getPassportPhotoUploaded() {
        return isPassportPhotoUploaded;
    }

    public void setPassportPhotoUploaded(Boolean passportPhotoUploaded) {
        isPassportPhotoUploaded = passportPhotoUploaded;
    }

    public KycStatus getPassportPhotoKycStatus() {
        return passportPhotoKycStatus;
    }

    public void setPassportPhotoKycStatus(KycStatus passportPhotoKycStatus) {
        this.passportPhotoKycStatus = passportPhotoKycStatus;
    }
}
