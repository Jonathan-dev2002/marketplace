package com.jo.marketplace.controller;

import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.model.dto.response.UserProfileResponse;
import com.jo.marketplace.repository.projection.UserProfileProjection;
import com.jo.marketplace.service.interfaces.MasUserService;
import com.jo.marketplace.utils.ResponseUtil;
import com.jo.marketplace.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MasUserController {

    private final MasUserService masUserService;

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserProfileResponse>> getMyProfile() {
        UserProfileProjection userProfile = masUserService.getUserProfileById(UserUtil.getCurrentUser().getUserId());

        UserProfileResponse response = UserProfileResponse.builder()
                .id(userProfile.getId())
                .username(userProfile.getUsername())
                .email(userProfile.getEmail())
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .phone(userProfile.getPhone())
                .status(userProfile.getStatus().name())
                .build();

        return ResponseUtil.success(response);
    }
}
