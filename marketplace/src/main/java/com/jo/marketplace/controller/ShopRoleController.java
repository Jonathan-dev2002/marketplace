package com.jo.marketplace.controller;

import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.model.dto.request.CreateShopRoleRequest;
import com.jo.marketplace.model.dto.request.UpdateRolePermissionsRequest;
import com.jo.marketplace.model.dto.request.UpdateShopRoleRequest;
import com.jo.marketplace.model.dto.response.ShopRoleResponse;
import com.jo.marketplace.service.interfaces.ShopRoleService;
import com.jo.marketplace.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shops/{shopId}/roles")
@RequiredArgsConstructor
public class ShopRoleController {

    private final ShopRoleService shopRoleService;

    @GetMapping
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_ROLE_VIEW')")
    public ResponseEntity<BaseResponse<List<ShopRoleResponse>>> getRoles(@PathVariable UUID shopId) {
        return ResponseUtil.success(shopRoleService.getRoles(shopId));
    }

    @PostMapping
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_ROLE_MANAGE')")
    public ResponseEntity<BaseResponse<ShopRoleResponse>> createRole(
            @PathVariable UUID shopId,
            @Valid @RequestBody CreateShopRoleRequest request
    ) {
        return ResponseUtil.success(shopRoleService.createRole(shopId, request));
    }

    @PatchMapping("/{roleId}")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_ROLE_MANAGE')")
    public ResponseEntity<BaseResponse<ShopRoleResponse>> updateRole(
            @PathVariable UUID shopId,
            @PathVariable UUID roleId,
            @Valid @RequestBody UpdateShopRoleRequest request
    ) {
        return ResponseUtil.success(shopRoleService.updateRole(shopId, roleId, request));
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_ROLE_MANAGE')")
    public ResponseEntity<BaseResponse<Void>> deleteRole(
            @PathVariable UUID shopId,
            @PathVariable UUID roleId
    ) {
        shopRoleService.deleteRole(shopId, roleId);
        return ResponseUtil.success(null);
    }

    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_ROLE_MANAGE')")
    public ResponseEntity<BaseResponse<ShopRoleResponse>> updateRolePermissions(
            @PathVariable UUID shopId,
            @PathVariable UUID roleId,
            @Valid @RequestBody UpdateRolePermissionsRequest request
    ) {
        return ResponseUtil.success(shopRoleService.updateRolePermissions(shopId, roleId, request));
    }
}
