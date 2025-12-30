package com.smartlogi.sdms.config.security;

import com.smartlogi.sdms.config.security.oauth2.CustomOAuth2UserService;
import com.smartlogi.sdms.config.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.smartlogi.sdms.config.security.oauth2.OAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;


/**
 * Configuration Spring Security avec authentification hybride:
 * - Authentification classique (email/password) → JWT
 * - Authentification OAuth2 (Google, Facebook, Apple, Okta) → JWT interne
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomUserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    // Composants OAuth2
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2FailureHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler,
                          CustomUserDetailsService userDetailsService,
                          CorsConfigurationSource corsConfigurationSource,
                          CustomOAuth2UserService customOAuth2UserService,
                          OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler,
                          OAuth2AuthenticationFailureHandler oAuth2FailureHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.userDetailsService = userDetailsService;
        this.corsConfigurationSource = corsConfigurationSource;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.oAuth2FailureHandler = oAuth2FailureHandler;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Désactiver CSRF (API stateless JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // Activer CORS avec notre configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // Session stateless (pas de session côté serveur)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Gestion des exceptions d'authentification et d'autorisation
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))

                // Règles d'autorisation des endpoints
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics - Authentification classique
                        .requestMatchers("/auth/**").permitAll()

                        // Endpoints publics - OAuth2
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers("/login/oauth2/**").permitAll()

                        // Endpoints publics - Documentation API
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()

                        // Endpoints publics - Actuator health
                        .requestMatchers("/actuator/health").permitAll()

                        // Endpoints ADMIN uniquement - Gestion rôles et permissions
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // Endpoints MANAGER uniquement - Gestion livreurs
                        .requestMatchers("/api/v1/livreurs/**").hasRole("MANAGER")

                        // Endpoints MANAGER uniquement - Gestion zones
                        .requestMatchers("/api/v1/zones/**").hasRole("MANAGER")

                        // Endpoints MANAGER uniquement - Gestion gestionnaires
                        .requestMatchers("/api/v1/gestionnaires/**").hasRole("MANAGER")

                        // Endpoints MANAGER uniquement - Statistiques
                        .requestMatchers("/api/v1/statistiques/**").hasRole("MANAGER")

                        // Endpoints avec règles spécifiques (vérification au niveau méthode)
                        .requestMatchers("/api/v1/colis/**").authenticated()
                        .requestMatchers("/api/v1/clients/**").authenticated()
                        .requestMatchers("/api/v1/client-expediteurs/**").authenticated()
                        .requestMatchers("/api/v1/destinataires/**").authenticated()
                        .requestMatchers("/api/v1/produits/**").authenticated()
                        .requestMatchers("/api/v1/historique/**").authenticated()

                        // Recherche globale - Authentifié
                        .requestMatchers("/api/v1/search/**").authenticated()

                        // Tout le reste nécessite une authentification
                        .anyRequest().authenticated())

                // ==================== CONFIGURATION OAUTH2 ====================
                .oauth2Login(oauth2 -> oauth2
                        // Service pour charger/créer l'utilisateur
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        // Handler en cas de succès → génère JWT
                        .successHandler(oAuth2SuccessHandler)
                        // Handler en cas d'échec
                        .failureHandler(oAuth2FailureHandler)
                )

                // Provider d'authentification classique
                .authenticationProvider(authenticationProvider())

                // Ajouter le filtre JWT avant le filtre d'authentification par défaut
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

