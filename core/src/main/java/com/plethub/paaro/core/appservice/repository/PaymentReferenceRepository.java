package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.models.PaymentReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentReferenceRepository extends JpaRepository<PaymentReference, Long>{

    PaymentReference findTopByPaymentReferenceNumber(String paymentReferenceNumber);

    Page<PaymentReference> findTopByWallet_Currency_Type(String paymentReferenceNumber, Pageable pageable);

    Page<PaymentReference> findTopByWallet_Id(Long walletId, Pageable pageable);

    Page<PaymentReference> findAllByWallet_ManagedUser_Id(Long userId, Pageable pageable);

    Page<PaymentReference> findAllByWallet_ManagedUser_IdAndAndUserDepositedIsTrue(Long userId, Pageable pageable);

    Page<PaymentReference> findAllByWallet_ManagedUser_IdAndAndUserDepositedIsFalse(Long userId, Pageable pageable);

    Page<PaymentReference> findAllByWallet_ManagedUser_IdAndAndUserDepositedIsFalseAndUserCanDeposit(Long userId, Pageable pageable);

    PaymentReference findTopByIdIsNotNullOrderByIdDesc();

    PaymentReference findTopByIdAndWallet_ManagedUser_Id(Long paymentId, Long userId);
}
