package com.jo.marketplace.service.interfaces;

import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.model.dto.request.UpdateUserProfileRequest;
import com.jo.marketplace.model.dto.response.UserProfileResponse;
import com.jo.marketplace.repository.projection.UserProfileProjection;

import java.util.UUID;

public interface MasUserService {

    MasUserEntity getUserByUsername(String username);

    MasUserEntity getUserByUsernameOrEmail(String usernameOrEmail);

    void validateDuplicateUser(String username, String email);

    MasUserEntity getUserById(java.util.UUID id);

    UserProfileProjection getUserProfileById(UUID id);

    UserProfileResponse getMyProfile(UUID userId);

    UserProfileResponse updateMyProfile(UUID userId, UpdateUserProfileRequest request);

    void deactivateMyAccount(UUID userId);
}
