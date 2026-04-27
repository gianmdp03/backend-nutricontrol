package com.erick.nutricontrol.security.config;

import com.erick.nutricontrol.security.user.Enum.Role;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.security.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@Configuration
public class DataSeeder {
    private final UserRepository userRepository;

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@admin.com";

            userRepository.findByEmail(adminEmail).ifPresentOrElse(
                    (existingAdmin) -> {
                        existingAdmin.setName("Admin");
                        existingAdmin.setLastname("Admin");
                        existingAdmin.setUsername("admin2003");
                        existingAdmin.setPassword(passwordEncoder.encode("123456"));
                        existingAdmin.setRole(Role.ROLE_ADMIN);

                        userRepository.save(existingAdmin);
                        System.out.println("DATOS DE ADMINISTRADOR ACTUALIZADOS AUTOMÁTICAMENTE");
                    },
                    () -> {
                        User admin = new User();
                        admin.setEmail(adminEmail);
                        admin.setName("Admin");
                        admin.setLastname("Admin");
                        admin.setUsername("admin2003");
                        admin.setPassword(passwordEncoder.encode("123456"));
                        admin.setRole(Role.ROLE_ADMIN);

                        userRepository.save(admin);
                        System.out.println("ADMINISTRADOR INICIAL CREADO");
                    }
            );
        };
    }
}
