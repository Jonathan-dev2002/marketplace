package com.jo.marketplace.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateShopRoleRequest {

    @Size(max = 50, message = "ชื่อ Role ต้องไม่เกิน 50 ตัวอักษร")
    private String name;

    private String description;

    private List<String> permissionSlugs;
}
