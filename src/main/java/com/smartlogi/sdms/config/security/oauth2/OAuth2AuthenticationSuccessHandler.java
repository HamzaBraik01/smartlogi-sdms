package com.smartlogi.sdms.config.security.oauth2;

import com.smartlogi.sdms.config.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * Handler appelé après une authentification OAuth2 réussie.
 *
 * Génère un JWT interne et redirige l'utilisateur vers le frontend
 * avec le token en paramètre de l'URL.
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.oauth2.authorized-redirect-uri:http://localhost:4200/oauth2/redirect}")
    private String authorizedRedirectUri;

    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            log.debug("La réponse a déjà été envoyée. Impossible de rediriger vers {}", targetUrl);
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }


    protected String determineTargetUrl(Authentication authentication) {
        OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();

        // Générer le JWT interne (identique à l'auth classique)
        String token = jwtTokenProvider.generateTokenFromOAuth2(authentication);

        log.info("JWT généré pour utilisateur OAuth2: {} (rôle: {})",
                principal.getEmail(), principal.getRole());

        // Construire l'URL de redirection avec les paramètres
        return UriComponentsBuilder.fromUriString(authorizedRedirectUri)
                .queryParam("token", token)
                .queryParam("userId", principal.getId())
                .queryParam("email", principal.getEmail())
                .queryParam("role", principal.getRole() != null ? principal.getRole().name() : "CLIENT_EXPEDITEUR")
                .build()
                .toUriString();
    }
}

