package com.plethub.paaro.core.configuration.apimodel;

import com.plethub.paaro.core.appservice.enums.Action;

public class ActionWrapper {

    private Action action;

    private String actionDescription;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }
}
