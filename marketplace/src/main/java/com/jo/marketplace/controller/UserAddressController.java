package com.jo.marketplace.controller;

import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.model.dto.request.CreateUserAddressRequest;
import com.jo.marketplace.model.dto.request.UpdateUserAddressRequest;
import com.jo.marketplace.model.dto.response.UserAddressResponse;
import com.jo.marketplace.service.interfaces.UserAddressService;
import com.jo.marketplace.utils.ResponseUtil;
import com.jo.marketplace.utils.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import static com.jo.marketplace.constant.StatusCodeEnums.ADDRESS_CREATED_201;
import static com.jo.marketplace.constant.StatusCodeEnums.ADDRESS_DEFAULT_UPDATED_200;
import static com.jo.marketplace.constant.StatusCodeEnums.ADDRESS_DELETED_200;
import static com.jo.marketplace.constant.StatusCodeEnums.ADDRESS_UPDATED_200;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<UserAddressResponse>>> getMyAddresses() {
        return ResponseUtil.success(userAddressService.getMyAddresses(UserUtil.getCurrentUser().getUserId()));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<UserAddressResponse>> createAddress(
            @Valid @RequestBody CreateUserAddressRequest request
    ) {
        UserAddressResponse response = userAddressService.createAddress(UserUtil.getCurrentUser().getUserId(), request);
        return ResponseUtil.success(ADDRESS_CREATED_201, response);
    }

    @GetMapping("/{addressId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<UserAddressResponse>> getAddress(@PathVariable UUID addressId) {
        return ResponseUtil.success(userAddressService.getAddress(UserUtil.getCurrentUser().getUserId(), addressId));
    }

    @PatchMapping("/{addressId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<UserAddressResponse>> updateAddress(
            @PathVariable UUID addressId,
            @Valid @RequestBody UpdateUserAddressRequest request
    ) {
        UserAddressResponse response = userAddressService.updateAddress(UserUtil.getCurrentUser().getUserId(), addressId, request);
        return ResponseUtil.success(ADDRESS_UPDATED_200, response);
    }

    @DeleteMapping("/{addressId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Void>> deleteAddress(@PathVariable UUID addressId) {
        userAddressService.deleteAddress(UserUtil.getCurrentUser().getUserId(), addressId);
        return ResponseUtil.success(ADDRESS_DELETED_200, null);
    }

    @PatchMapping("/{addressId}/default")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<UserAddressResponse>> setDefaultAddress(@PathVariable UUID addressId) {
        UserAddressResponse response = userAddressService.setDefaultAddress(UserUtil.getCurrentUser().getUserId(), addressId);
        return ResponseUtil.success(ADDRESS_DEFAULT_UPDATED_200, response);
    }
}
