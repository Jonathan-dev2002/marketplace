package com.jo.marketplace.controller;

import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.model.dto.request.ChangePasswordRequest;
import com.jo.marketplace.model.dto.request.LoginRequest;
import com.jo.marketplace.model.dto.request.RefreshTokenRequest;
import com.jo.marketplace.model.dto.request.RegisterRequest;
import com.jo.marketplace.model.dto.response.AuthResponse;
import com.jo.marketplace.service.interfaces.AuthService;
import com.jo.marketplace.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.jo.marketplace.constant.StatusCodeEnums.CREATED_201;
import static com.jo.marketplace.constant.StatusCodeEnums.LOGOUT_SUCCESS_200;
import static com.jo.marketplace.constant.StatusCodeEnums.PASSWORD_CHANGED_200;

@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseUtil.success(CREATED_201, null);
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseUtil.success(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        authService.logout(bearerToken);
        return ResponseUtil.success(LOGOUT_SUCCESS_200, null);
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return ResponseUtil.success(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<BaseResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseUtil.success(PASSWORD_CHANGED_200, null);
    }
}
