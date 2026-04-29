package com.n11.marketplace.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.n11.marketplace.entity.User;
import com.n11.marketplace.enums.Role;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil(
            "test-secret-key-that-is-long-enough-for-hs256-token",
            86_400_000);

    @Test
    void generateTokenShouldCreateValidToken() {
        User user = new User("user@example.com", "encoded-password", "Test User", null, Role.USER);

        String token = jwtUtil.generateToken(user);

        assertFalse(token.isBlank());
        assertEquals(user.getEmail(), jwtUtil.extractEmail(token));
        assertTrue(jwtUtil.validateToken(token));
    }
}
