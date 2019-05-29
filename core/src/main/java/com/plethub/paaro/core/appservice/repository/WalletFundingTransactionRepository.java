package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.models.WalletFundingTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletFundingTransactionRepository extends JpaRepository<WalletFundingTransaction, Long>, JpaSpecificationExecutor {

    List<WalletFundingTransaction> findAllByManagedUser_EmailAndCurrency_Type(String email, String currencyType);

    Page<WalletFundingTransaction> findAllByManagedUser_EmailAndCurrency_Type(String email, String currencyType, Pageable pageable);

    Page<WalletFundingTransaction> findAllByWallet_ManagedUser_Email(String email, Pageable pageable);
    Page<WalletFundingTransaction> findAllByWallet_Id(Long id, Pageable pageable);

    Page<WalletFundingTransaction> findAllByIdIsNotNull(Pageable pageable);
    Page<WalletFundingTransaction> findAllById(Long id, Pageable pageable);

    @Query("select w from WalletFundingTransaction w where w.wallet.managedUser.firstName like :firstName OR  w.wallet.managedUser.lastName like :firstName ")
    Page<WalletFundingTransaction> filterByWalletUserFirstOrLastName(@Param("firstName") String firstName, Pageable pageable);

    WalletFundingTransaction findTopByPaymentReference_Id(Long id);
    WalletFundingTransaction findTopByBankDeposit_Id(Long id);


}
