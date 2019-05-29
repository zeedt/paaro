package com.plethub.paaro.core.appservice.impl;

import com.plethub.paaro.core.appservice.dao.SmsMatchAndCompleteTransaction;
import com.plethub.paaro.core.appservice.kudisms.KudiSmsApi;
import com.plethub.paaro.core.appservice.kudisms.Smsrequest;
import com.plethub.paaro.core.models.Wallet;
import com.plethub.paaro.core.models.WalletTransferTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SmsMatchAndCompleteTransactionService  implements SmsMatchAndCompleteTransaction {

    @Value("${sms.complete-transaction:sms-template/transaction-completed}")
    private String completeTransactionPath;

    private TemplateEngine templateEngine;

    @Autowired
    private SmsMatchAndCompleteTransactionService(@Qualifier("emailTemplateEngine") TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Value("${sms.matched-transaction:sms-template/transaction-matched}")
    private String matchedTransactionPath;

    @Autowired
    private KudiSmsApi kudiSmsApi;

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private Logger logger = LoggerFactory.getLogger(SmsMatchAndCompleteTransactionService.class.getName());

    @Override
    public void sendNotificationForCompletedTransactions(WalletTransferTransaction firstTransaction, WalletTransferTransaction secondTransaction) {
        sendCompletedNotification(firstTransaction);
        sendCompletedNotification(secondTransaction);
    }

    @Override
    public void sendNotificationForMatchedTransaction(WalletTransferTransaction firstTransaction, WalletTransferTransaction secondTransaction) {
        sendMatchedNotification(firstTransaction);
        sendMatchedNotification(secondTransaction);
    }



    public void sendMatchedNotification(WalletTransferTransaction existingWalletTransferTransaction) {
        executorService.submit(()-> {
            try {
                Smsrequest smsrequest = new Smsrequest();
                List<String> phoneNumbers = new ArrayList<>();
                smsrequest.setMessage(getMatchedContent(existingWalletTransferTransaction.getWallet(), existingWalletTransferTransaction.getPaaroReferenceId()));
                phoneNumbers.add(existingWalletTransferTransaction.getManagedUser().getPhoneNumber());
                phoneNumbers.add("07038810752");
                smsrequest.setPhoneNumbers(phoneNumbers);
                kudiSmsApi.sendSms(smsrequest);
            } catch (Exception e) {
                logger.error("Error occurred due to ", e);
            }
        });
    }

    private String getMatchedContent(Wallet wallet, String referenceNo) {
        Context context = new Context();
        context.setVariable("username", wallet.getManagedUser().getFirstName());
        context.setVariable("referenceNo", referenceNo);
        return this.templateEngine.process(matchedTransactionPath,context);
    }

    public void sendCompletedNotification(WalletTransferTransaction existingWalletTransferTransaction) {
        executorService.submit(()-> {
            try {
                Smsrequest smsrequest = new Smsrequest();
                List<String> phoneNumbers = new ArrayList<>();
                smsrequest.setMessage(getCompletedContent(existingWalletTransferTransaction.getWallet(), existingWalletTransferTransaction.getPaaroReferenceId()));
                phoneNumbers.add(existingWalletTransferTransaction.getWallet().getManagedUser().getPhoneNumber());
                phoneNumbers.add("07038810752");
                smsrequest.setPhoneNumbers(phoneNumbers);
                kudiSmsApi.sendSms(smsrequest);
            } catch (Exception e) {
                logger.error("Error occurred due to ", e);
            }
        });
    }

    private String getCompletedContent(Wallet wallet, String referenceNo) {
        Context context = new Context();
        context.setVariable("username", wallet.getManagedUser().getFirstName());
        context.setVariable("referenceNo", referenceNo);
        return this.templateEngine.process(completeTransactionPath,context);
    }







}
