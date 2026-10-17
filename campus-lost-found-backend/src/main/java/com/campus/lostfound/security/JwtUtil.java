package com.campus.lostfound.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT 工具类
 * 负责 Token 的生成与校验
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiry}")
    private long accessTokenExpiry;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    @Value("${jwt.issuer}")
    private String issuer;

    /**
     * 生成 Access Token
     *
     * @param userId 用户ID
     * @param role   用户角色
     * @return JWT Token 字符串
     */
    public String generateAccessToken(Long userId, String role) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(userId))
                .withClaim("role", role)
                .withClaim("type", "access")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpiry * 1000))
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 生成 Refresh Token
     *
     * @param userId 用户ID
     * @return Refresh Token 字符串
     */
    public String generateRefreshToken(Long userId) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(userId))
                .withClaim("type", "refresh")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpiry * 1000))
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 校验并解析 Token
     *
     * @param token JWT Token
     * @return 解码后的 JWT 对象
     * @throws JWTVerificationException Token 无效或已过期
     */
    public DecodedJWT verifyToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(issuer)
                .build();
        return verifier.verify(token);
    }

    /**
     * 从 Token 中获取用户 ID
     */
    public Long getUserIdFromToken(String token) {
        DecodedJWT jwt = verifyToken(token);
        return Long.parseLong(jwt.getSubject());
    }

    /**
     * 从 Token 中获取用户角色
     */
    public String getRoleFromToken(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt.getClaim("role").asString();
    }

    /**
     * 从 Token 中获取类型（access / refresh）
     */
    public String getTypeFromToken(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt.getClaim("type").asString();
    }

    /**
     * 获取 Access Token 有效期（秒）
     */
    public long getAccessTokenExpiry() {
        return accessTokenExpiry;
    }
}
