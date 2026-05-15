package com.jo.marketplace.service.interfaces;

public interface TokenBlacklistService {

    void blacklist(String token);

    boolean isBlacklisted(String token);
}
