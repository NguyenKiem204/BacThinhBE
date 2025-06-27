package com.bacthinh.BacThinh.config;

import com.bacthinh.BacThinh.filter.JwtAuthenticationFilter;
import com.bacthinh.BacThinh.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserService userService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @SuppressWarnings("deprecation")
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService((UserDetailsService) userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html","/swagger-ui/**","/v3/api-docs/**","/v3/api-docs.yaml").permitAll()
                        .requestMatchers("/api/auth/logout", "/api/auth/logout-all", "/api/auth/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/user/create-user").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/user/admin-update/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/user/self-update/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/user/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/user").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/user/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/user/search").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/residents").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/residents/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/residents/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/residents/**").authenticated()
                        .requestMatchers("/api/v1/audit/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:[*]",
                "http://127.0.0.1:[*]",
                "https://harmless-right-chipmunk.ngrok-free.app",
                "https://*.ngrok-free.app",
                "https://*.ngrok.io"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}