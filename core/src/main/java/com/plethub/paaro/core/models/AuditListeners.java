package com.plethub.paaro.core.models;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class AuditListeners {

    @PrePersist
    @PreUpdate
    @PreRemove
    private void beforeAnyOperation(Object object) {

    }

}
