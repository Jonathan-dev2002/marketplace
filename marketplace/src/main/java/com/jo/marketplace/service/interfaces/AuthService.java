package com.jo.marketplace.service.interfaces;

import com.jo.marketplace.model.dto.request.LoginRequest;
import com.jo.marketplace.model.dto.request.RegisterRequest;
import com.jo.marketplace.model.dto.response.AuthResponse;

public interface AuthService {

    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
