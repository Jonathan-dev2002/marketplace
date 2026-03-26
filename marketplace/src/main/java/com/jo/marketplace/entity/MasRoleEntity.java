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
    private UUID shopId; // NULL = Role กลางของระบบ, มีค่า = Role เฉพาะของร้านค้านั้น

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_system_defined" , columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isSystemDefined = false;

    @ManyToMany(fetch = FetchType.EAGER) // ใช้ EAGER เพราะเวลาดึง Role เรามักจะต้องการ Permission ไปทำ JWT เสมอ
    @JoinTable(
            name = "role_permissions", // ชื่อตารางกลางใน DB
            joinColumns = @JoinColumn(name = "role_id"), // ฝั่งเรา (Role)
            inverseJoinColumns = @JoinColumn(name = "permission_id") // ฝั่งนู้น (Permission)
    )
    private Set<MasPermissionEntity> permissions = new HashSet<>();
}