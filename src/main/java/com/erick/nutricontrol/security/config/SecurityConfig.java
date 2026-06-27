package com.erick.nutricontrol.security.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(
            auth ->
                auth
                    // APPOINTMENTS
                    .requestMatchers("/api/appointments/admin", "/api/appointments/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/appointments/**")
                    .hasAnyRole("ADMIN", "PATIENT")
                    // MEDICALRECORD
                    .requestMatchers("/api/medical-records")
                    .hasRole("PATIENT")
                    .requestMatchers("/api/medical-records/admin")
                    .hasRole("ADMIN")
                    // PAYMENT
                    .requestMatchers("/api/payments/webhook")
                    .permitAll() // PayPal no tiene token, debe entrar libre
                    .requestMatchers("/api/payments/**")
                    .hasAnyRole("PATIENT", "ADMIN") // Tu frontend sí tiene token
                    // SCHEDULEEXCEPTION
                    .requestMatchers("/api/schedule-exceptions/**")
                    .hasRole("ADMIN")
                    // SCHEDULERULE
                    .requestMatchers("/api/schedule-rules/**")
                    .hasRole("ADMIN")
                    // SERVICE
                    .requestMatchers("/api/services/public", "/api/services/public/**")
                    .permitAll()
                    .requestMatchers("/api/services/**")
                    .hasRole("ADMIN")
                    // NOTIFICATION
                    .requestMatchers("/api/notifications/**")
                    .hasAnyRole("PATIENT", "ADMIN")
                    // REVIEW
                    .requestMatchers("/api/reviews")
                    .hasRole("PATIENT")
                    .requestMatchers("/api/reviews/admin")
                    .hasRole("ADMIN")
                    // AUTHENTICATION
                    .requestMatchers("/api/auth/logged/**")
                    .hasAnyRole("ADMIN", "PATIENT")
                    .requestMatchers("/api/auth/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/auth/**", "/ws/**")
                    .permitAll()
                    .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/oauth")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        List.of(
            "http://localhost:4200",
            "http://localhost:50093",
            "http://localhost:3000",
            "https://tumedicord.vercel.app"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
