package com.thesharehub.TheShareHub.utils;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Date;
import io.jsonwebtoken.*;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    private final SecretKey secretKey = getSecretKey();

    public String generateToken(Long userId) {
        long expireTime = 1000 * 60 * 60;
        return Jwts.builder()
                .subject(String.valueOf(userId)) //payload
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey) //jwt signature
                .compact(); //as string header.payload.signature

    }

    private SecretKey getSecretKey() {
        String jwtSecret = "dGhlc2hhcmVodWJzZWNyZXRrZXk=dGhlc2hhcmVodWJzZWNyZXRrZXk=";
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Long extractUserId(String token) {
        Claims claims = extractClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().after(new Date());
        } catch(JwtException e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) //verifies jwt signature
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
