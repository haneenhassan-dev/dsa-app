package com.haneen.dsa.config;

import com.haneen.dsa.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf-> csrf.disable()) // stateless REST API, using JWT
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/", "/index.html", "/login.html", "/register.html").permitAll()  // allow register/login
                        .anyRequest().authenticated()            // require auth for others
                );

        // Place JWT filter before Spring's username/password filter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
