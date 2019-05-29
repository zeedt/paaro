package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.models.PaaroBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaaroBankAccountRepository extends JpaRepository<PaaroBankAccount, Long> {

    PaaroBankAccount findTopByActiveIsTrue();

    List<PaaroBankAccount> findAllByActiveIsTrue();

    List<PaaroBankAccount> findAllByCurrency_TypeAndActiveIsTrue(String currencyType);
    List<PaaroBankAccount> findAllByCurrency_Type(String currencyType);

    PaaroBankAccount findTopByAccountNameAndAccountNumber(String accountName, String accountNumber);

    PaaroBankAccount findTopByAccountNumber(String accountNumber);

}
