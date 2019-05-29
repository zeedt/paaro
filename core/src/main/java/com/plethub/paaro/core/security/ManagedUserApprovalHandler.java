package com.plethub.paaro.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;
import org.springframework.stereotype.Service;

@Service
public class ManagedUserApprovalHandler extends DefaultUserApprovalHandler {
    @Override
    public AuthorizationRequest checkForPreApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        authorizationRequest.setApproved(true);
        return authorizationRequest;
    }
}
