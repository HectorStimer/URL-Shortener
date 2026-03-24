package com.hector.encurtadorlink.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Obrigatório para o Swagger e POSTs de teste
                .authorizeHttpRequests(auth -> auth
                        // libera o redirecionamento (ex: localhost:8080/abc)
                        .requestMatchers("/{shortCode}").permitAll()

                        // libera TODA a infraestrutura do Swagger UI
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // libera a rota de erro para você ver a causa real (ex: erro de banco)
                        .requestMatchers("/error").permitAll()

                        // o resto exige login
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}


