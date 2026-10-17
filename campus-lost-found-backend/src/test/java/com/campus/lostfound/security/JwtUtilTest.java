package com.campus.lostfound.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JWT 工具类单元测试")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "test-secret-key-1234567890-test-key");
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiry", 86400L);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiry", 604800L);
        ReflectionTestUtils.setField(jwtUtil, "issuer", "campus-lost-found");
    }

    @Test
    @DisplayName("生成并校验 Access Token")
    void testGenerateAndVerifyAccessToken() {
        Long userId = 1001L;
        String role = "STUDENT";

        String token = jwtUtil.generateAccessToken(userId, role);
        assertNotNull(token);

        Long extractedUserId = jwtUtil.getUserIdFromToken(token);
        String extractedRole = jwtUtil.getRoleFromToken(token);
        String tokenType = jwtUtil.getTypeFromToken(token);

        assertEquals(userId, extractedUserId);
        assertEquals(role, extractedRole);
        assertEquals("access", tokenType);
    }

    @Test
    @DisplayName("生成并校验 Refresh Token")
    void testGenerateAndVerifyRefreshToken() {
        Long userId = 1002L;

        String refreshToken = jwtUtil.generateRefreshToken(userId);
        assertNotNull(refreshToken);

        Long extractedUserId = jwtUtil.getUserIdFromToken(refreshToken);
        String tokenType = jwtUtil.getTypeFromToken(refreshToken);

        assertEquals(userId, extractedUserId);
        assertEquals("refresh", tokenType);
    }
}
