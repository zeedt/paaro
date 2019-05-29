package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.models.BeneficiaryAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficiaryAccountRepository extends JpaRepository<BeneficiaryAccount, Long> {

    BeneficiaryAccount findOneById(Long id);

    BeneficiaryAccount findAllByBank_IdAndUserId(Long bankid, Long userid);

    BeneficiaryAccount findAllByBank_IdAndUserIdAndAccountNumber(Long bankid, Long userid, String accountNumber);

    List<BeneficiaryAccount> findAllByUserId(Long userid);

    BeneficiaryAccount findTopByIdAndUserId(Long id, Long userId);

    List<BeneficiaryAccount> findAllByUserIdAndCurrency(Long userId, String currencyType);


}
