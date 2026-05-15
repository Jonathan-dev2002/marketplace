package com.jo.marketplace.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {

    @Size(max = 100, message = "ชื่อต้องไม่เกิน 100 ตัวอักษร")
    private String firstName;

    @Size(max = 100, message = "นามสกุลต้องไม่เกิน 100 ตัวอักษร")
    private String lastName;

    @Size(max = 20, message = "เบอร์โทรศัพท์ต้องไม่เกิน 20 ตัวอักษร")
    private String phone;
}
