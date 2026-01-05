package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.config.security.JwtTokenProvider;
import com.smartlogi.sdms.config.security.oauth2.OAuth2UserPrincipal;
import com.smartlogi.sdms.dto.auth.LoginResponse;
import com.smartlogi.sdms.entity.Utilisateur;
import com.smartlogi.sdms.entity.enumeration.AuthProvider;
import com.smartlogi.sdms.repository.UtilisateurRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur pour les endpoints OAuth2.
 *
 * Note: Les endpoints principaux OAuth2 sont gérés automatiquement par Spring Security:
 * - GET /oauth2/authorization/{provider} - Initie la connexion OAuth2
 * - GET /login/oauth2/code/{provider} - Callback OAuth2 (géré automatiquement)
 */
@RestController
@RequestMapping("/oauth2")
@Tag(name = "OAuth2", description = "API pour l'authentification OAuth2")
public class OAuth2Controller {

    private final UtilisateurRepository utilisateurRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public OAuth2Controller(UtilisateurRepository utilisateurRepository,
                           JwtTokenProvider jwtTokenProvider) {
        this.utilisateurRepository = utilisateurRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Operation(summary = "Liste des providers OAuth2 disponibles",
            description = "Retourne la liste des fournisseurs d'authentification OAuth2 supportés")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des providers")
    })
    @GetMapping("/providers")
    public ResponseEntity<Map<String, Object>> getAvailableProviders() {
        List<Map<String, String>> providers = Arrays.stream(AuthProvider.values())
                .filter(p -> p != AuthProvider.LOCAL)
                .map(p -> Map.of(
                        "name", p.name(),
                        "displayName", getDisplayName(p),
                        "authorizationUrl", "/oauth2/authorization/" + p.name().toLowerCase()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "providers", providers,
                "message", "Utilisez l'URL d'autorisation pour initier la connexion OAuth2"
        ));
    }

    @Operation(summary = "Informations sur le flux OAuth2",
            description = "Retourne des informations sur l'utilisation de l'authentification OAuth2")
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getOAuth2Info() {
        return ResponseEntity.ok(Map.of(
                "description", "Authentification OAuth2 pour SmartLogi SDMS",
                "flow", "Authorization Code Flow",
                "endpoints", Map.of(
                        "authorize", "/oauth2/authorization/{provider}",
                        "callback", "/login/oauth2/code/{provider}",
                        "redirectAfterSuccess", "Configuré dans app.oauth2.authorized-redirect-uri"
                ),
                "supportedProviders", Arrays.stream(AuthProvider.values())
                        .filter(p -> p != AuthProvider.LOCAL)
                        .map(AuthProvider::name)
                        .collect(Collectors.toList()),
                "tokenFormat", "JWT Bearer Token (identique à l'auth classique)"
        ));
    }

    @Operation(summary = "Profil utilisateur OAuth2",
            description = "Retourne le profil de l'utilisateur authentifié via OAuth2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil utilisateur"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentOAuth2User(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Non authentifié",
                    "message", "Vous devez être connecté pour accéder à cette ressource"
            ));
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2UserPrincipal oAuth2Principal) {
            Utilisateur utilisateur = oAuth2Principal.getUtilisateur();
            return ResponseEntity.ok(Map.of(
                    "userId", utilisateur.getId(),
                    "email", utilisateur.getEmail(),
                    "nom", utilisateur.getNom() != null ? utilisateur.getNom() : "",
                    "prenom", utilisateur.getPrenom() != null ? utilisateur.getPrenom() : "",
                    "role", utilisateur.getRole() != null ? utilisateur.getRole().name() : "CLIENT_EXPEDITEUR",
                    "provider", utilisateur.getProvider().name(),
                    "imageUrl", utilisateur.getImageUrl() != null ? utilisateur.getImageUrl() : "",
                    "emailVerified", utilisateur.getEmailVerified()
            ));
        }

        return ResponseEntity.ok(Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities()
        ));
    }

    /**
     * Retourne le nom d'affichage pour chaque provider.
     */
    private String getDisplayName(AuthProvider provider) {
        return switch (provider) {
            case GOOGLE -> "Google";
            case FACEBOOK -> "Facebook";
            case APPLE -> "Apple";
            case OKTA -> "Okta";
            case LOCAL -> "Email/Mot de passe";
        };
    }
}

