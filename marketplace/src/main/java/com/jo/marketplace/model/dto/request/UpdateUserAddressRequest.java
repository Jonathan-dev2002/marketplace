package com.jo.marketplace.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserAddressRequest {

    @Size(max = 100, message = "ชื่อที่อยู่ต้องไม่เกิน 100 ตัวอักษร")
    private String label;

    @Size(max = 150, message = "ชื่อผู้รับต้องไม่เกิน 150 ตัวอักษร")
    private String recipientName;

    @Size(max = 20, message = "เบอร์โทรศัพท์ต้องไม่เกิน 20 ตัวอักษร")
    private String phone;

    @Size(max = 255, message = "ที่อยู่ต้องไม่เกิน 255 ตัวอักษร")
    private String addressLine1;

    @Size(max = 255, message = "ที่อยู่เพิ่มเติมต้องไม่เกิน 255 ตัวอักษร")
    private String addressLine2;

    @Size(max = 100, message = "แขวง/ตำบลต้องไม่เกิน 100 ตัวอักษร")
    private String subDistrict;

    @Size(max = 100, message = "เขต/อำเภอต้องไม่เกิน 100 ตัวอักษร")
    private String district;

    @Size(max = 100, message = "จังหวัดต้องไม่เกิน 100 ตัวอักษร")
    private String province;

    @Size(max = 20, message = "รหัสไปรษณีย์ต้องไม่เกิน 20 ตัวอักษร")
    private String postalCode;

    @Size(max = 100, message = "ประเทศต้องไม่เกิน 100 ตัวอักษร")
    private String country;

    private Boolean defaultAddress;
}
