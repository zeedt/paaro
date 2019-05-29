package com.plethub.paaro.core.appservice.impl;

import com.plethub.paaro.core.appservice.dao.EmailMatchAndCompleteTransaction;
import com.plethub.paaro.core.models.EmailNotification;
import com.plethub.paaro.core.models.Wallet;
import com.plethub.paaro.core.models.WalletTransferTransaction;
import com.plethub.paaro.core.appservice.sendgrid.SendGridEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class EmailMatchAndCompleteTransactionService implements EmailMatchAndCompleteTransaction {

    private Logger logger = LoggerFactory.getLogger(EmailMatchAndCompleteTransactionService.class.getName());

    @Autowired
    private SendGridEmail sendGridEmail;

    private ExecutorService executorService =  Executors.newFixedThreadPool(4);

    private TemplateEngine templateEngine;

    @Autowired
    private EmailMatchAndCompleteTransactionService(@Qualifier("emailTemplateEngine") TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Value("${email.transaction-complete:email-template/transaction-completed}")
    private String completeTransactionPath;

    @Value("${email.transaction-complete-subject:Paaro - Transfer Request completed}")
    private String completeTransactionSubject;

    @Value("${email.transaction-matched:email-template/transaction-matched}")
    private String matchTransactionPath;

    @Value("${email.transaction-matched-subject:Paaro - Transfer request matched}")
    private String matchTransactionSubject;


    @Override
    public void sendNotificationForCompletedTransactions(WalletTransferTransaction firstTransaction, WalletTransferTransaction secondTransaction) {

    }

    @Override
    public void sendNotificationForMatchedTransaction(WalletTransferTransaction firstTransaction, WalletTransferTransaction secondTransaction) {
        sendEmailForMatch(firstTransaction);
        sendEmailForMatch(secondTransaction);
    }


    public void sendEmailForMatch(WalletTransferTransaction existingWalletTransferTransaction) {
        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setContent(getMatchEmailContentFromWallet(existingWalletTransferTransaction.getWallet(), existingWalletTransferTransaction.getPaaroReferenceId()));
        emailNotification.setSubject(matchTransactionSubject);
        emailNotification.setTo(existingWalletTransferTransaction.getManagedUser().getEmail());
        emailNotification.addTo("soluwawunmi@gmail.com");
        emailNotification.addTo(existingWalletTransferTransaction.getManagedUser().getEmail());

        executorService.submit(()->{
            try {
                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (IOException e) {

            }
        });
    }

    private String getMatchEmailContentFromWallet(Wallet wallet, String referenceNo) {
        Context context = new Context();
        context.setVariable("username", wallet.getManagedUser().getFirstName());
        context.setVariable("referenceNo", referenceNo);
        return this.templateEngine.process(matchTransactionPath,context);
    }


    public void sendEmailForCompleted(WalletTransferTransaction existingWalletTransferTransaction) {
        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setContent(getCompletedEmailContentFromWallet(existingWalletTransferTransaction.getWallet(), existingWalletTransferTransaction.getPaaroReferenceId()));
        emailNotification.setSubject(completeTransactionSubject);
        emailNotification.setTo("yusufsaheedtaiwo@gmail.com");
        emailNotification.addTo("soluwawunmi@gmail.com");
        emailNotification.addTo(existingWalletTransferTransaction.getWallet().getManagedUser().getEmail());

        executorService.submit(()->{
            try {
                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (IOException e) {

            }
        });
    }

    private String getCompletedEmailContentFromWallet(Wallet wallet, String referenceNo) {
        Context context = new Context();
        context.setVariable("username", wallet.getManagedUser().getFirstName());
        context.setVariable("referenceNo", referenceNo);
        return this.templateEngine.process(completeTransactionPath,context);
    }

}
