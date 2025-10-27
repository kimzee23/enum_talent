package org.example.enumtalentapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendVerificationEmail(String toEmail, String verificationToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Verify Your Email - EnumTalent");
            Context context = new Context();
            context.setVariable("verificationUrl", "http://localhost:8081/api/auth/verify?token=" + verificationToken);
            context.setVariable("supportEmail", "support@enumtalent.com");

            String htmlContent = templateEngine.process("email-verification", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Verification email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            sendSimpleVerificationEmail(toEmail, verificationToken);
        }
    }

    @Async
    public void sendSimpleVerificationEmail(String toEmail, String verificationToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Verify Your Email - EnumTalent");

            String textContent = """
                Welcome to EnumTalent!
                
                Please verify your email address by clicking the link below:
                http://localhost:8081/api/auth/verify?token=%s
                
                This link will expire in 24 hours.
                
                If you didn't create an account with EnumTalent, please ignore this email.
                
                Best regards,
                EnumTalent Team
                """.formatted(verificationToken);

            helper.setText(textContent);
            mailSender.send(message);
            log.info("Simple verification email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send simple verification email to: {}", toEmail, e);
        }
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String firstName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Welcome to EnumTalent!");

            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("supportEmail", "support@enumtalent.com");

            String htmlContent = templateEngine.process("email-welcome", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
        }
    }
}