package com.jo.marketplace.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateShopSlugRequest {

    @NotBlank(message = "กรุณาระบุ slug ร้านค้า")
    @Size(max = 150, message = "slug ร้านค้าต้องมีความยาวไม่เกิน 150 ตัวอักษร")
    private String slug;
}
