package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.appservice.enums.Action;
import com.plethub.paaro.core.models.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface RequestRepository  extends JpaRepository<Request, Long>, JpaSpecificationExecutor {


    Request findTopByActionAndEntityId(Action action, Long entityId);

    Page<Request> findAllByAction(Action action, Pageable pageable);


}
