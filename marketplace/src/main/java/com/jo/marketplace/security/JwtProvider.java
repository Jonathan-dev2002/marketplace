package com.jo.marketplace.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class JwtProvider {

    // ⚡ ดึงค่า Secret และเวลาหมดอายุมาจาก application.yml (เดี๋ยวเราไปเติมทีหลัง)
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private int jwtExpirationMs;

    // แปลง String Secret ให้เป็น Key เข้ารหัสระดับสูง (HMAC-SHA256)
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 🛡️ ฟังก์ชันสร้าง Token
    public String generateToken(UUID userId, String username, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(userId.toString()) // ใช้ ID เป็น Subject หลัก
                .claim("username", username) // แนบข้อมูลเพิ่มเติม (Claims)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public UUID getUserIdFromToken(String token) {
        // เรียกใช้ฟังก์ชันแกะกล่องใหญ่ของเราเอง แล้วดึงแค่ Subject (ID) ออกมา
        String subject = getClaimsFromToken(token).getSubject();
        return UUID.fromString(subject);
    }

    // 🛡️ ฟังก์ชันตรวจสอบความถูกต้องของ Token
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

    // 🛡️ ดึงข้อมูลทั้งหมดที่ฝังไว้ใน Token (Claims)
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}