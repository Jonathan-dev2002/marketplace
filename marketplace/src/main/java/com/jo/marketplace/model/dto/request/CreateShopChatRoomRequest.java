package com.jo.marketplace.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateShopChatRoomRequest {

    @NotBlank(message = "กรุณาระบุชื่อห้องแชท")
    @Size(max = 100, message = "ชื่อห้องแชทต้องไม่เกิน 100 ตัวอักษร")
    private String name;
}
