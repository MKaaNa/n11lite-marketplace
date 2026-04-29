package com.n11.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.n11.marketplace.dto.request.LoginRequest;
import com.n11.marketplace.dto.request.RegisterRequest;
import com.n11.marketplace.dto.request.VerifyLoginRequest;
import com.n11.marketplace.dto.response.JwtResponse;
import com.n11.marketplace.dto.response.MessageResponse;
import com.n11.marketplace.dto.response.VerificationInitResponse;
import com.n11.marketplace.entity.LoginVerificationCode;
import com.n11.marketplace.entity.User;
import com.n11.marketplace.enums.Role;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.repository.LoginVerificationCodeRepository;
import com.n11.marketplace.repository.UserRepository;
import com.n11.marketplace.security.JwtUtil;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoginVerificationCodeRepository loginVerificationCodeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtUtil jwtUtil;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                loginVerificationCodeRepository,
                passwordEncoder,
                emailService,
                jwtUtil);
    }

    @Test
    void registerShouldReturnSuccessWhenWelcomeEmailFails() {
        RegisterRequest request = new RegisterRequest("user@example.com", "password123", "Test User", "5551112233");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        doThrow(new MailSendException("mail down"))
                .when(emailService).sendWelcomeEmail(request.getEmail(), request.getFullName());

        MessageResponse response = authService.register(request);

        assertEquals("Registration successful", response.getMessage());
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail(request.getEmail(), request.getFullName());
    }

    @Test
    void registerShouldSaveUserAndSendWelcomeEmail() {
        RegisterRequest request = new RegisterRequest("user@example.com", "password123", "Test User", "5551112233");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");

        MessageResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("Registration successful", response.getMessage());
        assertEquals(request.getEmail(), savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPasswordHash());
        assertEquals(request.getFullName(), savedUser.getFullName());
        assertEquals(Role.USER, savedUser.getRole());
        verify(emailService).sendWelcomeEmail(request.getEmail(), request.getFullName());
    }

    @Test
    void loginShouldPropagateMailExceptionWhenVerificationEmailFails() {
        LoginRequest request = new LoginRequest("user@example.com", "password123");
        User user = new User(request.getEmail(), "encoded-password", "Test User", null, Role.USER);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(true);
        when(loginVerificationCodeRepository.save(any(LoginVerificationCode.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(new MailSendException("mail down"))
                .when(emailService).sendVerificationCodeEmail(eq(request.getEmail()), matches("\\d{6}"));

        assertThrows(MailSendException.class, () -> authService.login(request));

        verify(loginVerificationCodeRepository).save(any(LoginVerificationCode.class));
    }

    @Test
    void registerShouldThrowWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("user@example.com", "password123", "Test User", null);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendWelcomeEmail(any(), any());
    }

    @Test
    void loginShouldSaveVerificationCodeAndSendEmailWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("user@example.com", "password123");
        User user = new User(request.getEmail(), "encoded-password", "Test User", null, Role.USER);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(true);
        when(loginVerificationCodeRepository.save(any(LoginVerificationCode.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        VerificationInitResponse response = authService.login(request);

        ArgumentCaptor<LoginVerificationCode> codeCaptor = ArgumentCaptor.forClass(LoginVerificationCode.class);
        verify(loginVerificationCodeRepository).save(codeCaptor.capture());
        LoginVerificationCode savedCode = codeCaptor.getValue();

        assertEquals("Verification code sent", response.getMessage());
        assertEquals(request.getEmail(), savedCode.getEmail());
        verify(emailService).sendVerificationCodeEmail(eq(request.getEmail()), matches("\\d{6}"));
    }

    @Test
    void loginShouldThrowWhenUserNotFound() {
        LoginRequest request = new LoginRequest("missing@example.com", "password123");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        verify(loginVerificationCodeRepository, never()).save(any(LoginVerificationCode.class));
        verify(emailService, never()).sendVerificationCodeEmail(any(), any());
    }

    @Test
    void loginShouldThrowWhenPasswordIsWrong() {
        LoginRequest request = new LoginRequest("user@example.com", "wrong-password");
        User user = new User(request.getEmail(), "encoded-password", "Test User", null, Role.USER);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        verify(loginVerificationCodeRepository, never()).save(any(LoginVerificationCode.class));
        verify(emailService, never()).sendVerificationCodeEmail(any(), any());
    }

    @Test
    void verifyLoginCodeShouldReturnUserWhenCodeIsValid() {
        VerifyLoginRequest request = new VerifyLoginRequest(1L, "123456");
        LoginVerificationCode verificationCode = new LoginVerificationCode(
                "user@example.com",
                "123456",
                LocalDateTime.now().plusMinutes(5));
        User user = new User("user@example.com", "encoded-password", "Test User", null, Role.USER);
        when(loginVerificationCodeRepository.findById(request.getVerificationId()))
                .thenReturn(Optional.of(verificationCode));
        when(userRepository.findByEmail(verificationCode.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        JwtResponse response = authService.verifyLoginCode(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals(user.getEmail(), response.getUser().getEmail());
        assertEquals(user.getFullName(), response.getUser().getFullName());
        assertEquals(Role.USER.name(), response.getUser().getRole());
        assertTrue(verificationCode.isUsed());
        verify(loginVerificationCodeRepository).save(verificationCode);
    }

    @Test
    void verifyLoginCodeShouldThrowWhenCodeIsExpired() {
        VerifyLoginRequest request = new VerifyLoginRequest(1L, "123456");
        LoginVerificationCode verificationCode = new LoginVerificationCode(
                "user@example.com",
                "123456",
                LocalDateTime.now().minusMinutes(1));
        when(loginVerificationCodeRepository.findById(request.getVerificationId()))
                .thenReturn(Optional.of(verificationCode));

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.verifyLoginCode(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(loginVerificationCodeRepository, never()).save(any(LoginVerificationCode.class));
    }

    @Test
    void verifyLoginCodeShouldThrowWhenCodeIsAlreadyUsed() {
        VerifyLoginRequest request = new VerifyLoginRequest(1L, "123456");
        LoginVerificationCode verificationCode = new LoginVerificationCode(
                "user@example.com",
                "123456",
                LocalDateTime.now().plusMinutes(5));
        verificationCode.setUsed(true);
        when(loginVerificationCodeRepository.findById(request.getVerificationId()))
                .thenReturn(Optional.of(verificationCode));

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.verifyLoginCode(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(loginVerificationCodeRepository, never()).save(any(LoginVerificationCode.class));
    }

    @Test
    void verifyLoginCodeShouldThrowWhenCodeIsWrong() {
        VerifyLoginRequest request = new VerifyLoginRequest(1L, "654321");
        LoginVerificationCode verificationCode = new LoginVerificationCode(
                "user@example.com",
                "123456",
                LocalDateTime.now().plusMinutes(5));
        when(loginVerificationCodeRepository.findById(request.getVerificationId()))
                .thenReturn(Optional.of(verificationCode));

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.verifyLoginCode(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(loginVerificationCodeRepository, never()).save(any(LoginVerificationCode.class));
    }

    @Test
    void getCurrentUserShouldReturnUserResponse() {
        User user = new User("user@example.com", "encoded-password", "Test User", null, Role.USER);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        var response = authService.getCurrentUser(user.getEmail());

        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getFullName(), response.getFullName());
        assertEquals(Role.USER.name(), response.getRole());
    }

    @Test
    void getCurrentUserShouldThrowWhenUserMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.getCurrentUser("missing@example.com"));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }
}
