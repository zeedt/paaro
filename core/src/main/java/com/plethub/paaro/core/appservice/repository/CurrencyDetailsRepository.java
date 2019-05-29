package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.models.CurrencyDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyDetailsRepository extends JpaRepository<CurrencyDetails, Long> {

    CurrencyDetails findByCurrencyCode(String currencyCode);
}
