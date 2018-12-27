package com.mywebsite.configuration.beans;

import com.sendgrid.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class EmailSender extends EmailSenderBase {

    public static final Email SENDER_EMAIL = new Email("hello@mywebsite.com", "Team");

    public EmailSender(String apiKey, TemplateEngine templateEngine) {
        super(apiKey, templateEngine);
    }

    public void testEmail(Email to) {
        Context ctx = new Context();
        ctx.setVariable("name", "Bob");
        String subject = "Testing email";
        Email from = SENDER_EMAIL;
        send(ctx, "test-email", subject, from, to, null);
    }
}
