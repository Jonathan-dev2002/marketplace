package com.jo.marketplace.security;

import com.jo.marketplace.model.enums.UserStatusEnum;
import com.jo.marketplace.repository.interfaces.MasUserRepository;
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
import java.util.UUID;

import static com.jo.marketplace.constant.AuthConstants.AUTHORIZATION_HEADER;
import static com.jo.marketplace.constant.AuthConstants.BEARER_PREFIX;

@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final MasUserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

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
        return message;
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
}
