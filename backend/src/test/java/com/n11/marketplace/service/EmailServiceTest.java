package com.n11.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendWelcomeEmailShouldSendEmail() {
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail("user@example.com", "Test User"));

        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendVerificationCodeEmailShouldSendEmail() {
        assertDoesNotThrow(() -> emailService.sendVerificationCodeEmail("user@example.com", "123456"));

        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }
}
