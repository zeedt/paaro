package com.plethub.paaro.core.appservice.sendgrid;

import com.plethub.paaro.core.models.EmailNotification;
import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SendGridEmail {

    @Autowired
    private SendGrid sendGrid;

    @Value("${email.sender.address:admin@paaro.exchange}")
    private String senderEmail;
    @Value("${email.sender.name:Paaro Exchange}")
    private String senderName;

    public void sendEmailWithNoAttachment(EmailNotification emailNotification) throws IOException {

        Email sender = new Email(senderEmail, senderName);
        Email receiver = new Email(emailNotification.getTo());
        Content content = new Content("text/html",emailNotification.getContent());

        Mail mail = new Mail(sender, emailNotification.getSubject(), receiver, content);

        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        List<Email> tos = getEmailsFromStrings(emailNotification.getTos());
        List<Email> cc = getEmailsFromStrings(emailNotification.getCcs());
        List<Email> bcc = getEmailsFromStrings(emailNotification.getBcs());

        if (!CollectionUtils.isEmpty(tos)) {
            tos.forEach(toEmail->
                mail.personalization.get(0).addTo(toEmail)
            );
        }

        if (!CollectionUtils.isEmpty(cc)) {
            cc.forEach(ccEmail->
                mail.personalization.get(0).addCc(ccEmail)
            );
        }

        if (!CollectionUtils.isEmpty(bcc)) {
            bcc.forEach(bccEmail->
                mail.personalization.get(0).addBcc(bccEmail)
            );
        }

        sendGrid.api(request);

    }

    public List<Email> getEmailsFromStrings (List<String> emails) {
        if (CollectionUtils.isEmpty(emails)) {
            return new ArrayList<>();
        }

        List<Email> emailsList = new ArrayList<>();
        emails.stream().parallel().forEach(email->{
            if (!StringUtils.isEmpty(email)) {
                Email email1 = new Email(email);
                emailsList.add(email1);
            }
        });

        return emailsList;
    }

}
