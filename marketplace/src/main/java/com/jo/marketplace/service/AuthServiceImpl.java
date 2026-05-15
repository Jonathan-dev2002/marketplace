package com.jo.marketplace.service;

import com.jo.marketplace.constant.RoleConstants;
import com.jo.marketplace.entity.MasRoleEntity;
import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.entity.MasUserShopRoleEntity;
import com.jo.marketplace.exception.BusinessException;
import com.jo.marketplace.model.dto.request.LoginRequest;
import com.jo.marketplace.model.dto.request.RegisterRequest;
import com.jo.marketplace.model.dto.response.AuthResponse;
import com.jo.marketplace.repository.interfaces.MasRoleRepository;
import com.jo.marketplace.repository.interfaces.MasUserRepository;
import com.jo.marketplace.repository.interfaces.MasUserShopRoleRepository;
import com.jo.marketplace.security.JwtProvider;
import com.jo.marketplace.service.interfaces.AuthService;
import com.jo.marketplace.service.interfaces.MasUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jo.marketplace.constant.AppConstants.PLATFORM_SHOP_ID;
import static com.jo.marketplace.constant.AuthConstants.TOKEN_TYPE_BEARER;
import static com.jo.marketplace.constant.StatusCodeEnums.INVALID_CREDENTIALS_401;
import static com.jo.marketplace.constant.StatusCodeEnums.ROLE_NOT_FOUND_404;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MasUserRepository userRepository;
    private final MasUserService masUserService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final MasRoleRepository roleRepository;
    private final MasUserShopRoleRepository userShopRoleRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        masUserService.validateDuplicateUser(request.getUsername(), request.getEmail());

        MasUserEntity newUser = new MasUserEntity();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setPhone(request.getPhone());

        MasUserEntity savedUser = userRepository.save(newUser);
        log.info("User registered successfully: {}", request.getUsername());

        MasRoleEntity buyerRole = roleRepository.findByNameAndIsSystemDefinedTrue(RoleConstants.BUYER)
                .orElseThrow(() -> new BusinessException(ROLE_NOT_FOUND_404, ROLE_NOT_FOUND_404.getDescriptionTH()));

        MasUserShopRoleEntity userShopRole = new MasUserShopRoleEntity();
        userShopRole.setUserId(savedUser.getId());
        userShopRole.setRoleId(buyerRole.getId());
        userShopRole.setShopId(PLATFORM_SHOP_ID);

        userShopRoleRepository.save(userShopRole);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
            );

            MasUserEntity user = masUserService.getUserByUsernameOrEmail(authentication.getName());

            List<String> roles = user.getShopRoles().stream()
                    .map(shopRole -> shopRole.getRole().getName())
                    .toList();

            String token = jwtProvider.generateToken(user.getId(), user.getUsername(), roles);

            return AuthResponse.builder()
                    .accessToken(token)
                    .username(user.getUsername())
                    .tokenType(TOKEN_TYPE_BEARER)
                    .build();

        } catch (Exception e) {
            log.warn("Login failed for user/email: {}", request.getUsernameOrEmail());
            throw new BusinessException(INVALID_CREDENTIALS_401, INVALID_CREDENTIALS_401.getDescriptionTH());
        }
    }
}
