package com.jo.marketplace.model.dto.response;

import com.jo.marketplace.model.enums.ChatMessageTypeEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ShopChatMessageResponse {
    private UUID id;
    private UUID shopId;
    private UUID roomId;
    private UUID senderId;
    private String senderUsername;
    private String senderFirstName;
    private String senderLastName;
    private ChatMessageTypeEnum messageType;
    private String content;
    private LocalDateTime createdAt;
}
