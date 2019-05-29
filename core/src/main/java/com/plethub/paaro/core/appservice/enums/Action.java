package com.plethub.paaro.core.appservice.enums;

public enum Action {

    VERIFY_AUTHORITY("Authority verification"),
    ADD_CURRENCY("Currency addition initiation"),
    VERIFY_CURRENCY("Currency verification"),
    ADD_CURRENCY_DETAILS("Currency addition initiation"),
    UPDATE_CURRENCY_DETAILS("Currency details update initiation"),
    MAP_AUTHORITY_TO_USER("Authority addition to user initiation"),
    MAP_AUTHORITIES_TO_USER("Authorities addition to user initiation"),
    CREATE_USER("User creation initiation"),
    UPDATE_USER("User update initiation"),
    VERIFY_USER_CREATION("User creation verification"),
    INITIATE_REQUEST_FOR_USER(""),
    VERIFY_REQUEST_INITIATED_FOR_USER(""),
    DEACTIVATE_USER("User deactivation initiation"),
    ACTIVATE_USER("User activation initiation"),
    TOGGLE_MAKER_CHECKER("Maker checker update toggle for action initiation"),
    DISABLE_CURRENCY("Disable currency initiation"),
    ENABLE_CURRENCY("Enable currency initiation"),
    ADD_BANK("Bank creation initiation"),
    UPDATE_BANK("Bank modification initiation"),
    CREATE_PAARO_BANK_ACCOUNT("Paaro bank account addition initiation"),
    UPDATE_PAARO_BANK_ACCOUNT("Paaro bank account update initiation"),
    SETTLE_CUSTOMER("Settle customer"),
    VERIFY_DEPOSIT("Verify bank deposits");

    private String actionDescription = "";

    Action(String action) {
        this.actionDescription = action;
    }

    public String getDecsription() {
        return this.actionDescription;
    }

}
