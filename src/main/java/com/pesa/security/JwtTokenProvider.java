package com.pesa.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public Long getUserIdFromToken(String token) {
        Object raw = getAllClaims(token).get("userId");
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw instanceof String value && !value.isBlank()) {
            return Long.valueOf(value);
        }
        return null;
    }

    public String getPhoneNumberFromToken(String token) {
        return getAllClaims(token).get("phoneNumber", String.class);
    }

    public Long getAdminIdFromToken(String token) {
        Object raw = getAllClaims(token).get("adminId");
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw instanceof String value && !value.isBlank()) {
            return Long.valueOf(value);
        }
        return null;
    }

    public String getSubjectFromToken(String token) {
        return getAllClaims(token).getSubject();
    }

    public boolean isAdminToken(String token) {
        try {
            Claims claims = getAllClaims(token);
            String role = claims.get("role", String.class);
            if ("ADMIN".equalsIgnoreCase(role)) {
                return true;
            }
            Object rolesObj = claims.get("roles");
            if (rolesObj instanceof Collection<?> roles) {
                return roles.stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(String.valueOf(r)));
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private Claims getAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
