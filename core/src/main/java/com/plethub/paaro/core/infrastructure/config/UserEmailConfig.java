package com.plethub.paaro.core.infrastructure.config;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class UserEmailConfig {

    @Value("${spring.sendgrid.api-key}")
    public String sendGridApiKey;

    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(sendGridApiKey);
    }

    @Bean(name="emailTemplateEngine")
    public TemplateEngine emailTemplateEngine() {

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(emailTemplateResolver());
        return templateEngine;
    }


    private ITemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver loaderTemplateResolver = new ClassLoaderTemplateResolver();
        loaderTemplateResolver.setPrefix("/templates/");
        loaderTemplateResolver.setSuffix(".html");
        loaderTemplateResolver.setCacheable(true);
        loaderTemplateResolver.setTemplateMode(TemplateMode.HTML);
        return loaderTemplateResolver;
    }


}
