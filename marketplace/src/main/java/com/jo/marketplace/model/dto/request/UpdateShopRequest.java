package com.jo.marketplace.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateShopRequest {

    @Size(max = 100, message = "ชื่อร้านค้าต้องมีความยาวไม่เกิน 100 ตัวอักษร")
    private String name;

    private String description;

    private String logoUrl;
}
