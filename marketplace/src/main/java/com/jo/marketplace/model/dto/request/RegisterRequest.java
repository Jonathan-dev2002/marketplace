package com.jo.marketplace.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "กรุณาระบุชื่อผู้ใช้งาน (Username)")
    @Size(min = 4, max = 50, message = "ชื่อผู้ใช้งานต้องมีความยาว 4-50 ตัวอักษร")
    private String username;

    @NotBlank(message = "กรุณาระบุอีเมล (Email)")
    @Email(message = "รูปแบบอีเมลไม่ถูกต้อง")
    private String email;

    @NotBlank(message = "กรุณาระบุรหัสผ่าน (Password)")
    @Size(min = 6, message = "รหัสผ่านต้องมีความยาวอย่างน้อย 6 ตัวอักษร")
    private String password;

    private String firstName;
    private String lastName;
    private String phone;
}