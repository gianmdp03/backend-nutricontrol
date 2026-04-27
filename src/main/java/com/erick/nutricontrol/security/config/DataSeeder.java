package com.erick.nutricontrol.security.config;

import com.erick.nutricontrol.security.user.Enum.Role;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.security.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@Configuration
public class DataSeeder {
    private final UserRepository userRepository;

    @Value("${nutricontrol.initial-user.name}")
    private String name;

    @Value("${nutricontrol.initial-user.lastname}")
    private String lastname;

    @Value("${nutricontrol.initial-user.email}")
    private String email;

    @Value("${nutricontrol.initial-user.username}")
    private String username;

    @Value("${nutricontrol.initial-user.password}")
    private String password;

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = email;

            userRepository.findByEmail(adminEmail).ifPresentOrElse(
                    (existingAdmin) -> {
                        existingAdmin.setName(name);
                        existingAdmin.setLastname(lastname);
                        existingAdmin.setUsername(username);
                        existingAdmin.setPassword(passwordEncoder.encode(password));
                        existingAdmin.setRole(Role.ROLE_ADMIN);

                        userRepository.save(existingAdmin);
                        System.out.println("DATOS DE ADMINISTRADOR ACTUALIZADOS AUTOMÁTICAMENTE");
                    },
                    () -> {
                        User admin = new User();
                        admin.setEmail(adminEmail);
                        admin.setName(name);
                        admin.setLastname(lastname);
                        admin.setUsername(username);
                        admin.setPassword(passwordEncoder.encode(password));
                        admin.setRole(Role.ROLE_ADMIN);

                        userRepository.save(admin);
                        System.out.println("ADMINISTRADOR INICIAL CREADO");
                    }
            );
        };
    }
}
