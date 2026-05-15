package com.jo.marketplace.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class SecurityAuthorityUtil {

    private static final String ROLE_PREFIX = "ROLE_";

    private SecurityAuthorityUtil() {
    }

    public static List<String> toRoleAuthorities(Collection<String> roles) {
        if (roles == null) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(SecurityAuthorityUtil::toRoleAuthority)
                .distinct()
                .toList();
    }

    public static List<GrantedAuthority> toGrantedAuthorities(Collection<String> authorities) {
        if (authorities == null) {
            return Collections.emptyList();
        }

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .map(authority -> (GrantedAuthority) authority)
                .toList();
    }

    private static String toRoleAuthority(String role) {
        if (role.startsWith(ROLE_PREFIX)) {
            return role;
        }
        return ROLE_PREFIX + role;
    }
}
