package com.jo.marketplace.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 🛡️ เปิดใช้งาน @PreAuthorize ที่คุณใช้ในโค้ดตัวอย่าง MasCountryController
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final AccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ⚡ Performance: ปิด CSRF เพราะเราใช้ JWT (Stateless) ไม่ได้ใช้ Cookie แบบเว็บยุคเก่า
                .csrf(AbstractHttpConfigurer::disable)
                // ⚡ เพิ่มบล็อกนี้ เพื่อบอกให้ Spring Security ใช้ JSON Response ของเรา
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // คนไม่มีตั๋ว
                        .accessDeniedHandler(jwtAccessDeniedHandler)           // คนยศไม่ถึง
                )
                // ⚡ Performance: ปิดการสร้าง Session ใน Memory ของ Server (ช่วยให้ระบบ Scale กรองรับคนหลักแสนได้)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 🛡️ ตั้งค่าสิทธิ์การเข้าถึง API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // ปล่อยผ่าน API Login/Register
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // ปล่อยผ่าน Swagger (คู่มือ API)
                        .anyRequest().authenticated() // API อื่นๆ ทั้งหมดต้องมี JWT Token
                );

        // เอา Filter ของเราไปดักไว้หน้าสุด ก่อนที่ Spring จะเช็ค Username/Password ปกติ
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🛡️ ตัวเข้ารหัส Password (ใช้ BCrypt มาตรฐานสากล)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🛡️ ตัวจัดการการ Login ของ Spring Security
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}