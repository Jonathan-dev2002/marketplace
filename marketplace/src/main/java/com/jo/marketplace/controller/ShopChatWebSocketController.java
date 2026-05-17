package com.jo.marketplace.controller;

import com.jo.marketplace.model.dto.request.SendShopChatMessageRequest;
import com.jo.marketplace.model.dto.response.ShopChatMessageResponse;
import com.jo.marketplace.security.UserPrincipal;
import com.jo.marketplace.service.interfaces.ShopChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ShopChatWebSocketController {

    private final ShopChatService shopChatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/shops/{shopId}/chat/rooms/{roomId}/messages")
    public void sendMessage(
            @DestinationVariable UUID shopId,
            @DestinationVariable UUID roomId,
            @Valid @Payload SendShopChatMessageRequest request,
            Principal principal
    ) {
        UserPrincipal currentUser = getCurrentUser(principal);
        ShopChatMessageResponse response = shopChatService.sendMessage(shopId, roomId, request, currentUser.getUserId());
        messagingTemplate.convertAndSend("/topic/shops/" + shopId + "/chat/rooms/" + roomId, response);
    }

    private UserPrincipal getCurrentUser(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken authentication
                && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal;
        }

        throw new IllegalArgumentException("WebSocket authentication is required");
    }
}
