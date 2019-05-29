package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.models.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency,Long> {

    Currency findCurrencyByType(String type);

    List<Currency> findByDisabled(boolean disabled);


}
