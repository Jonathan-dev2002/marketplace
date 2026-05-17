package com.jo.marketplace.service;

import com.jo.marketplace.entity.MasShopEntity;
import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.entity.ShopChatMessageEntity;
import com.jo.marketplace.entity.ShopChatRoomEntity;
import com.jo.marketplace.exception.BusinessException;
import com.jo.marketplace.model.dto.request.CreateShopChatRoomRequest;
import com.jo.marketplace.model.dto.request.SendShopChatMessageRequest;
import com.jo.marketplace.model.dto.request.UpdateShopChatRoomRequest;
import com.jo.marketplace.model.dto.response.PageResponse;
import com.jo.marketplace.model.dto.response.ShopChatMessageResponse;
import com.jo.marketplace.model.dto.response.ShopChatRoomResponse;
import com.jo.marketplace.model.enums.ChatMessageTypeEnum;
import com.jo.marketplace.repository.interfaces.MasShopRepository;
import com.jo.marketplace.repository.interfaces.MasUserRepository;
import com.jo.marketplace.repository.interfaces.MasUserShopRoleRepository;
import com.jo.marketplace.repository.interfaces.ShopChatMessageRepository;
import com.jo.marketplace.repository.interfaces.ShopChatRoomRepository;
import com.jo.marketplace.repository.projection.UserProfileProjection;
import com.jo.marketplace.service.interfaces.ShopChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.jo.marketplace.constant.AppConstants.PLATFORM_SHOP_ID;
import static com.jo.marketplace.constant.PermissionConstants.SHOP_CHAT_MANAGE;
import static com.jo.marketplace.constant.PermissionConstants.SHOP_CHAT_SEND;
import static com.jo.marketplace.constant.PermissionConstants.SHOP_CHAT_VIEW;
import static com.jo.marketplace.constant.StatusCodeEnums.SHOP_ACCESS_DENIED_403;
import static com.jo.marketplace.constant.StatusCodeEnums.SHOP_CHAT_MESSAGE_INVALID_400;
import static com.jo.marketplace.constant.StatusCodeEnums.SHOP_CHAT_ROOM_NOT_FOUND_404;
import static com.jo.marketplace.constant.StatusCodeEnums.SHOP_NOT_FOUND_404;

@Service
@RequiredArgsConstructor
public class ShopChatServiceImpl implements ShopChatService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ShopChatRoomRepository roomRepository;
    private final ShopChatMessageRepository messageRepository;
    private final MasShopRepository shopRepository;
    private final MasUserRepository userRepository;
    private final MasUserShopRoleRepository userShopRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ShopChatRoomResponse> getRooms(UUID shopId, UUID currentUserId) {
        validateShopPermission(shopId, currentUserId, SHOP_CHAT_VIEW);
        return roomRepository.findByShopIdAndDeletedAtIsNullOrderByCreatedDateDesc(shopId).stream()
                .map(this::toRoomResponse)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopChatRoomResponse createRoom(UUID shopId, CreateShopChatRoomRequest request, UUID currentUserId) {
        validateShopPermission(shopId, currentUserId, SHOP_CHAT_MANAGE);

        ShopChatRoomEntity room = new ShopChatRoomEntity();
        room.setShopId(shopId);
        room.setName(request.getName().trim());
        room.setActive(true);

        return toRoomResponse(roomRepository.save(room));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopChatRoomResponse updateRoom(UUID shopId, UUID roomId, UpdateShopChatRoomRequest request, UUID currentUserId) {
        validateShopPermission(shopId, currentUserId, SHOP_CHAT_MANAGE);
        ShopChatRoomEntity room = getRoomOrThrow(shopId, roomId);

        if (StringUtils.hasText(request.getName())) {
            room.setName(request.getName().trim());
        }

        if (request.getActive() != null) {
            room.setActive(request.getActive());
        }

        return toRoomResponse(roomRepository.save(room));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoom(UUID shopId, UUID roomId, UUID currentUserId) {
        validateShopPermission(shopId, currentUserId, SHOP_CHAT_MANAGE);
        ShopChatRoomEntity room = getRoomOrThrow(shopId, roomId);
        room.setActive(false);
        room.setDeletedAt(LocalDateTime.now());
        roomRepository.save(room);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ShopChatMessageResponse> getMessages(UUID shopId, UUID roomId, int page, int size, UUID currentUserId) {
        validateShopPermission(shopId, currentUserId, SHOP_CHAT_VIEW);
        getRoomOrThrow(shopId, roomId);

        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize);
        Page<ShopChatMessageEntity> result = messageRepository.findByShopIdAndRoomIdAndDeletedAtIsNullOrderByCreatedDateDesc(
                shopId,
                roomId,
                pageable
        );

        return PageResponse.<ShopChatMessageResponse>builder()
                .items(result.getContent().stream().map(this::toMessageResponse).toList())
                .totalItems(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .page(normalizedPage)
                .size(normalizedSize)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopChatMessageResponse sendMessage(UUID shopId, UUID roomId, SendShopChatMessageRequest request, UUID currentUserId) {
        validateShopPermission(shopId, currentUserId, SHOP_CHAT_SEND);
        ShopChatRoomEntity room = getRoomOrThrow(shopId, roomId);
        if (!Boolean.TRUE.equals(room.getActive())) {
            throw new BusinessException(SHOP_CHAT_ROOM_NOT_FOUND_404, SHOP_CHAT_ROOM_NOT_FOUND_404.getDescriptionTH());
        }

        if (!StringUtils.hasText(request.getContent())) {
            throw new BusinessException(SHOP_CHAT_MESSAGE_INVALID_400, SHOP_CHAT_MESSAGE_INVALID_400.getDescriptionTH());
        }

        ChatMessageTypeEnum messageType = request.getMessageType() == null ? ChatMessageTypeEnum.TEXT : request.getMessageType();
        if (ChatMessageTypeEnum.SYSTEM.equals(messageType)) {
            throw new BusinessException(SHOP_CHAT_MESSAGE_INVALID_400, SHOP_CHAT_MESSAGE_INVALID_400.getDescriptionTH());
        }

        ShopChatMessageEntity message = new ShopChatMessageEntity();
        message.setShopId(shopId);
        message.setRoomId(roomId);
        message.setSenderId(currentUserId);
        message.setMessageType(messageType);
        message.setContent(request.getContent().trim());

        ShopChatMessageEntity savedMessage = messageRepository.save(message);
        UserProfileProjection sender = userRepository.findProfileById(currentUserId).orElse(null);
        return toMessageResponse(savedMessage, sender);
    }

    private void validateShopPermission(UUID shopId, UUID userId, String permissionSlug) {
        MasShopEntity shop = getShopOrThrow(shopId);
        if (shop.getOwnerId().equals(userId)
                || userShopRoleRepository.hasPermission(userId, shopId, permissionSlug)
                || userShopRoleRepository.hasPermission(userId, PLATFORM_SHOP_ID, permissionSlug)) {
            return;
        }

        throw new BusinessException(SHOP_ACCESS_DENIED_403, SHOP_ACCESS_DENIED_403.getDescriptionTH());
    }

    private MasShopEntity getShopOrThrow(UUID shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND_404, SHOP_NOT_FOUND_404.getDescriptionTH()));
    }

    private ShopChatRoomEntity getRoomOrThrow(UUID shopId, UUID roomId) {
        return roomRepository.findByIdAndShopIdAndDeletedAtIsNull(roomId, shopId)
                .orElseThrow(() -> new BusinessException(SHOP_CHAT_ROOM_NOT_FOUND_404, SHOP_CHAT_ROOM_NOT_FOUND_404.getDescriptionTH()));
    }

    private ShopChatRoomResponse toRoomResponse(ShopChatRoomEntity room) {
        return ShopChatRoomResponse.builder()
                .id(room.getId())
                .shopId(room.getShopId())
                .name(room.getName())
                .active(room.getActive())
                .createdAt(room.getCreatedDate())
                .updatedAt(room.getUpdatedDate())
                .build();
    }

    private ShopChatMessageResponse toMessageResponse(ShopChatMessageEntity message) {
        MasUserEntity sender = message.getSender();
        return ShopChatMessageResponse.builder()
                .id(message.getId())
                .shopId(message.getShopId())
                .roomId(message.getRoomId())
                .senderId(message.getSenderId())
                .senderUsername(sender == null ? null : sender.getUsername())
                .senderFirstName(sender == null ? null : sender.getFirstName())
                .senderLastName(sender == null ? null : sender.getLastName())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .createdAt(message.getCreatedDate())
                .build();
    }

    private ShopChatMessageResponse toMessageResponse(ShopChatMessageEntity message, UserProfileProjection sender) {
        return ShopChatMessageResponse.builder()
                .id(message.getId())
                .shopId(message.getShopId())
                .roomId(message.getRoomId())
                .senderId(message.getSenderId())
                .senderUsername(sender == null ? null : sender.getUsername())
                .senderFirstName(sender == null ? null : sender.getFirstName())
                .senderLastName(sender == null ? null : sender.getLastName())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .createdAt(message.getCreatedDate())
                .build();
    }
}
