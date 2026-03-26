package com.jo.marketplace.controller;

import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.constant.StatusCodeEnums;
import com.jo.marketplace.model.dto.request.LoginRequest;
import com.jo.marketplace.model.dto.request.RegisterRequest;
import com.jo.marketplace.model.dto.response.AuthResponse;
import com.jo.marketplace.service.AuthService;
import com.jo.marketplace.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.jo.marketplace.constant.StatusCodeEnums.CREATED_201;

@Validated
@RestController
@RequestMapping("/api/auth") // ⚡ URL นี้ถูกปลดล็อกไว้ใน SecurityConfig แล้ว
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
}