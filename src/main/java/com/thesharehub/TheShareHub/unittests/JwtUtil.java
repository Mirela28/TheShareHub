package com.thesharehub.TheShareHub.unittests;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import io.jsonwebtoken.*;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private final String jwtSecret;

    public JwtUtil(@Value("${JWT_SECRETKEY}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Long userId) {
        long expireTime = 1000L * 60L * 60L;

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSecretKey())
                .compact();
    }

    public Long extractUserId(String token) {
        Claims claims = extractClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
