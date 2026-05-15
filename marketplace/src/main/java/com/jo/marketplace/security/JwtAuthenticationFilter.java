package com.jo.marketplace.security;

import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.model.enums.UserStatusEnum;
import com.jo.marketplace.repository.interfaces.MasUserRepository;
import com.jo.marketplace.service.interfaces.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.jo.marketplace.constant.AuthConstants.AUTHORIZATION_HEADER;
import static com.jo.marketplace.constant.AuthConstants.BEARER_PREFIX;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final MasUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)
                    && jwtProvider.validateToken(jwt)
                    && !jwtProvider.isRefreshToken(jwt)
                    && !tokenBlacklistService.isBlacklisted(jwt)) {
                io.jsonwebtoken.Claims claims = jwtProvider.getClaimsFromToken(jwt);
                UUID userId = UUID.fromString(claims.getSubject());
                String username = claims.get("username", String.class);
                List<String> roles = claims.get("roles", List.class);
                List<String> authorities = claims.get("authorities", List.class);

                if (!isActiveUser(userId)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                UserPrincipal principal = UserPrincipal.builder()
                        .userId(userId)
                        .username(username)
                        .roles(roles)
                        .authorities(authorities)
                        .build();

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principal, null, SecurityAuthorityUtil.toGrantedAuthorities(authorities)
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private boolean isActiveUser(UUID userId) {
        return userRepository.findById(userId)
                .map(MasUserEntity::getStatus)
                .filter(UserStatusEnum.ACTIVE::equals)
                .isPresent();
    }
}
