package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.models.TransferRequestMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRequestMapRepository extends JpaRepository<TransferRequestMap, Long> {



}
