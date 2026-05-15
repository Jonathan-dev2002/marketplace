package com.jo.marketplace.controller;

import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.model.dto.request.UpdateUserProfileRequest;
import com.jo.marketplace.model.dto.response.UserProfileResponse;
import com.jo.marketplace.service.interfaces.MasUserService;
import com.jo.marketplace.utils.ResponseUtil;
import com.jo.marketplace.utils.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.jo.marketplace.constant.StatusCodeEnums.USER_DEACTIVATED_200;
import static com.jo.marketplace.constant.StatusCodeEnums.USER_PROFILE_UPDATED_200;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class MasUserController {

    private final MasUserService masUserService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<UserProfileResponse>> getMyProfile() {
        return ResponseUtil.success(masUserService.getMyProfile(UserUtil.getCurrentUser().getUserId()));
    }

    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<UserProfileResponse>> updateMyProfile(
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        UserProfileResponse response = masUserService.updateMyProfile(UserUtil.getCurrentUser().getUserId(), request);
        return ResponseUtil.success(USER_PROFILE_UPDATED_200, response);
    }

    @PatchMapping("/me/deactivate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Void>> deactivateMyAccount() {
        masUserService.deactivateMyAccount(UserUtil.getCurrentUser().getUserId());
        return ResponseUtil.success(USER_DEACTIVATED_200, null);
    }
}
