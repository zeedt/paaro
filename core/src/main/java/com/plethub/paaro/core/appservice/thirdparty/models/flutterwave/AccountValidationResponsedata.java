package com.plethub.paaro.core.appservice.thirdparty.models.flutterwave;

public class AccountValidationResponsedata {

    private String responsecode;

    private String accountnumber;

    private String accountname;

    private String responsemessage;

    private String phonenumber;

    private String uniquereference;

    private String status;

    private String internalreference;

    public String getResponsecode() {
        return responsecode;
    }

    public void setResponsecode(String responsecode) {
        this.responsecode = responsecode;
    }

    public String getAccountnumber() {
        return accountnumber;
    }

    public void setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
    }

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public String getResponsemessage() {
        return responsemessage;
    }

    public void setResponsemessage(String responsemessage) {
        this.responsemessage = responsemessage;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUniquereference() {
        return uniquereference;
    }

    public void setUniquereference(String uniquereference) {
        this.uniquereference = uniquereference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInternalreference() {
        return internalreference;
    }

    public void setInternalreference(String internalreference) {
        this.internalreference = internalreference;
    }
}
