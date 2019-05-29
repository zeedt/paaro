package com.plethub.paaro.core.usermanagement.service;

import com.plethub.paaro.core.appservice.sendgrid.SendGridEmail;
import com.plethub.paaro.core.models.EmailNotification;
import com.plethub.paaro.core.models.ManagedUser;
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
public class UserEmailService {

    private TemplateEngine templateEngine;

    @Autowired
    private SendGridEmail sendGridEmail;

    @Value("${email.activate-account:email-template/account-activation}")
    private String activateAccountPath;

    @Value("${email.activate-account-subject:Paaro - Account activation}")
    private String activateAccountSubject;

    @Value("${email.activate-account:email-template/account-deactivation}")
    private String deactivateAccountPath;

    @Value("${email.activate-account-subject:Paaro - Account deactivation}")
    private String deactivateAccountSubject;

    private Logger logger = LoggerFactory.getLogger(UserEmailService.class.getName());

    @Value("${email.forgot-password:email-template/password-reset}")
    private String forgotPasswordPath;

    @Value("${email.forgot-password-subject:Paaro - Password reset}")
    private String forgotPasswordSubject;


    @Value("${email.new-admin-user:email-template/admin-user-creation}")
    private String newAdminUserPath;

    @Value("${email.new-admin-user-subject:Paaro - Admin user created}")
    private String newAdminUserSubject;

    @Value("${email.new-user:email-template/registration-complete}")
    private String newUserPath;

    @Value("${email.new-user-subject:Paaro - Welcome!!!}")
    private String newUserSubject;

    @Value("${base.url:http://127.0.0.1:8012}")
    private String baseUrl;

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Autowired
    private UserEmailService(@Qualifier("emailTemplateEngine") TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void sendActivateAccountEmail(ManagedUser managedUser) throws Exception {

        executorService.submit(()->{
            try {
                EmailNotification emailNotification = new EmailNotification();
                emailNotification.setContent(getActivationEmailContent(managedUser));
                emailNotification.setSubject(activateAccountSubject);
                emailNotification.setTo(managedUser.getEmail());

                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (Exception e) {
                logger.error("error occurred while sending activation email to user due to ", e);
            }
        });
    }

    private String getActivationEmailContent(ManagedUser managedUser) {
        Context context = new Context();
        context.setVariable("email", managedUser.getEmail());
        context.setVariable("firstName", managedUser.getFirstName());
        return this.templateEngine.process(activateAccountPath,context);
    }


    public void sendDeactivateAccountEmail(ManagedUser managedUser) throws Exception {

        executorService.submit(()->{
            try {
                EmailNotification emailNotification = new EmailNotification();
                emailNotification.setContent(getDeactivationEmailContent(managedUser));
                emailNotification.setSubject(deactivateAccountSubject);
                emailNotification.setTo(managedUser.getEmail());
                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (Exception e) {
                logger.error("error occurred while sending deactivation email to user due to ", e);
            }
        });
    }

    private String getDeactivationEmailContent(ManagedUser managedUser) {
        Context context = new Context();
        context.setVariable("email", managedUser.getEmail());
        context.setVariable("firstName", managedUser.getFirstName());
        return this.templateEngine.process(deactivateAccountPath,context);
    }

    public void sendForgotPasswordEmail(String email, String password, String firstName) {

        executorService.submit(()->{
            try {
                EmailNotification emailNotification = new EmailNotification();
                emailNotification.setContent(getForgotPasswordEmailContent(firstName, password));
                emailNotification.setSubject(forgotPasswordSubject);
                emailNotification.setTo(email);
                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
                logger.info("Forgot Password mail sent successfully to {} ", email);
            } catch (Exception e) {
                logger.error("error occurred while sending new password email to user due to ", e);
            }
        });
    }

    private String getForgotPasswordEmailContent(String firstName, String password) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("password", password);
        return this.templateEngine.process(forgotPasswordPath,context);
    }

    public void sendNewAdminUserEmail(String email, String password, String firstName, String activationCode) {

        executorService.submit(()->{
            try {
                EmailNotification emailNotification = new EmailNotification();
                emailNotification.setContent(getNewAdminUserEmailContent(firstName, password, computeVerificationLink(email, activationCode)));
                emailNotification.setSubject(newAdminUserSubject);
                emailNotification.setTo(email);
                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (Exception e) {
                logger.error("error occurred while sending email to new admin user due to ", e);
            }
        });
    }

    private String getNewAdminUserEmailContent(String firstName, String password, String verificationLink) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("password", password);
        context.setVariable("verificationLink", verificationLink);
        return this.templateEngine.process(newAdminUserPath,context);
    }

    public void sendUserWelcomeEmail(String email, String firstName, String activationCode) {

        executorService.submit(()->{
            try {
                EmailNotification emailNotification = new EmailNotification();
                emailNotification.setContent(getNewserWelcomeEmailContent(firstName, computeVerificationLink(email, activationCode)));
                emailNotification.setSubject(newUserSubject);
                emailNotification.setTo(email);
                sendGridEmail.sendEmailWithNoAttachment(emailNotification);
            } catch (Exception e) {
                logger.error("error occurred while sending email to new user due to ", e);
            }
        });
    }

    private String getNewserWelcomeEmailContent(String firstName, String verificationLink) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("verificationLink", verificationLink);
        return this.templateEngine.process(newUserPath,context);
    }

    private String computeVerificationLink(String email, String activationCode) {
        return String.format("%s/user/activate?email=%s&activationCode=%s", baseUrl, email, activationCode);
    }

}
