package com.jo.marketplace.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.jo.marketplace.constant.AuthConstants.TOKEN_USAGE_ACCESS;
import static com.jo.marketplace.constant.AuthConstants.TOKEN_USAGE_REFRESH;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long jwtRefreshExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UUID userId, String username, List<String> roles, List<String> authorities) {
        return generateToken(userId, username, roles, authorities, jwtExpirationMs, TOKEN_USAGE_ACCESS);
    }

    public String generateRefreshToken(UUID userId, String username, List<String> roles, List<String> authorities) {
        return generateToken(userId, username, roles, authorities, jwtRefreshExpirationMs, TOKEN_USAGE_REFRESH);
    }

    private String generateToken(UUID userId, String username, List<String> roles, List<String> authorities,
                                 long expirationMs, String tokenUsage) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .claim("username", username)
                .claim("roles", roles)
                .claim("authorities", authorities)
                .claim("tokenUsage", tokenUsage)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public UUID getUserIdFromToken(String token) {
        String subject = getClaimsFromToken(token).getSubject();
        return UUID.fromString(subject);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getTokenId(String token) {
        return getClaimsFromToken(token).getId();
    }

    public Duration getRemainingTtl(String token) {
        Date expiration = getClaimsFromToken(token).getExpiration();
        return Duration.between(Instant.now(), expiration.toInstant());
    }

    public long getAccessTokenExpirationMs() {
        return jwtExpirationMs;
    }

    public boolean isRefreshToken(String token) {
        return TOKEN_USAGE_REFRESH.equals(getClaimsFromToken(token).get("tokenUsage", String.class));
    }

}
