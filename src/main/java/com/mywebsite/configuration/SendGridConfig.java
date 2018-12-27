package com.mywebsite.configuration;

import com.mywebsite.configuration.beans.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;

@Configuration
public class SendGridConfig {

    @Value("${sendgrid.client.apiKey}")
    String sendgridApiKey;

    @Autowired
    TemplateEngine templateEngine;

    @Bean
    public EmailSender emailSender() {
        return new EmailSender(sendgridApiKey, templateEngine);
    }
}
