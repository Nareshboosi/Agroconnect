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
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth

                // 🔓 Public
                .requestMatchers("/api/auth/**").permitAll()

                // 🧾 Razorpay payment (BUYER ONLY)
                .requestMatchers(HttpMethod.POST, "/api/orders/*/pay").hasRole("BUYER")

                // 🛒 Buyer
                .requestMatchers("/api/orders/place").hasRole("BUYER")
                .requestMatchers("/api/orders/buyer").hasRole("BUYER")
                .requestMatchers(HttpMethod.POST, "/api/orders/verify")
                .hasRole("BUYER")
                .requestMatchers(HttpMethod.PUT, "/api/orders/*/refund-request")
                .hasRole("BUYER")


                // 👨‍🌾 Farmer
                .requestMatchers("/api/orders/farmer").hasRole("FARMER")
                .requestMatchers(HttpMethod.POST, "/api/crops/add").hasRole("FARMER")
                .requestMatchers(HttpMethod.GET, "/api/crops/my-crops").hasRole("FARMER")

                // 👑 Admin
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/orders").hasRole("ADMIN")

                // 🌾 Crops (shared read)
                .requestMatchers(HttpMethod.GET, "/api/crops/**")
                    .hasAnyRole("ADMIN", "FARMER", "BUYER")
                    
                    .requestMatchers(HttpMethod.PUT, "/api/orders/*/refund-request")
                    .hasRole("BUYER")

                    .requestMatchers(HttpMethod.POST, "/api/admin/refund/*")
                    .hasRole("ADMIN")


                // 🔐 Everything else
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

