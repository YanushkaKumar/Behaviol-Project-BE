package com.todoapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getUrlDecoder().decode(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public String generateToken(String id, String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (SignatureException e) {
            System.out.println("❌ Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("❌ Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("❌ JWT token expired: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Token validation error: " + e.getMessage());
        }
        return false;
    }
}