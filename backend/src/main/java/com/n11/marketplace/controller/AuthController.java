package com.n11.marketplace.controller;

import com.n11.marketplace.dto.request.LoginRequest;
import com.n11.marketplace.dto.request.RegisterRequest;
import com.n11.marketplace.dto.request.VerifyLoginRequest;
import com.n11.marketplace.dto.response.JwtResponse;
import com.n11.marketplace.dto.response.MessageResponse;
import com.n11.marketplace.dto.response.UserResponse;
import com.n11.marketplace.dto.response.VerificationInitResponse;
import com.n11.marketplace.service.AuthService;
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
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<VerificationInitResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/verify-login")
    public ResponseEntity<JwtResponse> verifyLogin(@Valid @RequestBody VerifyLoginRequest request) {
        return ResponseEntity.ok(authService.verifyLoginCode(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Principal principal) {
        return ResponseEntity.ok(authService.getCurrentUser(principal.getName()));
    }
}
