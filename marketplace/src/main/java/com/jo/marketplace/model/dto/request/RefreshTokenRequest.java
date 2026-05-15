package com.jo.marketplace.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotBlank(message = "กรุณาระบุ refresh token")
    private String refreshToken;
}
