package com.agro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.agro.security.JwtFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth

                // 🔓 AUTH (NO TOKEN REQUIRED)
                .requestMatchers(
                    "/api/auth/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                
                .requestMatchers(HttpMethod.GET, "/api/crops/**").hasAnyRole("ADMIN", "FARMER", "BUYER")
                // 👑 ADMIN
                .requestMatchers(HttpMethod.GET, "/api/crops/all").hasRole("ADMIN")

                // 👨‍🌾 FARMER
                .requestMatchers(HttpMethod.POST, "/api/crops/add").hasRole("FARMER")
                .requestMatchers(HttpMethod.GET, "/api/crops/my-crops").hasRole("FARMER")

                // 👤 BUYER
             // BUYER
                .requestMatchers("/api/orders/buyer").hasRole("BUYER")
                .requestMatchers("/api/orders/place").hasRole("BUYER")
                .requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasAnyRole("BUYER", "FARMER", "ADMIN")
                .requestMatchers("/api/orders/buyer").hasRole("BUYER")
                .requestMatchers("/api/orders/farmer").hasRole("FARMER")
                .requestMatchers("/api/orders/**").authenticated()

                // FARMER
                .requestMatchers("/api/orders/farmer").hasRole("FARMER")
                .requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasRole("FARMER")

                .requestMatchers("/api/orders/**").hasRole("BUYER").requestMatchers("/api/buyer/**").hasRole("BUYER")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // 🌾 SHARED
                .requestMatchers("/api/crops/**").hasAnyRole("ADMIN", "FARMER")
                .requestMatchers("/api/market-prices/**").authenticated()

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ✅ Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
