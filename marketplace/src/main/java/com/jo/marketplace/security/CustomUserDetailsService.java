package com.jo.marketplace.security;

import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.model.enums.UserStatusEnum;
import com.jo.marketplace.repository.interfaces.MasUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MasUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        MasUserEntity userEntity = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));

        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .disabled(!UserStatusEnum.ACTIVE.equals(userEntity.getStatus()))
                .authorities(Collections.emptyList())
                .build();
    }
}
