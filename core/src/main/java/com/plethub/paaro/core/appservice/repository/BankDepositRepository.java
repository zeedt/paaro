package com.plethub.paaro.core.appservice.repository;


import com.plethub.paaro.core.appservice.enums.DepositStatus;
import com.plethub.paaro.core.models.BankDeposit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankDepositRepository extends JpaRepository<BankDeposit, Long>, JpaSpecificationExecutor {


    List<BankDeposit> findAllByPaymentReference_Wallet_Id(Long id);

    Page<BankDeposit> findAllByPaymentReference_Wallet_ManagedUser_Id(Long id, Pageable pageable);

    Page<BankDeposit> findAllByPaymentReference_Wallet_ManagedUser_IdAndTransactionStatus(Long id, DepositStatus depositStatus, Pageable pageable);

    BankDeposit findTopByPaymentReference_Id(Long id);

}
