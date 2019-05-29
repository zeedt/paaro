package com.plethub.paaro.core.models;

import com.plethub.paaro.core.appservice.enums.KycStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "wallet_kyc", uniqueConstraints = @UniqueConstraint(columnNames = {"wallet_id"}))
public class Kyc implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String bvn;

    private String utilityBillFileName;

    private String validIdFileName;

    private String passportPhotoFileName;

    private Date bvnUploadedDate;

    private Date utilityBillUploadedDate;

    private Date validIdUploadedDate;

    private Date passportPhotoUploadedDate;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private KycStatus bvnKycStatus = KycStatus.PENDING_UPLOAD;


    @Enumerated(value = EnumType.STRING)
    @NotNull
    private KycStatus utilityBillKycStatus = KycStatus.PENDING_UPLOAD;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private KycStatus validIdKycStatus = KycStatus.PENDING_UPLOAD;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private KycStatus passportPhotoKycStatus = KycStatus.PENDING_UPLOAD;

    @OneToOne
    private Wallet wallet;

    private Date verifiedOrDeclinedDate;

    @OneToOne
    private ManagedUser verifiedOrDeclinedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getUtilityBillFileName() {
        return utilityBillFileName;
    }

    public void setUtilityBillFileName(String utilityBillFileName) {
        this.utilityBillFileName = utilityBillFileName;
    }

    public Date getBvnUploadedDate() {
        return bvnUploadedDate;
    }

    public void setBvnUploadedDate(Date bvnUploadedDate) {
        this.bvnUploadedDate = bvnUploadedDate;
    }

    public Date getUtilityBillUploadedDate() {
        return utilityBillUploadedDate;
    }

    public void setUtilityBillUploadedDate(Date utilityBillUploadedDate) {
        this.utilityBillUploadedDate = utilityBillUploadedDate;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getValidIdFileName() {
        return validIdFileName;
    }

    public void setValidIdFileName(String validIdFileName) {
        this.validIdFileName = validIdFileName;
    }

    public Date getValidIdUploadedDate() {
        return validIdUploadedDate;
    }

    public void setValidIdUploadedDate(Date validIdUploadedDate) {
        this.validIdUploadedDate = validIdUploadedDate;
    }

    public KycStatus getBvnKycStatus() {
        return bvnKycStatus;
    }

    public void setBvnKycStatus(KycStatus bvnKycStatus) {
        this.bvnKycStatus = bvnKycStatus;
    }

    public KycStatus getUtilityBillKycStatus() {
        return utilityBillKycStatus;
    }

    public void setUtilityBillKycStatus(KycStatus utilityBillKycStatus) {
        this.utilityBillKycStatus = utilityBillKycStatus;
    }

    public KycStatus getValidIdKycStatus() {
        return validIdKycStatus;
    }

    public void setValidIdKycStatus(KycStatus validIdKycStatus) {
        this.validIdKycStatus = validIdKycStatus;
    }

    public String getPassportPhotoFileName() {
        return passportPhotoFileName;
    }

    public void setPassportPhotoFileName(String passportPhotoFileName) {
        this.passportPhotoFileName = passportPhotoFileName;
    }

    public Date getPassportPhotoUploadedDate() {
        return passportPhotoUploadedDate;
    }

    public void setPassportPhotoUploadedDate(Date passportPhotoUploadedDate) {
        this.passportPhotoUploadedDate = passportPhotoUploadedDate;
    }

    public KycStatus getPassportPhotoKycStatus() {
        return passportPhotoKycStatus;
    }

    public void setPassportPhotoKycStatus(KycStatus passportPhotoKycStatus) {
        this.passportPhotoKycStatus = passportPhotoKycStatus;
    }

    public Date getVerifiedOrDeclinedDate() {
        return verifiedOrDeclinedDate;
    }

    public void setVerifiedOrDeclinedDate(Date verifiedOrDeclinedDate) {
        this.verifiedOrDeclinedDate = verifiedOrDeclinedDate;
    }

    public ManagedUser getVerifiedOrDeclinedBy() {
        return verifiedOrDeclinedBy;
    }

    public void setVerifiedOrDeclinedBy(ManagedUser verifiedOrDeclinedBy) {
        this.verifiedOrDeclinedBy = verifiedOrDeclinedBy;
    }
}
