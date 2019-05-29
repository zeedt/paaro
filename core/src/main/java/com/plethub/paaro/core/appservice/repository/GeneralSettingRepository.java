package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.models.GeneralSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralSettingRepository extends JpaRepository<GeneralSetting,Long> {

    GeneralSetting findByIdNotNull(Long id);

    GeneralSetting findTopByIdIsNotNull();

}
