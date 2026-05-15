package com.jo.marketplace.service;

import com.jo.marketplace.constant.StatusCodeEnums;
import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.exception.BusinessException;
import com.jo.marketplace.model.dto.request.UpdateUserProfileRequest;
import com.jo.marketplace.model.dto.response.UserProfileResponse;
import com.jo.marketplace.model.enums.UserStatusEnum;
import com.jo.marketplace.repository.interfaces.MasUserRepository;
import com.jo.marketplace.repository.projection.UserProfileProjection;
import com.jo.marketplace.service.interfaces.MasUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MasUserServiceImpl implements MasUserService {

    private final MasUserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public MasUserEntity getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);

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
    public MasUserEntity getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(StatusCodeEnums.NOT_FOUND_404, "ไม่พบข้อมูลผู้ใช้งานในระบบ"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileProjection getUserProfileById(UUID id) {
        return userRepository.findProfileById(id)
                .orElseThrow(() -> new BusinessException(StatusCodeEnums.NOT_FOUND_404, "ไม่พบข้อมูลผู้ใช้งานในระบบ"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(UUID userId) {
        return toProfileResponse(getUserProfileById(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserProfileResponse updateMyProfile(UUID userId, UpdateUserProfileRequest request) {
        MasUserEntity user = getUserById(userId);

        if (request.getFirstName() != null) {
            user.setFirstName(normalizeNullable(request.getFirstName()));
        }

        if (request.getLastName() != null) {
            user.setLastName(normalizeNullable(request.getLastName()));
        }

        if (request.getPhone() != null) {
            user.setPhone(normalizeNullable(request.getPhone()));
        }

        MasUserEntity savedUser = userRepository.save(user);
        return UserProfileResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .phone(savedUser.getPhone())
                .status(savedUser.getStatus().name())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deactivateMyAccount(UUID userId) {
        MasUserEntity user = getUserById(userId);
        user.setStatus(UserStatusEnum.DEACTIVATED);
        userRepository.save(user);
    }

    private UserProfileResponse toProfileResponse(UserProfileProjection userProfile) {
        return UserProfileResponse.builder()
                .id(userProfile.getId())
                .username(userProfile.getUsername())
                .email(userProfile.getEmail())
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .phone(userProfile.getPhone())
                .status(userProfile.getStatus().name())
                .build();
    }

    private String normalizeNullable(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
