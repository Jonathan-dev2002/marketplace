package com.jo.marketplace.model.dto.request;

import com.jo.marketplace.model.enums.ChatMessageTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendShopChatMessageRequest {

    private ChatMessageTypeEnum messageType = ChatMessageTypeEnum.TEXT;

    @NotBlank(message = "กรุณาระบุข้อความ")
    @Size(max = 2000, message = "ข้อความต้องไม่เกิน 2000 ตัวอักษร")
    private String content;
}
