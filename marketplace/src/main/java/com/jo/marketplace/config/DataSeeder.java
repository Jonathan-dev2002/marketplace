package com.jo.marketplace.config;

import com.jo.marketplace.entity.MasRoleEntity;
import com.jo.marketplace.entity.MasShopEntity;
import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.repository.interfaces.MasRoleRepository;
import com.jo.marketplace.repository.interfaces.MasShopRepository;
import com.jo.marketplace.repository.interfaces.MasUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.jo.marketplace.constant.AppConstants.*;
import static com.jo.marketplace.constant.RoleConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final MasRoleRepository roleRepository;
    private final MasUserRepository userRepository;
    private final MasShopRepository shopRepository;
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
    }

    private void seedRoles() {
        log.info("Checking and seeding default Roles...");

        if (roleRepository.count() == 0) {

            MasRoleEntity adminRole = new MasRoleEntity();
            adminRole.setName(ADMIN);
            adminRole.setDescription(ADMIN_DESC);
            adminRole.setIsSystemDefined(true);
            roleRepository.save(adminRole);

            MasRoleEntity sellerRole = new MasRoleEntity();
            sellerRole.setName(SELLER);
            sellerRole.setDescription(SELLER_DESC);
            sellerRole.setIsSystemDefined(true);
            roleRepository.save(sellerRole);

            MasRoleEntity buyerRole = new MasRoleEntity();
            buyerRole.setName(BUYER);
            buyerRole.setDescription(BUYER_DESC);
            buyerRole.setIsSystemDefined(true);
            roleRepository.save(buyerRole);

            log.info("Default Roles seeded successfully!");
        } else {
            log.info("Roles already exist. Skipping seed.");
        }
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