package com.jo.marketplace.repository.interfaces;

import com.jo.marketplace.entity.ShopChatMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShopChatMessageRepository extends JpaRepository<ShopChatMessageEntity, UUID> {

    @EntityGraph(attributePaths = "sender")
    Page<ShopChatMessageEntity> findByShopIdAndRoomIdAndDeletedAtIsNullOrderByCreatedDateDesc(
            UUID shopId,
            UUID roomId,
            Pageable pageable
    );
}
