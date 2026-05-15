package com.jo.marketplace.security;

import com.jo.marketplace.entity.MasShopEntity;
import com.jo.marketplace.repository.interfaces.MasShopRepository;
import com.jo.marketplace.repository.interfaces.MasUserShopRoleRepository;
import com.jo.marketplace.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.jo.marketplace.constant.AppConstants.PLATFORM_SHOP_ID;

@Component("shopSecurity")
@RequiredArgsConstructor
public class ShopSecurity {

    private final MasShopRepository shopRepository;
    private final MasUserShopRoleRepository userShopRoleRepository;

    public boolean hasPermission(UUID shopId, String permissionSlug) {
        UserPrincipal currentUser = UserUtil.getCurrentUser();
        if (currentUser == null || shopId == null || permissionSlug == null) {
            return false;
        }

        UUID userId = currentUser.getUserId();
        return isShopOwner(shopId, userId)
                || userShopRoleRepository.hasPermission(userId, shopId, permissionSlug)
                || userShopRoleRepository.hasPermission(userId, PLATFORM_SHOP_ID, permissionSlug);
    }

    private boolean isShopOwner(UUID shopId, UUID userId) {
        return shopRepository.findById(shopId)
                .map(MasShopEntity::getOwnerId)
                .filter(userId::equals)
                .isPresent();
    }
}
