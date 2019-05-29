package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.models.Kyc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KycRepository extends JpaRepository<Kyc, Long> {

    Kyc findByWallet_Id(Long id);

}
