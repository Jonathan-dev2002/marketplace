package com.jo.marketplace.controller;

import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.model.dto.request.AssignShopEmployeeRequest;
import com.jo.marketplace.model.dto.request.ChangeShopEmployeeRoleRequest;
import com.jo.marketplace.model.dto.response.ShopEmployeeResponse;
import com.jo.marketplace.security.UserPrincipal;
import com.jo.marketplace.service.interfaces.ShopService;
import com.jo.marketplace.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.jo.marketplace.constant.StatusCodeEnums.SHOP_EMPLOYEE_ASSIGNED_200;
import static com.jo.marketplace.constant.StatusCodeEnums.SHOP_EMPLOYEE_REMOVED_200;
import static com.jo.marketplace.constant.StatusCodeEnums.SHOP_EMPLOYEE_ROLE_UPDATED_200;

@RestController
@RequestMapping("/api/v1/shops/{shopId}/employees")
@RequiredArgsConstructor
public class ShopEmployeeController {

    private final ShopService shopService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<ShopEmployeeResponse>>> getEmployees(
            @PathVariable UUID shopId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseUtil.success(shopService.getEmployees(shopId, principal.getUserId()));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Void>> assignEmployee(
            @PathVariable UUID shopId,
            @Valid @RequestBody AssignShopEmployeeRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        shopService.assignEmployee(shopId, request, principal.getUserId());
        return ResponseUtil.success(SHOP_EMPLOYEE_ASSIGNED_200, null);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Void>> removeEmployee(
            @PathVariable UUID shopId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        shopService.removeEmployee(shopId, userId, principal.getUserId());
        return ResponseUtil.success(SHOP_EMPLOYEE_REMOVED_200, null);
    }

    @PatchMapping("/{userId}/role")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Void>> changeEmployeeRole(
            @PathVariable UUID shopId,
            @PathVariable UUID userId,
            @Valid @RequestBody ChangeShopEmployeeRoleRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        shopService.changeEmployeeRole(shopId, userId, request, principal.getUserId());
        return ResponseUtil.success(SHOP_EMPLOYEE_ROLE_UPDATED_200, null);
    }
}
