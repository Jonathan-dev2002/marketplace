package com.jo.marketplace.service.interfaces;

import com.jo.marketplace.model.dto.request.CreateShopRoleRequest;
import com.jo.marketplace.model.dto.request.UpdateRolePermissionsRequest;
import com.jo.marketplace.model.dto.request.UpdateShopRoleRequest;
import com.jo.marketplace.model.dto.response.ShopPermissionResponse;
import com.jo.marketplace.model.dto.response.ShopRoleResponse;

import java.util.List;
import java.util.UUID;

public interface ShopRoleService {

    List<ShopRoleResponse> getRoles(UUID shopId);

    ShopRoleResponse createRole(UUID shopId, CreateShopRoleRequest request);

    ShopRoleResponse updateRole(UUID shopId, UUID roleId, UpdateShopRoleRequest request);

    void deleteRole(UUID shopId, UUID roleId);

    ShopRoleResponse updateRolePermissions(UUID shopId, UUID roleId, UpdateRolePermissionsRequest request);

    List<ShopPermissionResponse> getMyPermissions(UUID shopId, UUID userId);
}
