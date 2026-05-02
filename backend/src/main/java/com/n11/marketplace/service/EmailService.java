package com.n11.marketplace.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendWelcomeEmail(String email, String fullName) {
        log.info("Sending welcome email to {}", email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@n11lite.com");
        message.setTo(email);
        message.setSubject("Welcome to N11Lite");
        message.setText("Hello " + fullName + ", welcome to N11Lite.");

        javaMailSender.send(message);
    }

    public void sendOrderShippedEmail(String email, Long orderId) {
        log.info("Sending shipped email for order {} to {}", orderId, email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@n11lite.com");
        message.setTo(email);
        message.setSubject("N11Lite - Siparişiniz Kargoya Verildi");
        message.setText("Siparişiniz (" + orderId + ") kargoya verildi. İyi alışverişler!");

        javaMailSender.send(message);
    }

    public void sendOrderDeliveredEmail(String email, Long orderId) {
        log.info("Sending delivered email for order {} to {}", orderId, email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@n11lite.com");
        message.setTo(email);
        message.setSubject("N11Lite - Siparişiniz Teslim Edildi");
        message.setText("Siparişiniz (" + orderId + ") teslim edildi. Teşekkür ederiz!");

        javaMailSender.send(message);
    }

    public void sendVerificationCodeEmail(String email, String code) {
        log.info("Sending login verification email to {}", email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@n11lite.com");
        message.setTo(email);
        message.setSubject("N11Lite Login Verification Code");
        message.setText("Your login verification code is: " + code);

        javaMailSender.send(message);
    }
}
