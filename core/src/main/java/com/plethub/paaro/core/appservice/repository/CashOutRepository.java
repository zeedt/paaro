package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.appservice.enums.CashOutStatus;
import com.plethub.paaro.core.models.CashOutLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CashOutRepository extends JpaRepository<CashOutLog, Long>, JpaSpecificationExecutor {


    CashOutLog findCashOutLogByIdAndCashOutStatus(Long id, CashOutStatus cashOutStatus);

    Page<CashOutLog> findAllByIdNotNull(Pageable pageable);

}
