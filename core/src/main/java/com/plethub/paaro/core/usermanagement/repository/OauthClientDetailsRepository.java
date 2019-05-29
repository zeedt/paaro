package com.plethub.paaro.core.usermanagement.repository;

import com.plethub.paaro.core.models.OauthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthClientDetailsRepository extends JpaRepository<OauthClientDetails,Long> {

    OauthClientDetails findOneByClientId(String clientId);
}
