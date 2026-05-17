package com.jo.marketplace.entity;

import com.jo.marketplace.model.enums.ChatMessageTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "shop_chat_messages")
@SQLRestriction("deleted_at IS NULL")
public class ShopChatMessageEntity extends BaseEntity {

    @Column(name = "shop_id", nullable = false)
    private UUID shopId;

    @Column(name = "room_id", nullable = false)
    private UUID roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private ShopChatRoomEntity room;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", insertable = false, updatable = false)
    private MasUserEntity sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 20)
    private ChatMessageTypeEnum messageType = ChatMessageTypeEnum.TEXT;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
