package com.jo.marketplace.config;

import com.jo.marketplace.entity.MasRoleEntity;
import com.jo.marketplace.entity.MasPermissionEntity;
import com.jo.marketplace.entity.MasShopEntity;
import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.entity.MasUserShopRoleEntity;
import com.jo.marketplace.repository.interfaces.MasPermissionRepository;
import com.jo.marketplace.repository.interfaces.MasRoleRepository;
import com.jo.marketplace.repository.interfaces.MasShopRepository;
import com.jo.marketplace.repository.interfaces.MasUserRepository;
import com.jo.marketplace.repository.interfaces.MasUserShopRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.jo.marketplace.constant.AppConstants.*;
import static com.jo.marketplace.constant.PermissionConstants.*;
import static com.jo.marketplace.constant.RoleConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final MasRoleRepository roleRepository;
    private final MasPermissionRepository permissionRepository;
    private final MasUserRepository userRepository;
    private final MasShopRepository shopRepository;
    private final MasUserShopRoleRepository userShopRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.first-name}")
    private String adminFirstName;

    @Value("${app.admin.last-name}")
    private String adminLastName;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedPlatformShop();
        seedRoles();
        seedPermissions();
        seedRolePermissions();
        seedAdminAssignment();
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

    private void seedAdminAssignment() {
        MasUserEntity systemAdmin = userRepository.findByUsername(adminUsername).orElse(null);
        if (systemAdmin == null || userShopRoleRepository.existsByUserIdAndShopId(systemAdmin.getId(), PLATFORM_SHOP_ID)) {
            return;
        }

        MasRoleEntity adminRole = getSystemRole(ADMIN);
        MasUserShopRoleEntity adminAssignment = new MasUserShopRoleEntity();
        adminAssignment.setUserId(systemAdmin.getId());
        adminAssignment.setShopId(PLATFORM_SHOP_ID);
        adminAssignment.setRoleId(adminRole.getId());
        userShopRoleRepository.save(adminAssignment);
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

    private void seedPlatformShop() {
        if (!shopRepository.existsById(PLATFORM_SHOP_ID)) {
            log.info("Seeding Platform Shop...");

            MasUserEntity systemAdmin = userRepository.findByUsername(adminUsername)
                    .orElseGet(() -> {
                        MasUserEntity admin = new MasUserEntity();
                        admin.setUsername(adminUsername);
                        admin.setEmail(adminEmail);
                        admin.setPassword(passwordEncoder.encode(adminPassword));
                        admin.setFirstName(adminFirstName);
                        admin.setLastName(adminLastName);
                        return userRepository.saveAndFlush(admin);
                    });

            String sql = "INSERT INTO shops (id, owner_id, name, slug, description, is_active, is_verified, rating_avg) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(sql,
                    PLATFORM_SHOP_ID,
                    systemAdmin.getId(),
                    PLATFORM_SHOP_NAME,
                    PLATFORM_SHOP_SLUG,
                    PLATFORM_SHOP_DESC,
                    true,
                    true,
                    0.00
            );

            log.info("Platform Shop created successfully.");
        }
    }
}
