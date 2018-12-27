package com.mywebsite.configuration.beans;

import com.sendgrid.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

public abstract class EmailSenderBase {

    static Logger log = LoggerFactory.getLogger(EmailSenderBase.class.getName());

    private final String apiKey;
    private final TemplateEngine templateEngine;

    protected EmailSenderBase(String apiKey, TemplateEngine templateEngine) {
        this.apiKey = apiKey;
        this.templateEngine = templateEngine;
    }

    protected void send(Context ctx, String emailTemplate, String subject, Email from, Email to, Email optionalBcc) {
        String htmlContent = templateEngine.process("emails/"+emailTemplate+".html", ctx);
        if("missing".equals(apiKey)) {
            log.warn("Not sending email, as there is no sendgrid api key found");
            log.info(htmlContent);
            return;
        }
        log.info(String.format("Sending %s from %s to %s", emailTemplate, from.getEmail(), to.getEmail()));
        sendEmail(to, from, subject, htmlContent, optionalBcc);
        log.info(String.format("Successfully sent %s from %s to %s", emailTemplate, from.getEmail(), to.getEmail()));
    }

    private void sendEmail(Email recipient, Email from, String subject, String htmlContent, Email optionalBcc) {
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);
        Personalization personalization = new Personalization();
        personalization.addTo(recipient);
        if(optionalBcc != null) {
            personalization.addBcc(optionalBcc);
        }
        mail.addPersonalization(personalization);
        mail.addContent(new Content("text/html", htmlContent));

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        try {
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = mail.build();
            Response response = sg.api(request);
            System.out.println(response.statusCode);
            System.out.println(response.body);
            System.out.println(response.headers);
        } catch (IOException ex) {
            log.error("Problem sending mail to "+recipient.getEmail(), ex);
        }
    }
}
