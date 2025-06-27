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
import org.springframework.context.annotation.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("nkiem347@gmail.com").isEmpty()) {
                var roles = new HashSet<String>();
                User user = User.builder()
                        .passwordHash(passwordEncoder.encode("123456"))
                        .email("nkiem347@gmail.com")
                        .role(UserRole.ADMIN)
                        .build();
                userRepository.save(user);
                log.warn("admin user has bean create with default password: admin, please change it");
            }
        };
    }

}


