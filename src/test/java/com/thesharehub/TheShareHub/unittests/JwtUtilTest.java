package com.thesharehub.TheShareHub.unittests;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("VGhpc0lzQVRlc3QyNTZCaXRKYXZhSldUS2V5MTIzNDU2");
    }

    @Test
    void generateToken_shouldReturnValidJwt() {
        Long userId = 42L;

        String token = jwtUtil.generateToken(userId);

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // header.payload.signature
    }

    @Test
    void extractUserId_shouldReturnCorrectUserId() {
        Long userId = 99L;
        String token = jwtUtil.generateToken(userId);

        Long extractedUserId = jwtUtil.extractUserId(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    void isTokenValid_shouldReturnTrue_forValidToken() {
        String token = jwtUtil.generateToken(1L);

        boolean isValid = jwtUtil.isTokenValid(token);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_shouldReturnFalse_forTamperedToken() {
        String token = jwtUtil.generateToken(1L);

        // Tamper the token (invalidate signature)
        String tamperedToken = token.substring(0, token.length() - 2) + "xx";

        boolean isValid = jwtUtil.isTokenValid(tamperedToken);

        assertFalse(isValid);
    }

    @Test
    void extractUserId_shouldThrowException_forInvalidToken() {
        String invalidToken = "this.is.not.a.jwt";

        assertThrows(JwtException.class, () ->
                jwtUtil.extractUserId(invalidToken)
        );
    }
}
