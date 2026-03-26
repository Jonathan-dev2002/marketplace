package com.jo.marketplace.service.interfaces;

import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.repository.projection.UserProfileProjection;

import java.util.UUID;

public interface MasUserService {

    // ดึงข้อมูล User ด้วย Username (ใช้ตอน Login)
    MasUserEntity getUserByUsername(String username);
    MasUserEntity getUserByUsernameOrEmail(String usernameOrEmail);
    // เช็คว่า Username หรือ Email ซ้ำไหม (ใช้ตอน Register)
    void validateDuplicateUser(String username, String email);
    MasUserEntity getUserById(java.util.UUID id);
    UserProfileProjection getUserProfileById(UUID id);
}