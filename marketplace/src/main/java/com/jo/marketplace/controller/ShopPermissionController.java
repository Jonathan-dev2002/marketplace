package com.jo.marketplace.controller;

import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.model.dto.response.ShopPermissionResponse;
import com.jo.marketplace.security.UserPrincipal;
import com.jo.marketplace.service.interfaces.ShopRoleService;
import com.jo.marketplace.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shops/{shopId}/permissions")
@RequiredArgsConstructor
public class ShopPermissionController {

    private final ShopRoleService shopRoleService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<ShopPermissionResponse>>> getMyPermissions(
            @PathVariable UUID shopId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseUtil.success(shopRoleService.getMyPermissions(shopId, principal.getUserId()));
    }
}
