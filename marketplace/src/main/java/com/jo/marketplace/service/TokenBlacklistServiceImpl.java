package com.jo.marketplace.service;

import com.jo.marketplace.security.JwtProvider;
import com.jo.marketplace.service.interfaces.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.jo.marketplace.constant.AuthConstants.BLACKLIST_TOKEN_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private final JwtProvider jwtProvider;

    @Override
    public void blacklist(String token) {
        String tokenId = jwtProvider.getTokenId(token);
        Duration remainingTtl = jwtProvider.getRemainingTtl(token);

        if (!remainingTtl.isPositive()) {
            return;
        }

        redisTemplate.opsForValue().set(buildKey(tokenId), "revoked", remainingTtl);
    }

    @Override
    public boolean isBlacklisted(String token) {
        String tokenId = jwtProvider.getTokenId(token);
        return Boolean.TRUE.equals(redisTemplate.hasKey(buildKey(tokenId)));
    }

    private String buildKey(String tokenId) {
        return BLACKLIST_TOKEN_KEY_PREFIX + tokenId;
    }
}
