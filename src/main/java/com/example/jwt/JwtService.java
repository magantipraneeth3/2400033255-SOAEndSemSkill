package com.example.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final SecretKey key;
    private final String issuer;
    private final long expirationSeconds;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.issuer}") String issuer,
                      @Value("${app.jwt.expiration-seconds}") long expirationSeconds) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 UTF-8 bytes for HS256");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
    }

    public String generate(Map<String, Object> payload) {
        Instant now = Instant.now();
        var builder = Jwts.builder()
                .claims(payload)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)));
        return builder.signWith(key, Jwts.SIG.HS256).compact();
    }

    public Claims verify(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
