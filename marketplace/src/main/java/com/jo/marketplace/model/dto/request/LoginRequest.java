package com.jo.marketplace.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "กรุณาระบุชื่อผู้ใช้งานหรืออีเมล")
    private String usernameOrEmail;

    @NotBlank(message = "กรุณาระบุรหัสผ่าน")
    private String password;
}