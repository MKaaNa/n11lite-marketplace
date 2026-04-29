package com.n11.marketplace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11.marketplace.dto.request.LoginRequest;
import com.n11.marketplace.dto.request.RegisterRequest;
import com.n11.marketplace.dto.request.VerifyLoginRequest;
import com.n11.marketplace.dto.response.JwtResponse;
import com.n11.marketplace.dto.response.MessageResponse;
import com.n11.marketplace.dto.response.UserResponse;
import com.n11.marketplace.dto.response.VerificationInitResponse;
import com.n11.marketplace.security.JwtFilter;
import com.n11.marketplace.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Test
    void registerShouldReturnMessage() throws Exception {
        RegisterRequest request = new RegisterRequest("user@example.com", "password123", "Test User", null);
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new MessageResponse("Registration successful"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    void loginShouldReturnVerificationResponse() throws Exception {
        LoginRequest request = new LoginRequest("user@example.com", "password123");
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new VerificationInitResponse(1L, "Verification code sent"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationId").value(1))
                .andExpect(jsonPath("$.message").value("Verification code sent"));
    }

    @Test
    void verifyLoginShouldReturnJwtResponse() throws Exception {
        VerifyLoginRequest request = new VerifyLoginRequest(1L, "123456");
        UserResponse user = new UserResponse(1L, "user@example.com", "Test User", "USER");
        when(authService.verifyLoginCode(any(VerifyLoginRequest.class)))
                .thenReturn(new JwtResponse("jwt-token", user));

        mockMvc.perform(post("/api/auth/verify-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.email").value("user@example.com"))
                .andExpect(jsonPath("$.user.role").value("USER"));
    }

    @Test
    void registerShouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest("invalid-email", "short", "Test User", null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.startsWith("Validation failed: ")));
    }

    @Test
    void meShouldReturnCurrentUser() throws Exception {
        UserResponse user = new UserResponse(1L, "user@example.com", "Test User", "USER");
        when(authService.getCurrentUser("user@example.com")).thenReturn(user);

        mockMvc.perform(get("/api/auth/me")
                        .principal(() -> "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
}
