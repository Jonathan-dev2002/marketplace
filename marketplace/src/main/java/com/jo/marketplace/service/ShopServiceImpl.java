package com.jo.marketplace.service;

import com.jo.marketplace.constant.RoleConstants;
import com.jo.marketplace.entity.MasRoleEntity;
import com.jo.marketplace.entity.MasShopEntity;
import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.entity.MasUserShopRoleEntity;
import com.jo.marketplace.exception.BusinessException;
import com.jo.marketplace.model.dto.request.AssignShopEmployeeRequest;
import com.jo.marketplace.model.dto.request.ChangeShopEmployeeRoleRequest;
import com.jo.marketplace.model.dto.request.CreateShopRequest;
import com.jo.marketplace.model.dto.request.UpdateShopRequest;
import com.jo.marketplace.model.dto.request.UpdateShopSlugRequest;
import com.jo.marketplace.model.dto.request.UpdateShopStatusRequest;
import com.jo.marketplace.model.dto.response.ShopEmployeeResponse;
import com.jo.marketplace.model.dto.response.ShopResponse;
import com.jo.marketplace.repository.interfaces.MasRoleRepository;
import com.jo.marketplace.repository.interfaces.MasShopRepository;
import com.jo.marketplace.repository.interfaces.MasUserRepository;
import com.jo.marketplace.repository.interfaces.MasUserShopRoleRepository;
import com.jo.marketplace.service.interfaces.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.jo.marketplace.constant.StatusCodeEnums.*;
import static com.jo.marketplace.constant.ValidationPatterns.MULTIPLE_SPACES_PATTERN;
import static com.jo.marketplace.constant.ValidationPatterns.SLUG_ALLOWED_CHARS_PATTERN;
import static com.jo.marketplace.constant.ValidationPatterns.SLUG_VALID_FORMAT;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final MasShopRepository shopRepository;
    private final MasRoleRepository roleRepository;
    private final MasUserRepository userRepository;
    private final MasUserShopRoleRepository userShopRoleRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createShop(CreateShopRequest request, UUID currentUserId) {
        log.info("User {} is creating a new shop: {}", currentUserId, request.getName());

        String normalizedName = request.getName().trim();
        if (shopRepository.existsByName(normalizedName)) {
            throw new BusinessException(SHOP_NAME_DUPLICATE_400, SHOP_NAME_DUPLICATE_400.getDescriptionTH());
        }

        String slug = generateUniqueSlug(normalizedName);

        MasShopEntity newShop = new MasShopEntity();
        newShop.setOwnerId(currentUserId);
        newShop.setName(normalizedName);
        newShop.setSlug(slug);
        newShop.setDescription(request.getDescription());
        newShop.setLogoUrl(request.getLogoUrl());

        MasShopEntity savedShop = shopRepository.save(newShop);

        MasRoleEntity sellerRole = roleRepository.findByNameAndIsSystemDefinedTrue(RoleConstants.SELLER)
                .orElseThrow(() -> new BusinessException(ROLE_NOT_FOUND_404, ROLE_NOT_FOUND_404.getDescriptionTH()));

        MasUserShopRoleEntity ownerRole = new MasUserShopRoleEntity();
        ownerRole.setUserId(currentUserId);
        ownerRole.setShopId(savedShop.getId());
        ownerRole.setRoleId(sellerRole.getId());

        userShopRoleRepository.save(ownerRole);

        log.info("Shop created successfully with slug: {}", slug);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopResponse getShop(UUID shopId) {
        return toShopResponse(getShopOrThrow(shopId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> getMyShops(UUID currentUserId) {
        Map<UUID, ShopResponse> shops = new LinkedHashMap<>();

        shopRepository.findByOwnerId(currentUserId)
                .forEach(shop -> shops.put(shop.getId(), toShopResponse(shop)));

        userShopRoleRepository.findByUserId(currentUserId).stream()
                .map(MasUserShopRoleEntity::getShop)
                .filter(shop -> shop != null && shop.getDeletedAt() == null)
                .forEach(shop -> shops.putIfAbsent(shop.getId(), toShopResponse(shop)));

        return List.copyOf(shops.values());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShop(UUID shopId, UpdateShopRequest request, UUID currentUserId) {
        MasShopEntity shop = getShopOrThrow(shopId);
        validateShopOwner(shop, currentUserId);

        if (StringUtils.hasText(request.getName())) {
            String normalizedName = request.getName().trim();
            if (shopRepository.existsByNameAndIdNot(normalizedName, shopId)) {
                throw new BusinessException(SHOP_NAME_DUPLICATE_400, SHOP_NAME_DUPLICATE_400.getDescriptionTH());
            }
            shop.setName(normalizedName);
        }

        if (request.getDescription() != null) {
            shop.setDescription(request.getDescription());
        }

        if (request.getLogoUrl() != null) {
            shop.setLogoUrl(request.getLogoUrl());
        }

        shopRepository.save(shop);

        log.info("Shop {} updated by user {}", shopId, currentUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShopStatus(UUID shopId, UpdateShopStatusRequest request, UUID currentUserId) {
        MasShopEntity shop = getShopOrThrow(shopId);
        validateShopOwner(shop, currentUserId);

        shop.setIsActive(request.getActive());
        shopRepository.save(shop);

        log.info("Shop {} active status updated to {} by user {}", shopId, request.getActive(), currentUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShopSlug(UUID shopId, UpdateShopSlugRequest request, UUID currentUserId) {
        MasShopEntity shop = getShopOrThrow(shopId);
        validateShopOwner(shop, currentUserId);

        String slug = normalizeSlug(request.getSlug());
        validateSlug(slug);

        if (shopRepository.existsBySlugAndIdNot(slug, shopId)) {
            throw new BusinessException(SHOP_SLUG_DUPLICATE_409, SHOP_SLUG_DUPLICATE_409.getDescriptionTH());
        }

        shop.setSlug(slug);
        shopRepository.save(shop);

        log.info("Shop {} slug updated by user {}", shopId, currentUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void softDeleteShop(UUID shopId, UUID currentUserId) {
        MasShopEntity shop = getShopOrThrow(shopId);
        validateShopOwner(shop, currentUserId);

        shop.setIsActive(false);
        shop.setDeletedAt(LocalDateTime.now());
        shopRepository.save(shop);

        log.info("Shop {} soft deleted by user {}", shopId, currentUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignEmployee(UUID shopId, AssignShopEmployeeRequest request, UUID currentUserId) {
        MasShopEntity shop = getShopOrThrow(shopId);
        validateShopOwner(shop, currentUserId);

        MasUserEntity employee = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND_404, USER_NOT_FOUND_404.getDescriptionTH()));

        MasRoleEntity role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new BusinessException(ROLE_NOT_FOUND_404, ROLE_NOT_FOUND_404.getDescriptionTH()));

        validateNotOwner(shop, employee.getId());
        validateAssignableRole(role, shopId);

        if (userShopRoleRepository.existsByUserIdAndShopId(employee.getId(), shopId)) {
            throw new BusinessException(SHOP_EMPLOYEE_DUPLICATE_409, SHOP_EMPLOYEE_DUPLICATE_409.getDescriptionTH());
        }

        MasUserShopRoleEntity employeeRole = new MasUserShopRoleEntity();
        employeeRole.setUserId(employee.getId());
        employeeRole.setShopId(shopId);
        employeeRole.setRoleId(role.getId());

        userShopRoleRepository.save(employeeRole);

        log.info("User {} assigned to shop {} with role {}", employee.getId(), shopId, role.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopEmployeeResponse> getEmployees(UUID shopId, UUID currentUserId) {
        MasShopEntity shop = getShopOrThrow(shopId);
        validateShopOwner(shop, currentUserId);

        return userShopRoleRepository.findByShopId(shopId).stream()
                .map(shopRole -> toShopEmployeeResponse(shopRole, shop.getOwnerId()))
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeEmployee(UUID shopId, UUID userId, UUID currentUserId) {
        MasShopEntity shop = getShopOrThrow(shopId);
        validateShopOwner(shop, currentUserId);
        validateNotOwner(shop, userId);

        userShopRoleRepository.findFirstByShopIdAndUserId(shopId, userId)
                .orElseThrow(() -> new BusinessException(SHOP_EMPLOYEE_NOT_FOUND_404, SHOP_EMPLOYEE_NOT_FOUND_404.getDescriptionTH()));

        userShopRoleRepository.deleteByShopIdAndUserId(shopId, userId);

        log.info("User {} removed from shop {} by user {}", userId, shopId, currentUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeEmployeeRole(UUID shopId, UUID userId, ChangeShopEmployeeRoleRequest request, UUID currentUserId) {
        MasShopEntity shop = getShopOrThrow(shopId);
        validateShopOwner(shop, currentUserId);
        validateNotOwner(shop, userId);

        MasUserShopRoleEntity currentRole = userShopRoleRepository.findFirstByShopIdAndUserId(shopId, userId)
                .orElseThrow(() -> new BusinessException(SHOP_EMPLOYEE_NOT_FOUND_404, SHOP_EMPLOYEE_NOT_FOUND_404.getDescriptionTH()));

        MasRoleEntity newRole = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new BusinessException(ROLE_NOT_FOUND_404, ROLE_NOT_FOUND_404.getDescriptionTH()));

        validateAssignableRole(newRole, shopId);

        if (currentRole.getRoleId().equals(newRole.getId())) {
            return;
        }

        userShopRoleRepository.deleteByShopIdAndUserId(shopId, userId);

        MasUserShopRoleEntity employeeRole = new MasUserShopRoleEntity();
        employeeRole.setUserId(userId);
        employeeRole.setShopId(shopId);
        employeeRole.setRoleId(newRole.getId());
        userShopRoleRepository.save(employeeRole);

        log.info("User {} role changed in shop {} by user {}", userId, shopId, currentUserId);
    }

    private MasShopEntity getShopOrThrow(UUID shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND_404, SHOP_NOT_FOUND_404.getDescriptionTH()));
    }

    private void validateShopOwner(MasShopEntity shop, UUID currentUserId) {
        if (!shop.getOwnerId().equals(currentUserId)) {
            throw new BusinessException(SHOP_ACCESS_DENIED_403, SHOP_ACCESS_DENIED_403.getDescriptionTH());
        }
    }

    private void validateNotOwner(MasShopEntity shop, UUID userId) {
        if (shop.getOwnerId().equals(userId)) {
            throw new BusinessException(SHOP_OWNER_OPERATION_INVALID_400, SHOP_OWNER_OPERATION_INVALID_400.getDescriptionTH());
        }
    }

    private void validateAssignableRole(MasRoleEntity role, UUID shopId) {
        if (Boolean.TRUE.equals(role.getIsSystemDefined()) && RoleConstants.SELLER.equals(role.getName())) {
            return;
        }

        if (shopId.equals(role.getShopId())) {
            return;
        }

        throw new BusinessException(SHOP_ROLE_INVALID_400, SHOP_ROLE_INVALID_400.getDescriptionTH());
    }

    private String generateUniqueSlug(String name) {
        String slug = generateSlug(name);

        if (shopRepository.existsBySlug(slug)) {
            return slug + "-" + UUID.randomUUID().toString().substring(0, 5);
        }

        return slug;
    }

    private String generateSlug(String name) {
        return normalizeSlug(name);
    }

    private String normalizeSlug(String name) {
        String slug = name.toLowerCase().trim();
        slug = MULTIPLE_SPACES_PATTERN.matcher(slug).replaceAll("-");
        return SLUG_ALLOWED_CHARS_PATTERN.matcher(slug).replaceAll("");
    }

    private void validateSlug(String slug) {
        if (!StringUtils.hasText(slug) || !slug.matches(SLUG_VALID_FORMAT)) {
            throw new BusinessException(SHOP_SLUG_INVALID_400, SHOP_SLUG_INVALID_400.getDescriptionTH());
        }
    }

    private ShopResponse toShopResponse(MasShopEntity shop) {
        return ShopResponse.builder()
                .id(shop.getId())
                .ownerId(shop.getOwnerId())
                .name(shop.getName())
                .slug(shop.getSlug())
                .description(shop.getDescription())
                .logoUrl(shop.getLogoUrl())
                .verified(shop.getIsVerified())
                .active(shop.getIsActive())
                .ratingAvg(shop.getRatingAvg())
                .build();
    }

    private ShopEmployeeResponse toShopEmployeeResponse(MasUserShopRoleEntity shopRole, UUID ownerId) {
        MasUserEntity user = shopRole.getUser();
        MasRoleEntity role = shopRole.getRole();

        return ShopEmployeeResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roleId(role.getId())
                .roleName(role.getName())
                .owner(ownerId.equals(user.getId()))
                .build();
    }
}
