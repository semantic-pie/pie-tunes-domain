package com.apipietunes.clients.config;

import com.apipietunes.clients.repositories.UserH2Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    private final String[] WHITE_LIST_URLs = {
            "/api/v1/auth/signup",
            "/api/v1/auth/login",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    };

    @Value("${pie-tunes-ui.server.url}")
    private List<String> FRONTEND_URLs;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserH2Repository userH2Repository) {
        return username -> userH2Repository.findUserSqlByEmail(username)
                .map(foundUser -> {
                    List<GrantedAuthority> authorities = new ArrayList<>(Collections.emptyList());
                    authorities.add((GrantedAuthority) () -> foundUser.getRole().name());
                    return new User(foundUser.getEmail(), foundUser.getPassword(), authorities);
                });
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService) {

        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }

    @Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                        .pathMatchers(WHITE_LIST_URLs).permitAll())
                .authorizeExchange(authorize -> authorize
                        .anyExchange().authenticated())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
//                .exceptionHandling(exceptionHandling -> exceptionHandling
//                                .authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint("/form-login"))
                .addFilterBefore(jwtAuthenticationFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(FRONTEND_URLs);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Access-Control-Allow-Origin", "Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtFilterRequest jwtAuthenticationFilter() {
        return new JwtFilterRequest();
    }
}
