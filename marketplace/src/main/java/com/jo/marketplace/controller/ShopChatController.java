package com.jo.marketplace.controller;

import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.model.dto.request.CreateShopChatRoomRequest;
import com.jo.marketplace.model.dto.request.UpdateShopChatRoomRequest;
import com.jo.marketplace.model.dto.response.PageResponse;
import com.jo.marketplace.model.dto.response.ShopChatMessageResponse;
import com.jo.marketplace.model.dto.response.ShopChatRoomResponse;
import com.jo.marketplace.security.UserPrincipal;
import com.jo.marketplace.service.interfaces.ShopChatService;
import com.jo.marketplace.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.jo.marketplace.constant.StatusCodeEnums.SHOP_CHAT_ROOM_CREATED_201;
import static com.jo.marketplace.constant.StatusCodeEnums.SHOP_CHAT_ROOM_DELETED_200;
import static com.jo.marketplace.constant.StatusCodeEnums.SHOP_CHAT_ROOM_UPDATED_200;

@RestController
@RequestMapping("/api/v1/shops/{shopId}/chat/rooms")
@RequiredArgsConstructor
public class ShopChatController {

    private final ShopChatService shopChatService;

    @GetMapping
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_CHAT_VIEW')")
    public ResponseEntity<BaseResponse<List<ShopChatRoomResponse>>> getRooms(
            @PathVariable UUID shopId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseUtil.success(shopChatService.getRooms(shopId, principal.getUserId()));
    }

    @PostMapping
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_CHAT_MANAGE')")
    public ResponseEntity<BaseResponse<ShopChatRoomResponse>> createRoom(
            @PathVariable UUID shopId,
            @Valid @RequestBody CreateShopChatRoomRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseUtil.success(SHOP_CHAT_ROOM_CREATED_201, shopChatService.createRoom(shopId, request, principal.getUserId()));
    }

    @PatchMapping("/{roomId}")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_CHAT_MANAGE')")
    public ResponseEntity<BaseResponse<ShopChatRoomResponse>> updateRoom(
            @PathVariable UUID shopId,
            @PathVariable UUID roomId,
            @Valid @RequestBody UpdateShopChatRoomRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseUtil.success(SHOP_CHAT_ROOM_UPDATED_200, shopChatService.updateRoom(shopId, roomId, request, principal.getUserId()));
    }

    @DeleteMapping("/{roomId}")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_CHAT_MANAGE')")
    public ResponseEntity<BaseResponse<Void>> deleteRoom(
            @PathVariable UUID shopId,
            @PathVariable UUID roomId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        shopChatService.deleteRoom(shopId, roomId, principal.getUserId());
        return ResponseUtil.success(SHOP_CHAT_ROOM_DELETED_200, null);
    }

    @GetMapping("/{roomId}/messages")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_CHAT_VIEW')")
    public ResponseEntity<BaseResponse<PageResponse<ShopChatMessageResponse>>> getMessages(
            @PathVariable UUID shopId,
            @PathVariable UUID roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseUtil.success(shopChatService.getMessages(shopId, roomId, page, size, principal.getUserId()));
    }
}
