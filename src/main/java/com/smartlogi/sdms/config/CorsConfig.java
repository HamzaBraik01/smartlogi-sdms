package com.smartlogi.sdms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;


@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:4200",  // Angular
            "http://localhost:3000",  // React
            "http://localhost:8080"   // Application locale
    );

    private static final List<String> ALLOWED_HEADERS = Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "X-Requested-With",
            "Cache-Control"
    );

    private static final List<String> ALLOWED_METHODS = Arrays.asList(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "PATCH",
            "OPTIONS"
    );

    private static final List<String> EXPOSED_HEADERS = Arrays.asList(
            "Authorization",
            "Content-Disposition"
    );

    private static final long MAX_AGE = 3600L;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(ALLOWED_ORIGINS.toArray(new String[0]))
                .allowedMethods(ALLOWED_METHODS.toArray(new String[0]))
                .allowedHeaders(ALLOWED_HEADERS.toArray(new String[0]))
                .exposedHeaders(EXPOSED_HEADERS.toArray(new String[0]))
                .allowCredentials(true)
                .maxAge(MAX_AGE);
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(ALLOWED_ORIGINS);

        configuration.setAllowedMethods(ALLOWED_METHODS);

        configuration.setAllowedHeaders(ALLOWED_HEADERS);

        configuration.setExposedHeaders(EXPOSED_HEADERS);

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(MAX_AGE);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

