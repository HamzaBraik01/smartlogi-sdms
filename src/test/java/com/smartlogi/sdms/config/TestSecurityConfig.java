package com.smartlogi.sdms.config;

import com.smartlogi.sdms.config.security.CustomAccessDeniedHandler;
import com.smartlogi.sdms.config.security.CustomUserDetailsService;
import com.smartlogi.sdms.config.security.JwtAuthenticationEntryPoint;
import com.smartlogi.sdms.config.security.JwtAuthenticationFilter;
import com.smartlogi.sdms.config.security.JwtTokenProvider;
import com.smartlogi.sdms.config.security.oauth2.CustomOAuth2UserService;
import com.smartlogi.sdms.config.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.smartlogi.sdms.config.security.oauth2.OAuth2AuthenticationSuccessHandler;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de sécurité simplifiée pour les tests unitaires.
 * Désactive CSRF et permet toutes les requêtes.
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    @Primary
    public JwtTokenProvider jwtTokenProvider() {
        return Mockito.mock(JwtTokenProvider.class);
    }

    @Bean
    @Primary
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                                            CustomUserDetailsService userDetailsService) {
        return Mockito.mock(JwtAuthenticationFilter.class);
    }

    @Bean
    @Primary
    public CustomUserDetailsService customUserDetailsService() {
        return Mockito.mock(CustomUserDetailsService.class);
    }

    @Bean
    @Primary
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    @Primary
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    @Primary
    public CustomOAuth2UserService customOAuth2UserService() {
        return Mockito.mock(CustomOAuth2UserService.class);
    }

    @Bean
    @Primary
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return Mockito.mock(OAuth2AuthenticationSuccessHandler.class);
    }

    @Bean
    @Primary
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return Mockito.mock(OAuth2AuthenticationFailureHandler.class);
    }

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public AuthenticationManager authenticationManager() {
        return Mockito.mock(AuthenticationManager.class);
    }
}

