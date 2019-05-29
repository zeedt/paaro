package com.plethub.paaro.core.appservice.services;

import com.plethub.paaro.core.models.*;
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
import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WalletEmailService {

    private TemplateEngine templateEngine;

    @Autowired
    private SendGridEmail sendGridEmail;

    @Value("${email.add-wallet-to-account:email-template/wallet-to-account}")
    private String addWalletToAccountPath;

    @Value("${email.add-wallet-to-account-subject:Paaro - Wallet Creation}")
    private String addWalletToAccountSubject;

    @Value("${email.fund-wallet:email-template/wallet-funding}")
    private String fundWalletPath;

    @Value("${email.fund-wallet-subject:Paaro - Wallet Funding}")
    private String fundWalletSubject;

    @Value("${email.transfer-request:email-template/transfer-request}")
    private String transferRequestPath;

    @Value("${email.transfer-request-subject:Paaro - Transfer Request}")
    private String transferRequestSubject;

    @Value("${email.fund-wallet-request:email-template/wallet-funding-request}")
    private String walletFundingRequestPath;

    @Value("${email.fund-wallet-request-subject:Paaro - Wallet Funding Request}")
    private String walletFundingRequestSubject;

    @Value("${email.fund-approval-request:email-template/wallet-funding-request-approval}")
    private String walletFundingApprovalRequestPath;

    @Value("${email.fund-approval-request-subject:Paaro - Wallet Funding Approval}")
    private String walletFundingApprovalRequestSubject;

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private Logger logger = LoggerFactory.getLogger(WalletEmailService.class.getName());


    @Autowired
    private WalletEmailService(@Qualifier("emailTemplateEngine") TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void sendWalletAdditionToAccountEmail(Wallet wallet) {

        executorService.submit(()->{
            try {
                EmailNotification emailNotification = new EmailNotification();
                emailNotification.setContent(getAddWalletEmailContentFromWallet(wallet));
                emailNotification.setSubject(addWalletToAccountSubject);
                emailNotification.setTo(wallet.getManagedUser().getEmail());

                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (Exception e) {
                logger.error("Error occurred while sending wallet addition email due to ", e);
            }
        });
    }

    public void sendWalletFundingEmail(Wallet wallet, BigDecimal actualAmount) {

        executorService.submit(()->{
            try {
                EmailNotification emailNotification = new EmailNotification();
                emailNotification.setContent(getFundingEmailContentFromWallet(wallet, actualAmount));
                emailNotification.setSubject(fundWalletSubject);
                emailNotification.setTo(wallet.getManagedUser().getEmail());
                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (Exception e) {
                logger.error("Error occurred while sending funding email due to ", e);
            }
        });
    }

    public void sendTransferRequestEmail(Transaction transaction) {

        executorService.submit(()->{
            try {
                EmailNotification emailNotification = new EmailNotification();
                emailNotification.setContent(getTransferRequestEmailContent(transaction));
                emailNotification.setSubject(transferRequestSubject);
                emailNotification.setTo(transaction.getWallet().getManagedUser().getEmail());

                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (Exception e) {
                logger.error("Error occurred while sending transfer request email due to ", e);
            }
        });
    }


    private String getAddWalletEmailContentFromWallet(Wallet wallet) {
        Context context = new Context();
        context.setVariable("username", wallet.getManagedUser().getFirstName());
        context.setVariable("currencyType", wallet.getCurrency().getDescription());
        return this.templateEngine.process(addWalletToAccountPath,context);
    }


    private String getFundingEmailContentFromWallet(Wallet wallet, BigDecimal actualAmount) {
        Context context = new Context();
        context.setVariable("username", wallet.getManagedUser().getFirstName());
        context.setVariable("currencyType", wallet.getCurrency().getDescription());
        context.setVariable("availableBalance", wallet.getAvailableAccountBalance());
        context.setVariable("ledgerBalance", wallet.getLedgerAccountBalance());
        context.setVariable("actualAmount", actualAmount);
        return this.templateEngine.process(fundWalletPath,context);
    }

    private String getTransferRequestEmailContent(Transaction transaction) {
        Context context = new Context();
        context.setVariable("username", transaction.getWallet().getManagedUser().getFirstName());
        context.setVariable("currencyType", transaction.getWallet().getCurrency().getDescription());
        context.setVariable("recipientCurrency", transaction.getToCurrency().getDescription());
        context.setVariable("actualAmount", transaction.getActualAmount());
        context.setVariable("exchangeRate", transaction.getExchangeRate());
        context.setVariable("chargeAmount", transaction.getChargeAmount());
        context.setVariable("totalAmount", transaction.getTotalAmount());
        context.setVariable("accountName", transaction.getToAccountName());
        context.setVariable("accountNumber", transaction.getToAccountNumber());
        context.setVariable("referenceNo", transaction.getPaaroReferenceId());
        context.setVariable("availableBalance", transaction.getWallet().getAvailableAccountBalance());
        context.setVariable("ledgerBalance", transaction.getWallet().getLedgerAccountBalance());
        return this.templateEngine.process(transferRequestPath,context);

    }

    public void sendToken(String email, String token) {

        executorService.submit(()->{
            try {
                EmailNotification emailNotification = new EmailNotification();
                emailNotification.setContent("Dear user<p></p> Your token is " + token);
                emailNotification.setSubject(transferRequestSubject);
                emailNotification.setTo(email);
                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (Exception e) {
                logger.error("Error occurred while sending token due to ", e);
            }
        });
    }


    public void sendWalletFundingEmailForMatch(WalletTransferTransaction existingWalletTransferTransaction) {
        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setContent(getWalletFundingRequestEmailContentFromWallet(existingWalletTransferTransaction.getWallet(), existingWalletTransferTransaction.getTotalAmount()));
        emailNotification.setSubject(fundWalletSubject);
        emailNotification.setTo(existingWalletTransferTransaction.getWallet().getManagedUser().getEmail());

        executorService.submit(()->{
            try {
                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (Exception e) {
             logger.error("Error occurred while sending funding email for match due to ", e);
            }
        });
    }

    private String getWalletFundingRequestEmailContentFromWallet(Wallet wallet, BigDecimal totalAmount) {
        Context context = new Context();
        context.setVariable("username", wallet.getManagedUser().getFirstName());
        context.setVariable("currencyType", wallet.getCurrency().getDescription());
        context.setVariable("totalAmount", totalAmount);
        return this.templateEngine.process(walletFundingRequestPath,context);
    }

    public void sendWalletFundingApprovalEmail(PaymentReference paymentReference) {
        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setContent(getWalletFundingApprovalRequestEmailContent(paymentReference.getWallet().getCurrency().getType(), paymentReference.getPaymentReferenceNumber(), paymentReference.getWallet().getManagedUser().getFirstName()));
        emailNotification.setSubject(walletFundingApprovalRequestSubject);
        emailNotification.setTo(paymentReference.getWallet().getManagedUser().getEmail());

        executorService.submit(()->{
            try {
                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (Exception e) {
             logger.error("Error occurred while sending deposit approval email due to ", e);
            }
        });
    }

    private String getWalletFundingApprovalRequestEmailContent(String currencyType, String referenceId, String firstName) {
        Context context = new Context();
        context.setVariable("currencyType", currencyType);
        context.setVariable("referenceId", referenceId);
        context.setVariable("firstName", firstName);
        return this.templateEngine.process(walletFundingApprovalRequestPath,context);
    }

}
