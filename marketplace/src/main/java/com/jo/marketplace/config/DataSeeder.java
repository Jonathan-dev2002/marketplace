package com.jo.marketplace.config;

import com.jo.marketplace.entity.MasRoleEntity;
import com.jo.marketplace.repository.interfaces.MasRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final MasRoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking and seeding default Roles...");

        // 1. ตรวจสอบว่ามี Role ADMIN ในระบบหรือยัง
        if (roleRepository.count() == 0) {

            // สร้าง Role: ADMIN (ผู้ดูแลระบบกลาง)
            MasRoleEntity adminRole = new MasRoleEntity();
            adminRole.setName("ADMIN");
            adminRole.setDescription("ผู้ดูแลระบบสูงสุดของแพลตฟอร์ม");
            adminRole.setIsSystemDefined(true);
            roleRepository.save(adminRole);

            // สร้าง Role: SELLER (เจ้าของร้านค้า)
            MasRoleEntity sellerRole = new MasRoleEntity();
            sellerRole.setName("SELLER");
            sellerRole.setDescription("เจ้าของร้านค้า");
            sellerRole.setIsSystemDefined(true);
            roleRepository.save(sellerRole);

            // สร้าง Role: BUYER (ผู้ซื้อทั่วไป)
            MasRoleEntity buyerRole = new MasRoleEntity();
            buyerRole.setName("BUYER");
            buyerRole.setDescription("ผู้ใช้งาน/ผู้ซื้อทั่วไป");
            buyerRole.setIsSystemDefined(true);
            roleRepository.save(buyerRole);

            log.info("Default Roles seeded successfully!");
        } else {
            log.info("Roles already exist. Skipping seed.");
        }
    }
}