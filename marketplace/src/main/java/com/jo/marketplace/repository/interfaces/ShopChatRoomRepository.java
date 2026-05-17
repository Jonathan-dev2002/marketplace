package com.jo.marketplace.repository.interfaces;

import com.jo.marketplace.entity.ShopChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShopChatRoomRepository extends JpaRepository<ShopChatRoomEntity, UUID> {

    List<ShopChatRoomEntity> findByShopIdAndDeletedAtIsNullOrderByCreatedDateDesc(UUID shopId);

    Optional<ShopChatRoomEntity> findByIdAndShopIdAndDeletedAtIsNull(UUID id, UUID shopId);

    boolean existsByIdAndShopIdAndDeletedAtIsNull(UUID id, UUID shopId);
}
