package com.plethub.paaro.core.appservice.services;

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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WalletSmsService {

    @Value("${sms.fund-wallet:sms-template/wallet-funding-request}")
    private String walletFundingRequestPath;

    @Value("${sms.fund-wallet-subject:Paaro - Wallet Funding Request}")
    private String walletFundingRequestSubject;

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private TemplateEngine templateEngine;

    private Logger logger = LoggerFactory.getLogger(WalletSmsService.class.getName());

    @Autowired
    private KudiSmsApi kudiSmsApi;

    @Autowired
    private WalletSmsService(@Qualifier("emailTemplateEngine") TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void sendWalletFundingSmsForMatch(WalletTransferTransaction existingWalletTransferTransaction) {
        executorService.submit(()->{
            try {
                Smsrequest smsrequest = new Smsrequest();
                List<String> phoneNumbers = new ArrayList<>();
                smsrequest.setMessage(getWalletFundingRequestSmsContentFromWallet(existingWalletTransferTransaction.getWallet(), existingWalletTransferTransaction.getTotalAmount()));
                phoneNumbers.add(existingWalletTransferTransaction.getWallet().getManagedUser().getPhoneNumber());
                smsrequest.setPhoneNumbers(phoneNumbers);
                kudiSmsApi.sendSms(smsrequest);
            } catch (Exception e) {
                logger.error("Error occurred due to ", e);
            }
        });
    }

    private String getWalletFundingRequestSmsContentFromWallet(Wallet wallet, BigDecimal totalAmount) {
        Context context = new Context();
        context.setVariable("username", wallet.getManagedUser().getFirstName());
        context.setVariable("currencyType", wallet.getCurrency().getDescription());
        context.setVariable("totalAmount", totalAmount);
        return this.templateEngine.process(walletFundingRequestPath,context);
    }

}
