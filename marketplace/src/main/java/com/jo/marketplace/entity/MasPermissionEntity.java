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
    private String slug;

    @Column(length = 50)
    private String module;

    @Column(columnDefinition = "TEXT")
    private String description;
}
