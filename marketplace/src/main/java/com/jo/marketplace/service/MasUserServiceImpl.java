package com.jo.marketplace.service;

import com.jo.marketplace.constant.StatusCodeEnums;
import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.exception.BusinessException;
import com.jo.marketplace.repository.interfaces.MasUserRepository;
import com.jo.marketplace.repository.projection.UserProfileProjection;
import com.jo.marketplace.service.interfaces.MasUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor // ⚡ Senior Practice: ใช้ Lombok สร้าง Constructor Injection ให้โดยอัตโนมัติ (ปลอดภัยกว่า @Autowired)
public class MasUserServiceImpl implements MasUserService {

    private final MasUserRepository userRepository;

    @Override
    @Transactional(readOnly = true) // ⚡ Performance: บอก DB ว่าแค่อ่านข้อมูล ไม่ต้องสร้าง Transaction Lock
    public MasUserEntity getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);

        // ใช้ Optional ร่วมกับ Exception กลางของเรา
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(StatusCodeEnums.NOT_FOUND_404, "ไม่พบข้อมูลผู้ใช้งานในระบบ"));
    }

    @Override
    @Transactional(readOnly = true)
    public MasUserEntity getUserByUsernameOrEmail(String usernameOrEmail) {
        log.info("Fetching user by username or email: {}", usernameOrEmail);
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new BusinessException(StatusCodeEnums.NOT_FOUND_404, "ไม่พบข้อมูลผู้ใช้งานในระบบ"));
    }

    @Override
    @Transactional(readOnly = true)
    public void validateDuplicateUser(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            log.warn("Registration failed: Username {} is already taken", username);
            throw new BusinessException(StatusCodeEnums.DUPLICATE_409, "ชื่อผู้ใช้งานนี้ถูกใช้ไปแล้ว");
        }

        if (userRepository.existsByEmail(email)) {
            log.warn("Registration failed: Email {} is already taken", email);
            throw new BusinessException(StatusCodeEnums.DUPLICATE_409, "อีเมลนี้ถูกใช้ไปแล้ว");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MasUserEntity getUserById(java.util.UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(StatusCodeEnums.NOT_FOUND_404, "ไม่พบข้อมูลผู้ใช้งานในระบบ"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileProjection getUserProfileById(java.util.UUID id) {
        return userRepository.findProfileById(id)
                .orElseThrow(() -> new BusinessException(StatusCodeEnums.NOT_FOUND_404, "ไม่พบข้อมูลผู้ใช้งานในระบบ"));
    }
}