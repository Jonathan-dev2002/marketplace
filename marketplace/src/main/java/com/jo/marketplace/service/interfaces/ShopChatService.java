package com.jo.marketplace.service.interfaces;

import com.jo.marketplace.model.dto.request.CreateShopChatRoomRequest;
import com.jo.marketplace.model.dto.request.SendShopChatMessageRequest;
import com.jo.marketplace.model.dto.request.UpdateShopChatRoomRequest;
import com.jo.marketplace.model.dto.response.PageResponse;
import com.jo.marketplace.model.dto.response.ShopChatMessageResponse;
import com.jo.marketplace.model.dto.response.ShopChatRoomResponse;

import java.util.List;
import java.util.UUID;

public interface ShopChatService {

    List<ShopChatRoomResponse> getRooms(UUID shopId, UUID currentUserId);

    ShopChatRoomResponse createRoom(UUID shopId, CreateShopChatRoomRequest request, UUID currentUserId);

    ShopChatRoomResponse updateRoom(UUID shopId, UUID roomId, UpdateShopChatRoomRequest request, UUID currentUserId);

    void deleteRoom(UUID shopId, UUID roomId, UUID currentUserId);

    PageResponse<ShopChatMessageResponse> getMessages(UUID shopId, UUID roomId, int page, int size, UUID currentUserId);

    ShopChatMessageResponse sendMessage(UUID shopId, UUID roomId, SendShopChatMessageRequest request, UUID currentUserId);
}
