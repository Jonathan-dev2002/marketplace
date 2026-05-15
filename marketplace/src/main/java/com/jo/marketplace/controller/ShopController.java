package com.jo.marketplace.controller;

import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.model.dto.request.AssignShopEmployeeRequest;
import com.jo.marketplace.model.dto.request.ChangeShopEmployeeRoleRequest;
import com.jo.marketplace.model.dto.request.CreateShopRequest;
import com.jo.marketplace.model.dto.request.CreateShopRoleRequest;
import com.jo.marketplace.model.dto.request.UpdateRolePermissionsRequest;
import com.jo.marketplace.model.dto.request.UpdateShopSlugRequest;
import com.jo.marketplace.model.dto.request.UpdateShopStatusRequest;
import com.jo.marketplace.model.dto.request.UpdateShopRequest;
import com.jo.marketplace.model.dto.request.UpdateShopRoleRequest;
import com.jo.marketplace.model.dto.response.ShopEmployeeResponse;
import com.jo.marketplace.model.dto.response.ShopPermissionResponse;
import com.jo.marketplace.model.dto.response.ShopRoleResponse;
import com.jo.marketplace.model.dto.response.ShopResponse;
import com.jo.marketplace.security.UserPrincipal;
import com.jo.marketplace.service.interfaces.ShopRoleService;
import com.jo.marketplace.service.interfaces.ShopService;
import com.jo.marketplace.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.jo.marketplace.constant.StatusCodeEnums.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;
    private final ShopRoleService shopRoleService;

    @GetMapping("/{shopId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<ShopResponse>> getShop(@PathVariable UUID shopId) {
        return ResponseUtil.success(shopService.getShop(shopId));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<ShopResponse>>> getMyShops(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseUtil.success(shopService.getMyShops(principal.getUserId()));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Void>> createShop(
            @Valid @RequestBody CreateShopRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("Received request to create shop: {} from user: {}", request.getName(), principal.getUsername());

        shopService.createShop(request, principal.getUserId());

        return ResponseUtil.success(SHOP_CREATED_2001, null);
    }

    @PatchMapping("/{shopId}")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_UPDATE')")
    public ResponseEntity<BaseResponse<Void>> updateShop(
            @PathVariable UUID shopId,
            @Valid @RequestBody UpdateShopRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        shopService.updateShop(shopId, request, principal.getUserId());
        return ResponseUtil.success(SHOP_UPDATED_200, null);
    }

    @PatchMapping("/{shopId}/status")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_STATUS_UPDATE')")
    public ResponseEntity<BaseResponse<Void>> updateShopStatus(
            @PathVariable UUID shopId,
            @Valid @RequestBody UpdateShopStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        shopService.updateShopStatus(shopId, request, principal.getUserId());
        return ResponseUtil.success(SHOP_STATUS_UPDATED_200, null);
    }

    @PatchMapping("/{shopId}/slug")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_SLUG_UPDATE')")
    public ResponseEntity<BaseResponse<Void>> updateShopSlug(
            @PathVariable UUID shopId,
            @Valid @RequestBody UpdateShopSlugRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        shopService.updateShopSlug(shopId, request, principal.getUserId());
        return ResponseUtil.success(SHOP_SLUG_UPDATED_200, null);
    }

    @DeleteMapping("/{shopId}")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_DELETE')")
    public ResponseEntity<BaseResponse<Void>> deleteShop(
            @PathVariable UUID shopId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        shopService.softDeleteShop(shopId, principal.getUserId());
        return ResponseUtil.success(SHOP_DELETED_200, null);
    }

    @GetMapping("/{shopId}/employees")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_EMPLOYEE_VIEW')")
    public ResponseEntity<BaseResponse<List<ShopEmployeeResponse>>> getEmployees(
            @PathVariable UUID shopId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseUtil.success(shopService.getEmployees(shopId, principal.getUserId()));
    }

    @PostMapping("/{shopId}/employees")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_EMPLOYEE_MANAGE')")
    public ResponseEntity<BaseResponse<Void>> assignEmployee(
            @PathVariable UUID shopId,
            @Valid @RequestBody AssignShopEmployeeRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        shopService.assignEmployee(shopId, request, principal.getUserId());
        return ResponseUtil.success(SHOP_EMPLOYEE_ASSIGNED_200, null);
    }

    @DeleteMapping("/{shopId}/employees/{userId}")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_EMPLOYEE_MANAGE')")
    public ResponseEntity<BaseResponse<Void>> removeEmployee(
            @PathVariable UUID shopId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        shopService.removeEmployee(shopId, userId, principal.getUserId());
        return ResponseUtil.success(SHOP_EMPLOYEE_REMOVED_200, null);
    }

    @PatchMapping("/{shopId}/employees/{userId}/role")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_EMPLOYEE_MANAGE')")
    public ResponseEntity<BaseResponse<Void>> changeEmployeeRole(
            @PathVariable UUID shopId,
            @PathVariable UUID userId,
            @Valid @RequestBody ChangeShopEmployeeRoleRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        shopService.changeEmployeeRole(shopId, userId, request, principal.getUserId());
        return ResponseUtil.success(SHOP_EMPLOYEE_ROLE_UPDATED_200, null);
    }

    @GetMapping("/{shopId}/roles")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_ROLE_VIEW')")
    public ResponseEntity<BaseResponse<List<ShopRoleResponse>>> getRoles(@PathVariable UUID shopId) {
        return ResponseUtil.success(shopRoleService.getRoles(shopId));
    }

    @PostMapping("/{shopId}/roles")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_ROLE_MANAGE')")
    public ResponseEntity<BaseResponse<ShopRoleResponse>> createRole(
            @PathVariable UUID shopId,
            @Valid @RequestBody CreateShopRoleRequest request
    ) {
        return ResponseUtil.success(shopRoleService.createRole(shopId, request));
    }

    @PatchMapping("/{shopId}/roles/{roleId}")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_ROLE_MANAGE')")
    public ResponseEntity<BaseResponse<ShopRoleResponse>> updateRole(
            @PathVariable UUID shopId,
            @PathVariable UUID roleId,
            @Valid @RequestBody UpdateShopRoleRequest request
    ) {
        return ResponseUtil.success(shopRoleService.updateRole(shopId, roleId, request));
    }

    @DeleteMapping("/{shopId}/roles/{roleId}")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_ROLE_MANAGE')")
    public ResponseEntity<BaseResponse<Void>> deleteRole(
            @PathVariable UUID shopId,
            @PathVariable UUID roleId
    ) {
        shopRoleService.deleteRole(shopId, roleId);
        return ResponseUtil.success(null);
    }

    @PutMapping("/{shopId}/roles/{roleId}/permissions")
    @PreAuthorize("@shopSecurity.hasPermission(#shopId, 'SHOP_ROLE_MANAGE')")
    public ResponseEntity<BaseResponse<ShopRoleResponse>> updateRolePermissions(
            @PathVariable UUID shopId,
            @PathVariable UUID roleId,
            @Valid @RequestBody UpdateRolePermissionsRequest request
    ) {
        return ResponseUtil.success(shopRoleService.updateRolePermissions(shopId, roleId, request));
    }

    @GetMapping("/{shopId}/permissions/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<ShopPermissionResponse>>> getMyPermissions(
            @PathVariable UUID shopId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseUtil.success(shopRoleService.getMyPermissions(shopId, principal.getUserId()));
    }
}
