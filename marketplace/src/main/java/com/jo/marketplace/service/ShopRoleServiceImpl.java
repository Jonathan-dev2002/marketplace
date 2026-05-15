package com.jo.marketplace.service;

import com.jo.marketplace.entity.MasPermissionEntity;
import com.jo.marketplace.entity.MasRoleEntity;
import com.jo.marketplace.entity.MasShopEntity;
import com.jo.marketplace.exception.BusinessException;
import com.jo.marketplace.model.dto.request.CreateShopRoleRequest;
import com.jo.marketplace.model.dto.request.UpdateRolePermissionsRequest;
import com.jo.marketplace.model.dto.request.UpdateShopRoleRequest;
import com.jo.marketplace.model.dto.response.ShopPermissionResponse;
import com.jo.marketplace.model.dto.response.ShopRoleResponse;
import com.jo.marketplace.repository.interfaces.MasPermissionRepository;
import com.jo.marketplace.repository.interfaces.MasRoleRepository;
import com.jo.marketplace.repository.interfaces.MasShopRepository;
import com.jo.marketplace.repository.interfaces.MasUserShopRoleRepository;
import com.jo.marketplace.service.interfaces.ShopRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.jo.marketplace.constant.AppConstants.PLATFORM_SHOP_ID;
import static com.jo.marketplace.constant.PermissionConstants.ALL_PERMISSIONS;
import static com.jo.marketplace.constant.RoleConstants.SELLER;
import static com.jo.marketplace.constant.StatusCodeEnums.*;

@Service
@RequiredArgsConstructor
public class ShopRoleServiceImpl implements ShopRoleService {

    private final MasShopRepository shopRepository;
    private final MasRoleRepository roleRepository;
    private final MasPermissionRepository permissionRepository;
    private final MasUserShopRoleRepository userShopRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ShopRoleResponse> getRoles(UUID shopId) {
        getShopOrThrow(shopId);
        return roleRepository.findByShopIdOrIsSystemDefinedTrue(shopId).stream()
                .filter(role -> !Boolean.TRUE.equals(role.getIsSystemDefined()) || SELLER.equals(role.getName()))
                .map(this::toRoleResponse)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopRoleResponse createRole(UUID shopId, CreateShopRoleRequest request) {
        getShopOrThrow(shopId);

        String roleName = normalizeRoleName(request.getName());
        validateRoleNameAvailable(shopId, roleName);

        MasRoleEntity role = new MasRoleEntity();
        role.setShopId(shopId);
        role.setName(roleName);
        role.setDescription(request.getDescription());
        role.setIsSystemDefined(false);
        role.setPermissions(resolvePermissions(request.getPermissionSlugs()));

        return toRoleResponse(roleRepository.save(role));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopRoleResponse updateRole(UUID shopId, UUID roleId, UpdateShopRoleRequest request) {
        MasRoleEntity role = getShopRoleOrThrow(shopId, roleId);
        validateCustomRole(role);

        if (StringUtils.hasText(request.getName())) {
            String roleName = normalizeRoleName(request.getName());
            if (!role.getName().equals(roleName)) {
                validateRoleNameAvailable(shopId, roleName);
                role.setName(roleName);
            }
        }

        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }

        if (request.getPermissionSlugs() != null) {
            role.setPermissions(resolvePermissions(request.getPermissionSlugs()));
        }

        return toRoleResponse(roleRepository.save(role));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(UUID shopId, UUID roleId) {
        MasRoleEntity role = getShopRoleOrThrow(shopId, roleId);
        validateCustomRole(role);

        if (userShopRoleRepository.existsByRoleId(roleId)) {
            throw new BusinessException(ROLE_IN_USE_409, ROLE_IN_USE_409.getDescriptionTH());
        }

        roleRepository.delete(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopRoleResponse updateRolePermissions(UUID shopId, UUID roleId, UpdateRolePermissionsRequest request) {
        MasRoleEntity role = getShopRoleOrThrow(shopId, roleId);
        validateCustomRole(role);
        role.setPermissions(resolvePermissions(request.getPermissionSlugs()));
        return toRoleResponse(roleRepository.save(role));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopPermissionResponse> getMyPermissions(UUID shopId, UUID userId) {
        MasShopEntity shop = getShopOrThrow(shopId);
        if (shop.getOwnerId().equals(userId)
                || hasPlatformAdminPermissions(userId)) {
            return permissionRepository.findBySlugIn(ALL_PERMISSIONS).stream()
                    .map(this::toPermissionResponse)
                    .toList();
        }

        return userShopRoleRepository.findPermissionsByUserIdAndShopId(userId, shopId).stream()
                .map(this::toPermissionResponse)
                .toList();
    }

    private boolean hasPlatformAdminPermissions(UUID userId) {
        return userShopRoleRepository.countDistinctPermissionsByUserIdAndShopId(userId, PLATFORM_SHOP_ID) >= ALL_PERMISSIONS.size();
    }

    private MasShopEntity getShopOrThrow(UUID shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND_404, SHOP_NOT_FOUND_404.getDescriptionTH()));
    }

    private MasRoleEntity getShopRoleOrThrow(UUID shopId, UUID roleId) {
        MasRoleEntity role = roleRepository.findWithPermissionsById(roleId)
                .orElseThrow(() -> new BusinessException(ROLE_NOT_FOUND_404, ROLE_NOT_FOUND_404.getDescriptionTH()));

        if (!shopId.equals(role.getShopId())) {
            throw new BusinessException(SHOP_ROLE_INVALID_400, SHOP_ROLE_INVALID_400.getDescriptionTH());
        }

        return role;
    }

    private void validateCustomRole(MasRoleEntity role) {
        if (Boolean.TRUE.equals(role.getIsSystemDefined())) {
            throw new BusinessException(ROLE_SYSTEM_MODIFY_INVALID_400, ROLE_SYSTEM_MODIFY_INVALID_400.getDescriptionTH());
        }
    }

    private void validateRoleNameAvailable(UUID shopId, String roleName) {
        if (roleRepository.findByNameAndIsSystemDefinedTrue(roleName).isPresent()
                || roleRepository.existsByNameAndShopId(roleName, shopId)) {
            throw new BusinessException(ROLE_DUPLICATE_409, ROLE_DUPLICATE_409.getDescriptionTH());
        }
    }

    private String normalizeRoleName(String roleName) {
        return roleName.trim().replace(" ", "_").toUpperCase();
    }

    private Set<MasPermissionEntity> resolvePermissions(List<String> permissionSlugs) {
        if (permissionSlugs == null || permissionSlugs.isEmpty()) {
            return new HashSet<>();
        }

        Set<String> normalizedSlugs = permissionSlugs.stream()
                .filter(StringUtils::hasText)
                .map(slug -> slug.trim().toUpperCase())
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));

        if (!ALL_PERMISSIONS.containsAll(normalizedSlugs)) {
            throw new BusinessException(PERMISSION_NOT_FOUND_404, PERMISSION_NOT_FOUND_404.getDescriptionTH());
        }

        List<MasPermissionEntity> permissions = permissionRepository.findBySlugIn(normalizedSlugs);
        if (permissions.size() != normalizedSlugs.size()) {
            throw new BusinessException(PERMISSION_NOT_FOUND_404, PERMISSION_NOT_FOUND_404.getDescriptionTH());
        }

        return new HashSet<>(permissions);
    }

    private ShopRoleResponse toRoleResponse(MasRoleEntity role) {
        return ShopRoleResponse.builder()
                .id(role.getId())
                .shopId(role.getShopId())
                .name(role.getName())
                .description(role.getDescription())
                .systemDefined(role.getIsSystemDefined())
                .permissions(role.getPermissions().stream()
                        .map(this::toPermissionResponse)
                        .toList())
                .build();
    }

    private ShopPermissionResponse toPermissionResponse(MasPermissionEntity permission) {
        return ShopPermissionResponse.builder()
                .id(permission.getId())
                .slug(permission.getSlug())
                .module(permission.getModule())
                .description(permission.getDescription())
                .build();
    }
}
