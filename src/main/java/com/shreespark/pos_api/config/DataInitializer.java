package com.shreespark.pos_api.config;

import com.shreespark.pos_api.admin.entity.Admin;
import com.shreespark.pos_api.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!adminRepository.existsByEmail("admin@shreespark.com")) {
            adminRepository.save(Admin.builder()
                    .name("Super Admin")
                    .email("admin@shreespark.com")
                    .password(passwordEncoder.encode("Admin@1234"))
                    .build());
            log.info("Super admin seeded: admin@shreespark.com / Admin@1234");
        }
    }
}
