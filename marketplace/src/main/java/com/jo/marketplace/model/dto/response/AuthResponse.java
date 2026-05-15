package com.jo.marketplace.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AuthResponse {
    private UUID userId;
    private String accessToken;
    private String refreshToken;
    private String username;
    private String tokenType;
    private Long expiresIn;
    private List<String> roles;
    private List<String> authorities;
}
