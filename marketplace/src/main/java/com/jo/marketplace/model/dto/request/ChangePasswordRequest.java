package com.jo.marketplace.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "กรุณาระบุรหัสผ่านปัจจุบัน")
    private String currentPassword;

    @NotBlank(message = "กรุณาระบุรหัสผ่านใหม่")
    @Size(min = 8, message = "รหัสผ่านใหม่ต้องมีความยาวอย่างน้อย 8 ตัวอักษร")
    private String newPassword;
}
