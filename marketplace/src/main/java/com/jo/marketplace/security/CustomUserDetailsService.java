package com.jo.marketplace.security;

import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.repository.interfaces.MasUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MasUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // 1. ค้นหา User จาก Database
        MasUserEntity userEntity = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));

        // 2. แปลง MasUserEntity ของเรา ให้กลายเป็น UserDetails ของ Spring Security
        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword()) // รหัสผ่านที่เข้ารหัสแล้วใน DB
                .authorities(Collections.emptyList()) // ตอนนี้ยังไม่มี Role ให้ใส่ List ว่างไปก่อน
                .build();
    }
}