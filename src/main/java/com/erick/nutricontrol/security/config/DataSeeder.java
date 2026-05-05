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

    @Value("${nutricontrol.initial-admin.name}")
    private String name;

    @Value("${nutricontrol.initial-admin.lastname}")
    private String lastname;

    @Value("${nutricontrol.initial-admin.email}")
    private String email;

    @Value("${nutricontrol.initial-admin.username}")
    private String username;

    @Value("${nutricontrol.initial-admin.password}")
    private String password;

    @Value("${nutricontrol.initial-user.name}")
    private String userName;

    @Value("${nutricontrol.initial-user.lastname}")
    private String userLastname;

    @Value("${nutricontrol.initial-user.email}")
    private String userEmail;

    @Value("${nutricontrol.initial-user.username}")
    private String userUsername;

    @Value("${nutricontrol.initial-user.password}")
    private String userPassword;

    @Value("${nutricontrol.initial-admin.timezone}")
    private String timezone;

    @Value("${nutricontrol.initial-user.timezone}")
    private String userTimezone;

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = email;
            String aux = userEmail;
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

            userRepository.findByEmail(userEmail).ifPresentOrElse(
                    (existingUser) -> {
                        existingUser.setName(userName);
                        existingUser.setLastname(userLastname);
                        existingUser.setUsername(userUsername);
                        existingUser.setPassword(passwordEncoder.encode(userPassword));
                        existingUser.setRole(Role.ROLE_PATIENT);

                        userRepository.save(existingUser);
                        System.out.println("DATOS DE PACIENTE ACTUALIZADOS AUTOMÁTICAMENTE");
                    },
                    () -> {
                        User user = new User();
                        user.setEmail(userEmail);
                        user.setName(userName);
                        user.setLastname(userLastname);
                        user.setUsername(userUsername);
                        user.setPassword(passwordEncoder.encode(userPassword));
                        user.setRole(Role.ROLE_PATIENT);

                        userRepository.save(user);
                        System.out.println("PACIENTE INICIAL CREADO");
                    }
            );
        };
    }
}
