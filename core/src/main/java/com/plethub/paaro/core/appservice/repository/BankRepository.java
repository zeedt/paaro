package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.appservice.enums.BankType;
import com.plethub.paaro.core.models.Bank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {

    List<Bank> findAllByBankType(BankType bankType);

    Page<Bank> findAllByBankType(BankType bankType, Pageable pageable);

    Page<Bank> findAllByIdNotNull(Pageable pageable);

    Bank findTopByBankCodeAndCountryCode(String bankCode, String countryCode);

    Bank findTopByBankCodeOrBankName(String bankCode, String bankName);

    Bank findTopByBankCode(String bankCode);

    Bank findTopByBankName(String bankName);

}
