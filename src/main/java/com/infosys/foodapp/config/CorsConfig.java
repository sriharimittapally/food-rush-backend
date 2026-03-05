package com.infosys.foodapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow Angular dev server
        config.setAllowedOrigins(List.of(
                "http://localhost:4200"
        ));

        // Allow all HTTP methods
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT",
                "PATCH", "DELETE", "OPTIONS"
        ));

        // Allow all headers including Authorization
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"
        ));

        // Allow cookies / auth headers
        config.setAllowCredentials(true);

        // Cache preflight for 1 hour
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        // Apply to all routes
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}