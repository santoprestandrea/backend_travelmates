package com.santoprestandrea_s00007624.backend_travelmates.config;

import com.santoprestandrea_s00007624.backend_travelmates.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enables @PreAuthorize, @Secured, etc.
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * MAIN SECURITY CONFIGURATION
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (not needed for stateless REST APIs)
                .csrf(csrf -> csrf.disable())

                // Enable CORS (to allow requests from frontend)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // STATELESS: don't create HTTP sessions (we use JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Disable form login (not needed for REST APIs)
                .formLogin(form -> form.disable())

                // Disable HTTP Basic (we use JWT)
                .httpBasic(basic -> basic.disable())

                // AUTHORIZATIONS: who can access what
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC endpoints (accessible without login)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/h2-console/**").permitAll() // Dev only

                        // All other endpoints REQUIRE AUTHENTICATION
                        .anyRequest().authenticated())

                // Add JWT filter BEFORE the standard Spring filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Allow iframes for H2 Console (dev only)
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    /**
     * CORS CONFIGURATION
     *
     * Allows the frontend (React/Angular/Vue) to make requests to the backend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed origins (where the frontend runs)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000", // React
                "http://localhost:4200", // Angular
                "http://localhost:5173" // Vite
        ));

        // Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Allowed headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept"));

        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * PASSWORD ENCODER
     *
     * Uses BCrypt to encrypt passwords securely.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}