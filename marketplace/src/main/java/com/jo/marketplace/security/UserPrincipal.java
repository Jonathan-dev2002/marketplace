package com.jo.marketplace.security;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class UserPrincipal {
    private UUID userId;
    private String username;
    private List<String> roles;
    private List<String> authorities;
}
