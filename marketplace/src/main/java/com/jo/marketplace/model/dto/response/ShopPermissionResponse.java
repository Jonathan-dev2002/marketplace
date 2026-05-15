package com.jo.marketplace.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ShopPermissionResponse {
    private UUID id;
    private String slug;
    private String module;
    private String description;
}
