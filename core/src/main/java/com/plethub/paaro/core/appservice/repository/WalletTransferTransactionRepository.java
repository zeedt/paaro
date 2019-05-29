package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.appservice.enums.TransactionStatus;
import com.plethub.paaro.core.appservice.enums.WalletStatus;
import com.plethub.paaro.core.models.TransferTransactionType;
import com.plethub.paaro.core.models.WalletTransferTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface WalletTransferTransactionRepository extends JpaRepository<WalletTransferTransaction, Long>, JpaSpecificationExecutor {

    List<WalletTransferTransaction> findAllByManagedUser_EmailAndFromCurrency_Type(String email, String currency);

    Page<WalletTransferTransaction> findAllByManagedUser_EmailAndFromCurrency_Type(String email, String currency, Pageable pageable);

    List<WalletTransferTransaction> findAllByManagedUser_Email(String email);

    Page<WalletTransferTransaction> findAllByManagedUser_Email(String email, Pageable pageable);

    Page<WalletTransferTransaction> findAllByManagedUser_EmailAndToAccountNameLike(String email, String accountName, Pageable pageable);

    List<WalletTransferTransaction> findAllByManagedUser_EmailAndTransactionStatus(String email, TransactionStatus transactionStatus);

    Page<WalletTransferTransaction> findAllByManagedUser_EmailAndTransactionStatus(String email, TransactionStatus transactionStatus, Pageable pageable);

    List<WalletTransferTransaction> findAllByFromCurrency_Type(String currency);

    List<WalletTransferTransaction> findAllByFromCurrency_TypeNotIn(String currency);

    List<WalletTransferTransaction> findAllByFromCurrency_TypeAndTransferRequestMapIsNull(String currency);

    List<WalletTransferTransaction> findAllByFromCurrency_TypeNotInAndTransferRequestMapIsNull(String currency);

    Page<WalletTransferTransaction> findAllByIdIsNotNull(Pageable pageable);

    Page<WalletTransferTransaction> findAllByIdIsNotNullAndToAccountNumberIsLike(String accountName, Pageable pageable);

    Page<WalletTransferTransaction> findAllByIdIsNotNullAndToAccountNameIsLike(String accountName, Pageable pageable);

    List<WalletTransferTransaction> findAllByIdIsNotNullAndToAccountNameIsLike(String accountName);

    Page<WalletTransferTransaction> findAllByIdIsNotNullAndToAccountNameIsLikeAndInitiatedDateIsBetween(String accountName, Date fromDate, Date toDate, Pageable pageable);

    List<WalletTransferTransaction> findAllByIdIsNotNullAndToAccountNameIsLikeAndInitiatedDateIsBetween(String accountName, Date fromDate, Date toDate);

    @Query("select w from WalletTransferTransaction w where w.transactionStatus = :transactionStatus AND (w.wallet.managedUser.firstName like :firstName OR  w.wallet.managedUser.lastName like :firstName) ")
    Page<WalletTransferTransaction> filterByWalletUserFirstOrLastNameAndPendingRequests(@Param("firstName") String firstName, @Param("transactionStatus") TransactionStatus transactionStatus, Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusIn(List<TransactionStatus> transactionStatusList, Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusNotIn(List<TransactionStatus> transactionStatusList, Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusNotInAndManagedUser_Id(List<TransactionStatus> transactionStatusList, Long id, Pageable pageable);

    Long countAllByTransactionStatusNotInAndManagedUser_Id(List<TransactionStatus> transactionStatusList, Long id);

    Long countAllByTransactionStatusInAndManagedUser_Id(List<TransactionStatus> transactionStatusList, Long id);

    Page<WalletTransferTransaction> findAllByTransactionStatusInAndManagedUser_Id(List<TransactionStatus> transactionStatusList, Long id, Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusNotInAndTransferTransactionTypeIs(List<TransactionStatus> transactionStatusList, TransferTransactionType transferTransactionType, Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusInAndTransferTransactionTypeIs(List<TransactionStatus> transactionStatusList, TransferTransactionType transferTransactionType, Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusInAndActualAmountBetweenAndFromCurrency_Type(List<TransactionStatus> transactionStatusList, BigDecimal fromAmount, BigDecimal toAmount, String currencyCode, Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusInAndActualAmountGreaterThanEqualAndActualAmountLessThanEqualAndFromCurrency_Type(List<TransactionStatus> transactionStatusList, BigDecimal fromAmount, BigDecimal toAmount, String currencyCode, Pageable pageable);

    Page<WalletTransferTransaction> findAllByFromCurrency_TypeAndTransactionStatusIn(String currencyCode, List<TransactionStatus> transactionStatuses, Pageable pageable);

    Page<WalletTransferTransaction> findAllByManagedUser_Id(Long id, Pageable pageable);

    WalletTransferTransaction findTopByIdAndTransactionStatusIn(Long id, List<TransactionStatus> transactionStatusList);

    WalletTransferTransaction findOneByIdAndWalletStatusAndFromCurrency_Type(Long id, WalletStatus walletStatus, String currencyType);

    WalletTransferTransaction findOneByIdAndWalletStatusAndFromCurrency_TypeAndManagedUser_Id(Long id, WalletStatus walletStatus, String currencyType, Long userid);

    Page<WalletTransferTransaction> findAllByWalletStatusAndManagedUser_Id(WalletStatus walletStatus, Long id, Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusInAndActualAmountAndFromCurrency_TypeAndExchangeRateBetween(List<TransactionStatus> transactionStatusList, BigDecimal actualAmount, String currencyCode, Double exchangeRatefrom, Double exchangeRateTo, Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusInAndActualAmountAndToCurrency_TypeAndExchangeRateBetween(List<TransactionStatus> transactionStatusList, BigDecimal actualAmount, String currencyCode, Double exchangeRatefrom, Double exchangeRateTo, Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusInAndActualAmountAndToCurrency_TypeAndFromCurrency_TypeAndExchangeRateBetween(List<TransactionStatus> transactionStatusList, BigDecimal actualAmount, String toCurrencyCode, String fromCurrencyCode, Double exchangeRatefrom, Double exchangeRateTo, Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusInAndActualAmountBetweenAndToCurrency_TypeAndFromCurrency_TypeAndExchangeRateBetween(List<TransactionStatus> transactionStatusList, BigDecimal fromActualAmount, BigDecimal toActualAmount, String toCurrencyCode, String fromCurrencyCode, Double exchangeRatefrom, Double exchangeRateTo, Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusInAndTotalAmountBetweenAndToCurrency_TypeAndFromCurrency_TypeAndExchangeRateBetween(List<TransactionStatus> transactionStatusList, BigDecimal fromActualAmount, BigDecimal toActualAmount, String toCurrencyCode, String fromCurrencyCode, Double exchangeRatefrom, Double exchangeRateTo, Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusInAndTotalAmountAndToCurrency_TypeAndFromCurrency_TypeAndExchangeRateBetween(List<TransactionStatus> transactionStatusList, BigDecimal Amount, String toCurrencyCode, String fromCurrencyCode, Double exchangeRatefrom, Double exchangeRateTo, Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusInAndActualAmountAndToCurrency_TypeAndFromCurrency_TypeAndExchangeRate(List<TransactionStatus> transactionStatusList, BigDecimal actualAmount, String toCurrencyCode, String fromCurrencyCode, Double exchangeRate , Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusInAndTotalAmountAndToCurrency_TypeAndFromCurrency_TypeAndExchangeRate(List<TransactionStatus> transactionStatusList, BigDecimal actualAmount, String toCurrencyCode, String fromCurrencyCode, Double exchangeRate , Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransactionStatusAndInitiatedDateIsLessThanEqual(TransactionStatus transactionStatus, Date date, Pageable pageable);

    @Query("select count(1) from WalletTransferTransaction m where m.initiatedDate between :startDate and :endDate")
    Long getDashboardDetailsWithdateRange(@Param("startDate")Date startDate, @Param("endDate")Date endDate);

    @Query("select sum(m.chargeAmount) from WalletTransferTransaction m where m.initiatedDate between :startDate and :endDate and m.fromCurrency.type = :type")
    BigDecimal getDashboardDetailsWithDateRangeForRevenue(@Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("type") String type);

    @Query("select avg (m.exchangeRate) from WalletTransferTransaction m where m.fromCurrency.type = :type or m.toCurrency.type = :type")
    BigDecimal getAverageExchangeRateByCurrency(@Param("type") String currencyType);

    @Query("select avg (m.exchangeRate) from WalletTransferTransaction m where m.initiatedDate between :startDate and :endDate and (m.fromCurrency.type = :type or m.toCurrency.type = :type)")
    BigDecimal getDailyAverageExchangeRateByCurrency(@Param("type") String currencyType, @Param("startDate")Date startDate, @Param("endDate")Date endDate );

    Page<WalletTransferTransaction> findAllByTransferRequestMapNotNull(Pageable pageable);

    Page<WalletTransferTransaction> findAllByTransferRequestMapNotNullAndManagedUser_Id(Long userId, Pageable pageable);


    @Query("select count(1) from WalletTransferTransaction m where m.initiatedDate between :startDate and :endDate and m.transferRequestMap is not null ")
    Long getMatchedTransactionDashboardDetailsWithdateRange(@Param("startDate")Date startDate, @Param("endDate")Date endDate);


    @Query("select sum(m.chargeAmount) from WalletTransferTransaction m where m.initiatedDate between :startDate and :endDate and m.fromCurrency.type = :type and m.transferRequestMap is not null")
    BigDecimal getMatchedDashboardDetailsWithDateRangeForRevenue(@Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("type") String type);

    @Query("select sum(m.totalAmount) from WalletTransferTransaction m where m.initiatedDate between :startDate and :endDate and m.fromCurrency.type = :type and m.transactionStatus in :statuses and m.managedUser.id = :userId")
    BigDecimal getSumOfUsersTransactionsPendingOrCompleted(@Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("type") String type, @Param("statuses") List<TransactionStatus> transactionStatuses, @Param("userId") Long userId);

    @Query("select m from WalletTransferTransaction m where m.initiatedDate between :startDate and :endDate and (select count(b) from WalletTransferTransaction b where b.initiatedDate between :startDate and :endDate and b.transactionStatus in  :statuses and b.managedUser.id = m.managedUser.id) >= 1 ")
    List<WalletTransferTransaction> getBidDetailsWithDateRangeForExistingUser(@Param("startDate")Date startDate, @Param("endDate")Date endDate,  @Param("statuses") List<TransactionStatus> transactionStatuses);

    @Query("select m from WalletTransferTransaction m where m.initiatedDate between :startDate and :endDate  and (select count(b) from WalletTransferTransaction b where b.initiatedDate between :startDate and :endDate and b.transactionStatus in  :statuses and b.managedUser.id = m.managedUser.id) < 1 ")
    List<WalletTransferTransaction> getBidDetailsWithDateRangeForNewUser(@Param("startDate")Date startDate, @Param("endDate")Date endDate,  @Param("statuses") List<TransactionStatus> transactionStatuses);

//    @Query("select m from WalletTransferTransaction m where m.initiatedDate between :startDate and :endDate and m.transactionStatus not in  :statuses")
//    List<WalletTransferTransaction> getBidDetailsWithDateRangeForNewUser(@Param("startDate")Date startDate, @Param("endDate")Date endDate,  @Param("statuses") List<TransactionStatus> transactionStatuses);

}
