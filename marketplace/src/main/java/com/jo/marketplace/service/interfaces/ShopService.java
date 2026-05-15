package com.jo.marketplace.service.interfaces;

import com.jo.marketplace.model.dto.request.CreateShopRequest;
import com.jo.marketplace.model.dto.request.AssignShopEmployeeRequest;
import com.jo.marketplace.model.dto.request.ChangeShopEmployeeRoleRequest;
import com.jo.marketplace.model.dto.request.UpdateShopSlugRequest;
import com.jo.marketplace.model.dto.request.UpdateShopStatusRequest;
import com.jo.marketplace.model.dto.request.UpdateShopRequest;
import com.jo.marketplace.model.dto.response.ShopEmployeeResponse;
import com.jo.marketplace.model.dto.response.ShopResponse;

import java.util.List;
import java.util.UUID;

public interface ShopService {

    void createShop(CreateShopRequest request, UUID currentUserId);

    ShopResponse getShop(UUID shopId);

    List<ShopResponse> getMyShops(UUID currentUserId);

    void updateShop(UUID shopId, UpdateShopRequest request, UUID currentUserId);

    void updateShopStatus(UUID shopId, UpdateShopStatusRequest request, UUID currentUserId);

    void updateShopSlug(UUID shopId, UpdateShopSlugRequest request, UUID currentUserId);

    void softDeleteShop(UUID shopId, UUID currentUserId);

    void assignEmployee(UUID shopId, AssignShopEmployeeRequest request, UUID currentUserId);

    List<ShopEmployeeResponse> getEmployees(UUID shopId, UUID currentUserId);

    void removeEmployee(UUID shopId, UUID userId, UUID currentUserId);

    void changeEmployeeRole(UUID shopId, UUID userId, ChangeShopEmployeeRoleRequest request, UUID currentUserId);
}
