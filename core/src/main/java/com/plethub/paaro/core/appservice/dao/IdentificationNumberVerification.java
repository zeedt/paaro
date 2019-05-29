package com.plethub.paaro.core.appservice.dao;

import com.plethub.paaro.core.models.ManagedUser;

public interface IdentificationNumberVerification {

    default Boolean verifyBvnForNairaAccount(ManagedUser managedUser) {
        return true;
    };

    default Boolean verifyIdentificationNumberForOtherCurrency(ManagedUser managedUser){
        return true;
    }

}
