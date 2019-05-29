package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.appservice.enums.Action;
import com.plethub.paaro.core.models.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    Configuration findTopByAction(Action action);

    Page<Configuration> findAllByIdNotNull(Pageable pageable);


}
