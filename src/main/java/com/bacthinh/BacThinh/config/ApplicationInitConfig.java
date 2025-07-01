package com.bacthinh.BacThinh.config;

import com.bacthinh.BacThinh.entity.User;
import com.bacthinh.BacThinh.entity.UserRole;
import com.bacthinh.BacThinh.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("nkiem347@gmail.com").isEmpty()) {
                User admin = User.builder()
                        .passwordHash(passwordEncoder.encode("123456"))
                        .email("nkiem347@gmail.com")
                        .role(UserRole.ADMIN)
                        .build();
                userRepository.save(admin);
                log.warn("Admin user has been created with default password: 123456, please change it.");
            }

            if (userRepository.count() < 100) {
                for (int i = 1; i <= 100; i++) {
                    String email = "user" + i + "@example.com";
                    if (userRepository.findByEmail(email).isEmpty()) {
                        User user = User.builder()
                                .email(email)
                                .passwordHash(passwordEncoder.encode("123456"))
                                .role(UserRole.GIAO_DAN)
                                .build();
                        userRepository.save(user);
                    }
                }
                log.info("100 dummy users have been generated.");
            }
        };
    }


}


