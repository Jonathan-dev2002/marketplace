package com.jo.marketplace.config;

import com.jo.marketplace.entity.MasRoleEntity;
import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.entity.MasUserShopRoleEntity;
import com.jo.marketplace.repository.interfaces.MasRoleRepository;
import com.jo.marketplace.repository.interfaces.MasUserRepository;
import com.jo.marketplace.repository.interfaces.MasUserShopRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.jo.marketplace.constant.AppConstants.PLATFORM_SHOP_ID;
import static com.jo.marketplace.constant.RoleConstants.ADMIN;

@Order(3)
@Component
@RequiredArgsConstructor
public class AdminAssignmentSeeder implements CommandLineRunner {

    private final MasRoleRepository roleRepository;
    private final MasUserRepository userRepository;
    private final MasUserShopRoleRepository userShopRoleRepository;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Override
    @Transactional
    public void run(String... args) {
        MasUserEntity systemAdmin = userRepository.findByUsername(adminUsername).orElse(null);
        if (systemAdmin == null || userShopRoleRepository.existsByUserIdAndShopId(systemAdmin.getId(), PLATFORM_SHOP_ID)) {
            return;
        }

        MasRoleEntity adminRole = roleRepository.findByNameAndIsSystemDefinedTrue(ADMIN)
                .orElseThrow(() -> new IllegalStateException("Missing system role: " + ADMIN));

        MasUserShopRoleEntity adminAssignment = new MasUserShopRoleEntity();
        adminAssignment.setUserId(systemAdmin.getId());
        adminAssignment.setShopId(PLATFORM_SHOP_ID);
        adminAssignment.setRoleId(adminRole.getId());
        userShopRoleRepository.save(adminAssignment);
    }
}
