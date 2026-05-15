package com.jo.marketplace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class MasRoleEntity extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "shop_id")
    private UUID shopId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_system_defined" , columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isSystemDefined = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<MasPermissionEntity> permissions = new HashSet<>();
}
