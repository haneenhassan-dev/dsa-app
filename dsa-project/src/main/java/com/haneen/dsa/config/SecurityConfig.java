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
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.addAllowedOrigin("http://localhost:3000");
                    config.addAllowedMethod("*");
                    config.addAllowedHeader("*");
                    return config;
                }))
                .csrf(csrf-> csrf.disable()) // stateless REST API, using JWT
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/","index2.html", "login.html", "/register.html","/error").permitAll()  // allow register/login
                        .requestMatchers("/org/members/**").hasRole("ADMIN")
                        .anyRequest().authenticated()            // require auth for others
                ).exceptionHandling(ex -> ex.authenticationEntryPoint(
                (request, response, authException) -> {
                    response.setStatus(401);
                    response.getWriter().write("Unauthorized");
                }
        ));


        // Place JWT filter before Spring's username/password filter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
