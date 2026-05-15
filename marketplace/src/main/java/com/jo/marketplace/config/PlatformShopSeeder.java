package com.jo.marketplace.config;

import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.repository.interfaces.MasShopRepository;
import com.jo.marketplace.repository.interfaces.MasUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.jo.marketplace.constant.AppConstants.PLATFORM_SHOP_DESC;
import static com.jo.marketplace.constant.AppConstants.PLATFORM_SHOP_ID;
import static com.jo.marketplace.constant.AppConstants.PLATFORM_SHOP_NAME;
import static com.jo.marketplace.constant.AppConstants.PLATFORM_SHOP_SLUG;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class PlatformShopSeeder implements CommandLineRunner {

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
    public void run(String... args) {
        if (shopRepository.existsById(PLATFORM_SHOP_ID)) {
            return;
        }

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
