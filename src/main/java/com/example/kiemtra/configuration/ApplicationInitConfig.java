package com.example.kiemtra.configuration;

import com.example.kiemtra.constant.PredefinedRole;
import com.example.kiemtra.entity.Role;
import com.example.kiemtra.entity.User;
import com.example.kiemtra.entity.UserRole;
import com.example.kiemtra.repository.RoleRepository;
import com.example.kiemtra.repository.UserRepository;
import com.example.kiemtra.repository.UserRoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    @ConditionalOnProperty(prefix = "lms", value = "init-data", havingValue = "true")
    ApplicationRunner applicationRunner(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository) {

        return args -> {
            // Tạo role USER nếu chưa có
            roleRepository.findByName(PredefinedRole.USER_ROLE)
                    .orElseGet(() -> roleRepository.save(
                            Role.builder().name(PredefinedRole.USER_ROLE).build()
                    ));

            // Tạo role ADMIN nếu chưa có
            Role adminRole = roleRepository.findByName(PredefinedRole.ADMIN_ROLE)
                    .orElseGet(() -> roleRepository.save(
                            Role.builder().name(PredefinedRole.ADMIN_ROLE).build()
                    ));

            // Tạo user admin nếu chưa có
            if (userRepository.findByUserName(ADMIN_USER_NAME).isEmpty()) {
                User user = User.builder()
                        .fullName(ADMIN_USER_NAME)
                        .userName(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .phoneNumber("0000000000")
                        .build();
                userRepository.save(user);

                // Gán role ADMIN cho user admin
                UserRole userRole = UserRole.builder()
                        .userId(user.getUserId())
                        .roleId(adminRole.getId())
                        .build();
                userRoleRepository.save(userRole);

                log.warn("Admin user has been created with default password: admin, please change it!");
            }

            log.info("Application initialization completed ..... ✅");
        };
    }
}
