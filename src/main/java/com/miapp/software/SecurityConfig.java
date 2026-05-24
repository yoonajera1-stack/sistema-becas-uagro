package com.miapp.software;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * ✅ CORRECCIÓN: Spring Security estaba bloqueando /admin y otras rutas
     * porque por defecto requiere autenticación propia.
     *
     * Este sistema usa su propio login con sesiones HTTP (AuthController),
     * por lo que le decimos a Spring Security que permita TODAS las rutas
     * sin interferir, y desactivamos su login y logout propios.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Permitir todas las rutas
                )
                .formLogin(form -> form.disable())   // Desactivar su login propio
                .logout(logout -> logout.disable())  // Desactivar su logout propio
                .csrf(csrf -> csrf.disable());       // Desactivar CSRF (usamos sesiones propias)

        return http.build();
    }
}