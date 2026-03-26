package com.jo.marketplace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "permissions")
@Getter
@Setter
public class MasPermissionEntity extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String slug; // เช่น "product:create", "shop:edit"

    @Column(length = 50)
    private String module; // จัดกลุ่มสิทธิ์ เช่น "PRODUCT", "ORDER"

    @Column(columnDefinition = "TEXT")
    private String description;
}