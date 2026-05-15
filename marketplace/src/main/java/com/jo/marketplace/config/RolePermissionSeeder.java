package com.jo.marketplace.config;

import com.jo.marketplace.entity.MasPermissionEntity;
import com.jo.marketplace.entity.MasRoleEntity;
import com.jo.marketplace.repository.interfaces.MasPermissionRepository;
import com.jo.marketplace.repository.interfaces.MasRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.jo.marketplace.constant.PermissionConstants.ALL_PERMISSIONS;
import static com.jo.marketplace.constant.PermissionConstants.BUYER_PERMISSIONS;
import static com.jo.marketplace.constant.PermissionConstants.SELLER_PERMISSIONS;
import static com.jo.marketplace.constant.RoleConstants.ADMIN;
import static com.jo.marketplace.constant.RoleConstants.ADMIN_DESC;
import static com.jo.marketplace.constant.RoleConstants.BUYER;
import static com.jo.marketplace.constant.RoleConstants.BUYER_DESC;
import static com.jo.marketplace.constant.RoleConstants.SELLER;
import static com.jo.marketplace.constant.RoleConstants.SELLER_DESC;

@Slf4j
@Order(2)
@Component
@RequiredArgsConstructor
public class RolePermissionSeeder implements CommandLineRunner {

    private final MasRoleRepository roleRepository;
    private final MasPermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedPermissions();
        seedRolePermissions();
    }

    private void seedRoles() {
        log.info("Checking and seeding default Roles...");

        createSystemRoleIfMissing(ADMIN, ADMIN_DESC);
        createSystemRoleIfMissing(SELLER, SELLER_DESC);
        createSystemRoleIfMissing(BUYER, BUYER_DESC);
    }

    private void seedPermissions() {
        log.info("Checking and seeding default Permissions...");

        ALL_PERMISSIONS.forEach(permission -> createPermissionIfMissing(permission, moduleOf(permission)));
    }

    private void seedRolePermissions() {
        MasRoleEntity adminRole = getSystemRole(ADMIN);
        MasRoleEntity sellerRole = getSystemRole(SELLER);
        MasRoleEntity buyerRole = getSystemRole(BUYER);

        adminRole.setPermissions(new java.util.HashSet<>(permissionRepository.findBySlugIn(ALL_PERMISSIONS)));
        sellerRole.setPermissions(new java.util.HashSet<>(permissionRepository.findBySlugIn(SELLER_PERMISSIONS)));
        buyerRole.setPermissions(new java.util.HashSet<>(permissionRepository.findBySlugIn(BUYER_PERMISSIONS)));

        roleRepository.save(adminRole);
        roleRepository.save(sellerRole);
        roleRepository.save(buyerRole);
    }

    private void createSystemRoleIfMissing(String name, String description) {
        roleRepository.findByNameAndIsSystemDefinedTrue(name)
                .orElseGet(() -> {
                    MasRoleEntity role = new MasRoleEntity();
                    role.setName(name);
                    role.setDescription(description);
                    role.setIsSystemDefined(true);
                    return roleRepository.save(role);
                });
    }

    private MasRoleEntity getSystemRole(String name) {
        return roleRepository.findByNameAndIsSystemDefinedTrue(name)
                .orElseThrow(() -> new IllegalStateException("Missing system role: " + name));
    }

    private void createPermissionIfMissing(String slug, String module) {
        permissionRepository.findBySlug(slug)
                .orElseGet(() -> {
                    MasPermissionEntity permission = new MasPermissionEntity();
                    permission.setSlug(slug);
                    permission.setModule(module);
                    permission.setDescription(slug.replace("_", " ").toLowerCase());
                    return permissionRepository.save(permission);
                });
    }

    private String moduleOf(String permission) {
        int separatorIndex = permission.indexOf('_');
        if (separatorIndex < 0) {
            return permission;
        }
        return permission.substring(0, separatorIndex);
    }
}
