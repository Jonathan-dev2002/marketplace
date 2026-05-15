package com.jo.marketplace.service.interfaces;

import com.jo.marketplace.model.dto.request.LoginRequest;
import com.jo.marketplace.model.dto.request.ChangePasswordRequest;
import com.jo.marketplace.model.dto.request.RefreshTokenRequest;
import com.jo.marketplace.model.dto.request.RegisterRequest;
import com.jo.marketplace.model.dto.response.AuthResponse;

public interface AuthService {

    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void logout(String bearerToken);

    AuthResponse refresh(RefreshTokenRequest request);

    void changePassword(ChangePasswordRequest request);
}
