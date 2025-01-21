package com.openwiki.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService();
    }

    @Test
    void validateToken_withInvalidToken_returnsFalse() throws Exception {
        // Test con token non valido
        String invalidToken = "invalid_token";
        boolean result = authService.validateToken(invalidToken);
        assertFalse(result);
    }

    @Test
    void validateFlaskSession_withInvalidSession_returnsFalse() {
        // Test con sessione non valida
        String invalidSession = "invalid_session";
        boolean result = authService.validateFlaskSession(invalidSession);
        assertFalse(result);
    }

    @Test
    void login_withInvalidCredentials_throwsException() {
        // Test con credenziali non valide
        assertThrows(RuntimeException.class, () -> {
            authService.login("invalid_user", "invalid_password");
        });
    }
} 