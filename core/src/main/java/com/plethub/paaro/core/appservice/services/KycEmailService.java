package com.plethub.paaro.core.appservice.services;

import com.plethub.paaro.core.appservice.sendgrid.SendGridEmail;
import com.plethub.paaro.core.models.EmailNotification;
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
public class KycEmailService {


    @Autowired
    private SendGridEmail sendGridEmail;

    @Value("${email.kyc-approved:email-template/kyc-approved}")
    private String kycApprovedPath;

    @Value("${email.kyc-approved-subject:Paaro - KYC Approval!!!}")
    private String approvedKycSubject;

    private TemplateEngine templateEngine;

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    private Logger logger = LoggerFactory.getLogger(KycEmailService.class.getName());

    private KycEmailService(@Qualifier("emailTemplateEngine") TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void sendApprovedKycEmail(String email, String kycType, String firstName) {

        executorService.submit(()->{
            try {
                EmailNotification emailNotification = new EmailNotification();
                emailNotification.setContent(getKycApprovalEmailContent(firstName, kycType));
                emailNotification.setSubject(approvedKycSubject);
                emailNotification.setTo(email);
                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (Exception e) {
                logger.error(String.format("Error occurred while sending kyc approval (%s) email due to ", kycType), e);
            }
        });
    }


    private String getKycApprovalEmailContent(String firstName, String kycType) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("kycType", kycType);
        return this.templateEngine.process(kycApprovedPath,context);
    }


}
