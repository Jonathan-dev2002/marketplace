package com.jo.marketplace.security;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserPrincipal {
    private UUID userId;
    private String username;
    private String role;
}