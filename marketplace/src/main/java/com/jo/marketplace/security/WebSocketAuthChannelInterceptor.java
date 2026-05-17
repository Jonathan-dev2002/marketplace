package com.jo.marketplace.security;

import com.jo.marketplace.entity.MasShopEntity;
import com.jo.marketplace.model.enums.UserStatusEnum;
import com.jo.marketplace.repository.interfaces.MasShopRepository;
import com.jo.marketplace.repository.interfaces.MasUserRepository;
import com.jo.marketplace.repository.interfaces.MasUserShopRoleRepository;
import com.jo.marketplace.repository.interfaces.ShopChatRoomRepository;
import com.jo.marketplace.service.interfaces.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jo.marketplace.constant.AppConstants.PLATFORM_SHOP_ID;
import static com.jo.marketplace.constant.AuthConstants.AUTHORIZATION_HEADER;
import static com.jo.marketplace.constant.AuthConstants.BEARER_PREFIX;
import static com.jo.marketplace.constant.PermissionConstants.SHOP_CHAT_VIEW;

@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final MasUserRepository userRepository;
    private final MasShopRepository shopRepository;
    private final MasUserShopRoleRepository userShopRoleRepository;
    private final ShopChatRoomRepository shopChatRoomRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticateConnection(accessor);
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            authorizeSubscription(accessor);
        }

        return message;
    }

    private void authenticateConnection(StompHeaderAccessor accessor) {
        String token = getToken(accessor);
        if (!StringUtils.hasText(token)
                || !jwtProvider.validateToken(token)
                || jwtProvider.isRefreshToken(token)
                || tokenBlacklistService.isBlacklisted(token)) {
            throw new IllegalArgumentException("Invalid WebSocket authentication token");
        }

        io.jsonwebtoken.Claims claims = jwtProvider.getClaimsFromToken(token);
        UUID userId = UUID.fromString(claims.getSubject());
        if (!userRepository.existsByIdAndStatus(userId, UserStatusEnum.ACTIVE)) {
            throw new IllegalArgumentException("User account is not active");
        }

        List<String> authorities = getStringListClaim(claims, "authorities");
        UserPrincipal principal = UserPrincipal.builder()
                .userId(userId)
                .username(claims.get("username", String.class))
                .roles(getStringListClaim(claims, "roles"))
                .authorities(authorities)
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                SecurityAuthorityUtil.toGrantedAuthorities(authorities)
        );
        accessor.setUser(authentication);
    }

    private String getToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(bearerToken)) {
            bearerToken = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER.toLowerCase());
        }

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private List<String> getStringListClaim(io.jsonwebtoken.Claims claims, String claimName) {
        Object claimValue = claims.get(claimName);
        if (!(claimValue instanceof List<?> values)) {
            return List.of();
        }

        return values.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .toList();
    }

    private void authorizeSubscription(StompHeaderAccessor accessor) {
        Optional<ShopChatSubscription> subscription = parseShopChatSubscription(accessor.getDestination());
        if (subscription.isEmpty()) {
            return;
        }

        UserPrincipal principal = getAuthenticatedPrincipal(accessor);
        UUID shopId = subscription.get().shopId();
        UUID roomId = subscription.get().roomId();
        if (!shopChatRoomRepository.existsByIdAndShopIdAndDeletedAtIsNull(roomId, shopId)) {
            throw new IllegalArgumentException("Shop chat room not found");
        }

        if (!hasShopChatViewPermission(shopId, principal.getUserId())) {
            throw new IllegalArgumentException("Access denied to shop chat topic");
        }
    }

    private Optional<ShopChatSubscription> parseShopChatSubscription(String destination) {
        if (!StringUtils.hasText(destination)) {
            return Optional.empty();
        }

        String[] parts = destination.split("/");
        if (parts.length != 7
                || !"topic".equals(parts[1])
                || !"shops".equals(parts[2])
                || !"chat".equals(parts[4])
                || !"rooms".equals(parts[5])) {
            if (destination.startsWith("/topic/shops/")) {
                throw new IllegalArgumentException("Invalid shop chat subscription destination");
            }
            return Optional.empty();
        }

        try {
            return Optional.of(new ShopChatSubscription(
                    UUID.fromString(parts[3]),
                    UUID.fromString(parts[6])
            ));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid shop chat subscription destination");
        }
    }

    private UserPrincipal getAuthenticatedPrincipal(StompHeaderAccessor accessor) {
        if (accessor.getUser() instanceof UsernamePasswordAuthenticationToken authentication
                && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal;
        }

        throw new IllegalArgumentException("WebSocket authentication is required");
    }

    private boolean hasShopChatViewPermission(UUID shopId, UUID userId) {
        boolean isOwner = shopRepository.findById(shopId)
                .map(MasShopEntity::getOwnerId)
                .filter(userId::equals)
                .isPresent();

        return isOwner
                || userShopRoleRepository.hasPermission(userId, shopId, SHOP_CHAT_VIEW)
                || userShopRoleRepository.hasPermission(userId, PLATFORM_SHOP_ID, SHOP_CHAT_VIEW);
    }

    private record ShopChatSubscription(UUID shopId, UUID roomId) {
    }
}
