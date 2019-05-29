package com.plethub.paaro.api.background;

import com.plethub.paaro.core.appservice.enums.TransactionStatus;
import com.plethub.paaro.core.appservice.repository.WalletFundingTransactionRepository;
import com.plethub.paaro.core.appservice.repository.WalletRepository;
import com.plethub.paaro.core.appservice.repository.WalletTransferTransactionRepository;
import com.plethub.paaro.core.models.Wallet;
import com.plethub.paaro.core.models.WalletFundingTransaction;
import com.plethub.paaro.core.models.WalletTransferTransaction;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;

@Configuration
@Transactional(rollbackFor = Exception.class)
public class TransactionMonitoringJob {

    @Value("${paaro.bid.expire-duration:72}")
    private Integer expiryHours;

    Logger logger = LoggerFactory.getLogger(TransactionMonitoringJob.class.getName());

    @Autowired
    WalletTransferTransactionRepository walletTransferTransactionRepository;

    @Autowired
    private WalletFundingTransactionRepository walletFundingTransactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    private boolean isRunning = false;

    @Scheduled(fixedDelay = (1000*60*1))
    public void scheduleFixedDelayTask() {

        if (!isRunning) {
            expireTransferRequests(expiryHours);
        }

    }

    public void expireTransferRequests(Integer expiryHours) {

        try {
            isRunning = true;
            Date currentDate = new Date();
            currentDate = DateUtils.addHours(currentDate, (-1*expiryHours));

            Page<WalletTransferTransaction> pendingTransactions;
            Integer pageNo = 0;
            Integer pageSize = 2;

            while (!(pendingTransactions = walletTransferTransactionRepository
                    .findAllByTransactionStatusAndInitiatedDateIsLessThanEqual(TransactionStatus.CUSTOMER_LOGGED_REQUEST, currentDate, new PageRequest(pageNo++, pageSize))).getContent().isEmpty()) {

                for (WalletTransferTransaction transferTransaction : pendingTransactions.getContent()) {
                    if (transferTransaction == null)
                        continue;
                    try {
                        revertTransactionAndFundWallet(transferTransaction);
                    } catch (Exception e) {
                        logger.error("Error occurred while reverting transaction with Id " + transferTransaction.getId());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error occurred while checking for update due to ", e);
        } finally {
            isRunning = false;
        }

    }

    private void revertTransactionAndFundWallet(WalletTransferTransaction transferTransaction) {
        if (transferTransaction == null)
            return;

        Wallet wallet = transferTransaction.getWallet();
        if (wallet == null) {
            logger.warn("There's no wallet for this user");
            return;
        }

        transferTransaction.setTransactionStatus(TransactionStatus.BID_EXPIRED);
        transferTransaction.setLastUpdatedDate(new Date());

        BigDecimal currrentAvaiableBalance = wallet.getAvailableAccountBalance();
        BigDecimal currrentLedgerBalance = wallet.getLedgerAccountBalance();

        currrentAvaiableBalance = currrentAvaiableBalance.add(transferTransaction.getTotalAmount());

        wallet.setAvailableAccountBalance(currrentAvaiableBalance);

        walletRepository.save(wallet);
        walletTransferTransactionRepository.save(transferTransaction);

        WalletFundingTransaction walletFundingTransaction = new WalletFundingTransaction();
        walletFundingTransaction.setActualAmount(transferTransaction.getTotalAmount());

        walletFundingTransaction.setInitiatedDate(new Date());
        walletFundingTransaction.setLastUpdatedDate(new Date());
        walletFundingTransaction.setNarration("refund for expired bid");
        walletFundingTransaction.setCurrency(transferTransaction.getFromCurrency());
        walletFundingTransaction.setPaymentMethod(transferTransaction.getPaymentMethod());
        walletFundingTransaction.setTransactionStatus(TransactionStatus.BID_EXPIRED);
        walletFundingTransaction.setPaymentReferenceId(transferTransaction.getPaymentReferenceId());
        walletFundingTransaction.setPaaroReferenceId(transferTransaction.getPaaroReferenceId());
        walletFundingTransaction.setThirdPartyReferenceId(transferTransaction.getThirdPartyReferenceId());
        walletFundingTransaction.setWallet(wallet);
        walletFundingTransaction.setExchangeRate(0d);
        walletFundingTransaction.setManagedUser(wallet.getManagedUser());
        walletFundingTransaction.setActualAmount(transferTransaction.getTotalAmount());

        walletFundingTransactionRepository.save(walletFundingTransaction);

    }

}
