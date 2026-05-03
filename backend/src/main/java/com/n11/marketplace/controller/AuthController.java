package com.n11.marketplace.controller;

import com.n11.marketplace.dto.request.LoginRequest;
import com.n11.marketplace.dto.request.RegisterRequest;
import com.n11.marketplace.dto.request.ResendLoginVerificationRequest;
import com.n11.marketplace.dto.request.VerifyLoginRequest;
import com.n11.marketplace.dto.response.JwtResponse;
import com.n11.marketplace.dto.response.MessageResponse;
import com.n11.marketplace.dto.response.UserResponse;
import com.n11.marketplace.dto.response.VerificationInitResponse;
import com.n11.marketplace.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Start login verification")
    public ResponseEntity<VerificationInitResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/verify-login")
    @Operation(summary = "Verify login code")
    public ResponseEntity<JwtResponse> verifyLogin(@Valid @RequestBody VerifyLoginRequest request) {
        return ResponseEntity.ok(authService.verifyLoginCode(request));
    }

    @PostMapping("/resend-login-verification")
    @Operation(summary = "Resend login verification code")
    public ResponseEntity<VerificationInitResponse> resendLoginVerification(
            @Valid @RequestBody ResendLoginVerificationRequest request) {
        return ResponseEntity.ok(authService.resendLoginVerification(request.getVerificationId()));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user")
    public ResponseEntity<UserResponse> me(Principal principal) {
        return ResponseEntity.ok(authService.getCurrentUser(principal.getName()));
    }
}
