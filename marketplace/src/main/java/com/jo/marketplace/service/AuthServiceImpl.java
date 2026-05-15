package com.jo.marketplace.service;

import com.jo.marketplace.constant.RoleConstants;
import com.jo.marketplace.entity.MasRoleEntity;
import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.entity.MasUserShopRoleEntity;
import com.jo.marketplace.exception.BusinessException;
import com.jo.marketplace.model.dto.request.ChangePasswordRequest;
import com.jo.marketplace.model.dto.request.LoginRequest;
import com.jo.marketplace.model.dto.request.RefreshTokenRequest;
import com.jo.marketplace.model.dto.request.RegisterRequest;
import com.jo.marketplace.model.dto.response.AuthResponse;
import com.jo.marketplace.model.enums.UserStatusEnum;
import com.jo.marketplace.repository.interfaces.MasRoleRepository;
import com.jo.marketplace.repository.interfaces.MasUserRepository;
import com.jo.marketplace.repository.interfaces.MasUserShopRoleRepository;
import com.jo.marketplace.security.JwtProvider;
import com.jo.marketplace.security.SecurityAuthorityUtil;
import com.jo.marketplace.service.interfaces.AuthService;
import com.jo.marketplace.service.interfaces.MasUserService;
import com.jo.marketplace.service.interfaces.TokenBlacklistService;
import com.jo.marketplace.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.jo.marketplace.constant.AppConstants.PLATFORM_SHOP_ID;
import static com.jo.marketplace.constant.AuthConstants.BEARER_PREFIX;
import static com.jo.marketplace.constant.AuthConstants.TOKEN_TYPE_BEARER;
import static com.jo.marketplace.constant.StatusCodeEnums.ACCOUNT_DISABLED_403;
import static com.jo.marketplace.constant.StatusCodeEnums.CURRENT_PASSWORD_INVALID_400;
import static com.jo.marketplace.constant.StatusCodeEnums.INVALID_CREDENTIALS_401;
import static com.jo.marketplace.constant.StatusCodeEnums.REFRESH_TOKEN_INVALID_401;
import static com.jo.marketplace.constant.StatusCodeEnums.ROLE_NOT_FOUND_404;
import static com.jo.marketplace.constant.StatusCodeEnums.UNAUTHORIZED_401;

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
    private final TokenBlacklistService tokenBlacklistService;

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
            validateActiveUser(user);

            return issueAuthResponse(user);

        } catch (DisabledException e) {
            throw new BusinessException(ACCOUNT_DISABLED_403, ACCOUNT_DISABLED_403.getDescriptionTH());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Login failed for user/email: {}", request.getUsernameOrEmail());
            throw new BusinessException(INVALID_CREDENTIALS_401, INVALID_CREDENTIALS_401.getDescriptionTH());
        }
    }

    @Override
    public void logout(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith(BEARER_PREFIX)) {
            throw new BusinessException(UNAUTHORIZED_401, UNAUTHORIZED_401.getDescriptionTH());
        }

        String token = bearerToken.substring(BEARER_PREFIX.length());
        if (!jwtProvider.validateToken(token)) {
            throw new BusinessException(UNAUTHORIZED_401, UNAUTHORIZED_401.getDescriptionTH());
        }

        tokenBlacklistService.blacklist(token);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtProvider.validateToken(refreshToken)
                || !jwtProvider.isRefreshToken(refreshToken)
                || tokenBlacklistService.isBlacklisted(refreshToken)) {
            throw new BusinessException(REFRESH_TOKEN_INVALID_401, REFRESH_TOKEN_INVALID_401.getDescriptionTH());
        }

        UUID userId = jwtProvider.getUserIdFromToken(refreshToken);
        MasUserEntity user = masUserService.getUserById(userId);
        validateActiveUser(user);

        tokenBlacklistService.blacklist(refreshToken);
        return issueAuthResponse(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(ChangePasswordRequest request) {
        UUID userId = UserUtil.getCurrentUser().getUserId();
        MasUserEntity user = masUserService.getUserById(userId);
        validateActiveUser(user);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(CURRENT_PASSWORD_INVALID_400, CURRENT_PASSWORD_INVALID_400.getDescriptionTH());
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private void validateActiveUser(MasUserEntity user) {
        if (!UserStatusEnum.ACTIVE.equals(user.getStatus())) {
            throw new BusinessException(ACCOUNT_DISABLED_403, ACCOUNT_DISABLED_403.getDescriptionTH());
        }
    }

    private AuthResponse issueAuthResponse(MasUserEntity user) {
        List<String> roles = user.getShopRoles().stream()
                .map(shopRole -> shopRole.getRole().getName())
                .toList();
        List<String> authorities = SecurityAuthorityUtil.toRoleAuthorities(roles);

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getUsername(), roles, authorities);
        String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getUsername(), roles, authorities);

        return AuthResponse.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .tokenType(TOKEN_TYPE_BEARER)
                .expiresIn(jwtProvider.getAccessTokenExpirationMs() / 1000)
                .roles(roles)
                .authorities(authorities)
                .build();
    }
}
