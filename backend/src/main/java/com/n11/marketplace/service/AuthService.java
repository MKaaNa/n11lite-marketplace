package com.n11.marketplace.service;

import com.n11.marketplace.dto.request.LoginRequest;
import com.n11.marketplace.dto.request.RegisterRequest;
import com.n11.marketplace.dto.request.VerifyLoginRequest;
import com.n11.marketplace.dto.response.JwtResponse;
import com.n11.marketplace.dto.response.MessageResponse;
import com.n11.marketplace.dto.response.UserResponse;
import com.n11.marketplace.dto.response.VerificationInitResponse;
import com.n11.marketplace.entity.LoginVerificationCode;
import com.n11.marketplace.entity.User;
import com.n11.marketplace.enums.Role;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.repository.LoginVerificationCodeRepository;
import com.n11.marketplace.repository.UserRepository;
import com.n11.marketplace.security.JwtUtil;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final int CODE_BOUND = 1_000_000;
    private static final int LOGIN_CODE_VALIDITY_MINUTES = 2;

    private final UserRepository userRepository;
    private final LoginVerificationCodeRepository loginVerificationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            UserRepository userRepository,
            LoginVerificationCodeRepository loginVerificationCodeRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.loginVerificationCodeRepository = loginVerificationCodeRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    public MessageResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists", HttpStatus.BAD_REQUEST);
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());
        User user = new User(
                request.getEmail(),
                passwordHash,
                request.getFullName(),
                request.getPhone(),
                Role.USER);

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());
        try {
            emailService.sendWelcomeEmail(request.getEmail(), request.getFullName());
        } catch (MailException e) {
            log.warn("Welcome email failed for {}", request.getEmail());
        }

        return new MessageResponse("Registration successful");
    }

    public VerificationInitResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        String code = generateVerificationCode();
        LoginVerificationCode verificationCode = new LoginVerificationCode(
                user.getEmail(),
                code,
                LocalDateTime.now().plusMinutes(LOGIN_CODE_VALIDITY_MINUTES));

        LoginVerificationCode savedVerificationCode = loginVerificationCodeRepository.save(verificationCode);
        emailService.sendVerificationCodeEmail(user.getEmail(), code);
        log.info("Login verification code sent for {}", user.getEmail());

        return new VerificationInitResponse(savedVerificationCode.getId(), "Verification code sent");
    }

    @Transactional
    public VerificationInitResponse resendLoginVerification(Long verificationId) {
        LoginVerificationCode previous = loginVerificationCodeRepository
                .findById(verificationId)
                .orElseThrow(() -> new BusinessException("Invalid verification", HttpStatus.BAD_REQUEST));

        if (previous.isUsed()) {
            throw new BusinessException("Verification code already used", HttpStatus.BAD_REQUEST);
        }

        String email = previous.getEmail();
        userRepository
                .findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.UNAUTHORIZED));

        previous.setUsed(true);
        loginVerificationCodeRepository.save(previous);

        String code = generateVerificationCode();
        LoginVerificationCode next = new LoginVerificationCode(
                email,
                code,
                LocalDateTime.now().plusMinutes(LOGIN_CODE_VALIDITY_MINUTES));

        LoginVerificationCode saved = loginVerificationCodeRepository.save(next);
        emailService.sendVerificationCodeEmail(email, code);
        log.info("Login verification code resent for {}", email);

        return new VerificationInitResponse(saved.getId(), "Verification code sent");
    }

    @Transactional
    public JwtResponse verifyLoginCode(VerifyLoginRequest request) {
        LoginVerificationCode verificationCode = loginVerificationCodeRepository.findById(request.getVerificationId())
                .orElseThrow(() -> new BusinessException("Invalid verification code", HttpStatus.BAD_REQUEST));

        if (verificationCode.isUsed()) {
            throw new BusinessException("Verification code already used", HttpStatus.BAD_REQUEST);
        }

        if (verificationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Verification code expired", HttpStatus.BAD_REQUEST);
        }

        if (!verificationCode.getCode().equals(request.getCode())) {
            throw new BusinessException("Invalid verification code", HttpStatus.BAD_REQUEST);
        }

        verificationCode.setUsed(true);
        loginVerificationCodeRepository.save(verificationCode);

        User user = userRepository.findByEmail(verificationCode.getEmail())
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.UNAUTHORIZED));

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name());

        String token = jwtUtil.generateToken(user);
        log.info("Login verified successfully for {}", user.getEmail());
        return new JwtResponse(token, userResponse);
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.UNAUTHORIZED));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name());
    }

    private String generateVerificationCode() {
        return String.format("%06d", secureRandom.nextInt(CODE_BOUND));
    }
}
