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
@NoArgsConstructor
public class JwtUtil {
    private final SecretKey secretKey = getSecretKey();

    public String generateToken(String userUuid) {
        long expireTime = 1000 * 60 * 60;
        return Jwts.builder()
                .subject(userUuid)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey)
                .compact();

    }

    private SecretKey getSecretKey() {
        String jwtSecret = "dGhlc2hhcmVodWJzZWNyZXRrZXk=dGhlc2hhcmVodWJzZWNyZXRrZXk=";
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserUuid(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().after(new Date());
        } catch(JwtException e) {
            return false;
        }
    }
}
