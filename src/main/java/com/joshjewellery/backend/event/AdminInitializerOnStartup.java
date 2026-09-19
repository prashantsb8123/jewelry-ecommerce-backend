package com.joshjewellery.backend.event;

import com.joshjewellery.backend.constant.RoleType;
import com.joshjewellery.backend.entity.Role;
import com.joshjewellery.backend.entity.User;
import com.joshjewellery.backend.repository.RoleRepository;
import com.joshjewellery.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializerOnStartup {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@jyoshikamillennium.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123456}")
    private String adminPassword;

    @Value("${app.admin.name:Jyoshika Millennium Admin}")
    private String adminName;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeAdminAccount() {
        log.info("Checking single Admin account status...");

        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ROLE_ADMIN).build()));

        // Ensure CUSTOMER role is also present
        roleRepository.findByName(RoleType.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ROLE_CUSTOMER).build()));

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User adminUser = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .fullName(adminName)
                    .phone("+919876543210")
                    .role(adminRole)
                    .isEnabled(true)
                    .isEmailVerified(true)
                    .rewardPoints(0)
                    .build();

            userRepository.save(adminUser);
            log.info("Single Admin account created successfully on startup with email: {}", adminEmail);
        } else {
            log.info("Admin account ({}) already exists. Startup check complete.", adminEmail);
        }
    }
}
