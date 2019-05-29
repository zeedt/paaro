package com.plethub.paaro.core.security;

import com.plethub.paaro.core.usermanagement.repository.OauthClientDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OauthClientDetailsService implements ClientDetailsService, ClientRegistrationService {

    @Autowired
    public OauthClientDetailsRepository oauthClientDetailsRepository;

    @Override
    public ClientDetails loadClientByClientId(String clientId)  {
        return oauthClientDetailsRepository.findOneByClientId(clientId);
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {

    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {

    }

    @Override
    public void updateClientSecret(String s, String s1) throws NoSuchClientException {

    }

    @Override
    public void removeClientDetails(String s) throws NoSuchClientException {

    }

    @Override
    public List<ClientDetails> listClientDetails() {
        return null;
    }
}
