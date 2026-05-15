package com.jo.marketplace.controller;

import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.model.dto.request.CreateShopRequest;
import com.jo.marketplace.model.dto.request.UpdateShopSlugRequest;
import com.jo.marketplace.model.dto.request.UpdateShopStatusRequest;
import com.jo.marketplace.model.dto.request.UpdateShopRequest;
import com.jo.marketplace.model.dto.response.ShopResponse;
import com.jo.marketplace.security.UserPrincipal;
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
}
