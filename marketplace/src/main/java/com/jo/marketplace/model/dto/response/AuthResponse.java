package com.jo.marketplace.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String username;
    private String tokenType;
}
