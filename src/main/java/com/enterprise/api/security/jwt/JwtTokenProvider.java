package com.enterprise.api.security.jwt;

import com.enterprise.api.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enterprise JWT Token Provider for issuing and validating cryptographically signed tokens.
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtTokenProvider(
            @Value("${security.jwt.secret-key}") String secretKeyHex,
            @Value("${security.jwt.access-token-expiration-ms:3600000}") long accessTokenExpirationMs,
            @Value("${security.jwt.refresh-token-expiration-ms:604800000}") long refreshTokenExpirationMs) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyHex);
        if (keyBytes.length < 32) {
            // Fallback for hex format
            this.key = Keys.hmacShaKeyFor(secretKeyHex.getBytes());
        } else {
            this.key = Keys.hmacShaKeyFor(keyBytes);
        }
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String generateAccessToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();
        return generateAccessToken(userPrincipal);
    }

    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("fullName", user.getFullName());
        claims.put("roles", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .subject(user.getEmail())
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("tokenType", "REFRESH")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature or token format: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }
}
