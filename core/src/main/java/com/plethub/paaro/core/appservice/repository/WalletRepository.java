package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.appservice.apiresponsemodel.WalletBalanceDashboardDetails;
import com.plethub.paaro.core.models.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findAllByManagedUser_Id(Long id);

    List<Wallet> findAllByCurrency_Type(String type);

    Page<Wallet> findAllByCurrency_Type(String type, Pageable pageable);

    @Query("select w from Wallet w where w.currency.type=:type AND (w.managedUser.firstName like :filter OR w.managedUser.lastName like :filter)")
    Page<Wallet> findWalletsByCurrencyTypeAndFilter(String type, String filter, Pageable pageable);

    List<Wallet> findAllByCurrency_TypeAndManagedUser_Email(String type, String email);

    List<Wallet> findAllByManagedUser_Email(String email);

    Wallet findByManagedUser_EmailAndCurrency_Type(String email, String currencyType);

    Wallet findByManagedUser_IdAndCurrency_Type(Long userId, String currencyType);

    @Query("select new com.plethub.paaro.core.appservice.apiresponsemodel.WalletBalanceDashboardDetails(sum(w.availableAccountBalance), sum(w.ledgerAccountBalance)) from Wallet w where w.currency.type=:type")
    List<WalletBalanceDashboardDetails> getWalletBalanceDetailsByCurrency(@Param("type") String type);

}
