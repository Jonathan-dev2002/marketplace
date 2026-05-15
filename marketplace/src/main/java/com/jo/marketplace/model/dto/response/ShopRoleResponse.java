package com.jo.marketplace.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ShopRoleResponse {
    private UUID id;
    private UUID shopId;
    private String name;
    private String description;
    private Boolean systemDefined;
    private List<ShopPermissionResponse> permissions;
}
