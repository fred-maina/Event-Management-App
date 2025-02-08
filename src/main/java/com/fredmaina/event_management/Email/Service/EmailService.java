package com.fredmaina.event_management.Email.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Objects;

@Service
public class EmailService {

    // Define your authorized sender email address as a constant
    private static final String FROM_ADDRESS = "events.noreply@fredmaina.com";

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Async
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        // Set the sender's email address
        message.setFrom(FROM_ADDRESS);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    @Async
    public void sendHtmlEmail(String to, int code, String fullName, String subject) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            // Set the sender's email address for the HTML email
            helper.setFrom(FROM_ADDRESS);
            helper.setTo(to);
            helper.setSubject(subject);
            // Generate the email content based on the subject
            String content = htmlContent(subject, code, fullName);
            helper.setText(Objects.requireNonNull(content), true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Optionally add more error handling here
        }
    }

    private String htmlContent(String subject, int code, String fullName) {
        Context context = new Context();
        context.setVariable("fullName", fullName);

        switch (subject) {
            case "resetPassword":
                context.setVariable("resetCode", code);
                return templateEngine.process("password-reset", context);
            case "verificationCode":
                context.setVariable("verificationCode", code);
                return templateEngine.process("email-verification", context);
            default:
                return null;
        }
    }
}
