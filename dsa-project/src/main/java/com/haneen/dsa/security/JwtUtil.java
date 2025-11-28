package com.haneen.dsa.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration-ms}") long expirationMs) {
        // secret must be sufficiently long (>= 32 bytes). Use env var in production.
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    // Create JWT token with userId, username and role as claims
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)              // "sub" claim
                .claim("userId", userId)           // custom claim
                .claim("role", role)               // custom claim
                .setIssuedAt(now)                  // "iat"
                .setExpiration(expiry)             // "exp"
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate signature and expiration; returns parsed JWS (throws on invalid)
    public Jws<Claims> validateAndParse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
