package com.smartlogi.sdms.security;

import com.smartlogi.sdms.config.security.JwtTokenProvider;
import com.smartlogi.sdms.config.security.CustomUserDetails;
import com.smartlogi.sdms.entity.GestionnaireLogistique;
import com.smartlogi.sdms.entity.enumeration.RoleUtilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires pour JwtTokenProvider.
 * Vérifie la génération, validation et extraction des informations des tokens JWT.
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private static final String TEST_SECRET = "test-secret-key-32-bytes-long-!!";
    private static final long TEST_EXPIRATION = 86400000L; // 24h

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(TEST_SECRET, TEST_EXPIRATION);
    }

    @Test
    @DisplayName("Génération token - Succès avec Authentication valide")
    void generateToken_WithValidAuthentication_ReturnsValidToken() {
        // Given
        Authentication auth = createMockAuthentication("manager@test.com", RoleUtilisateur.GESTIONNAIRE);

        // When
        String token = jwtTokenProvider.generateToken(auth);

        // Then
        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3); // Header.Payload.Signature
    }

    @Test
    @DisplayName("Validation token - Token valide retourne true")
    void validateToken_WithValidToken_ReturnsTrue() {
        // Given
        Authentication auth = createMockAuthentication("manager@test.com", RoleUtilisateur.GESTIONNAIRE);
        String token = jwtTokenProvider.generateToken(auth);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validation token - Token malformé retourne false")
    void validateToken_WithMalformedToken_ReturnsFalse() {
        // Given
        String malformedToken = "invalid.token.format";

        // When
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validation token - Token avec signature invalide retourne false")
    void validateToken_WithInvalidSignature_ReturnsFalse() {
        // Given
        String tamperedToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIn0.tampered_signature";

        // When
        boolean isValid = jwtTokenProvider.validateToken(tamperedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validation token - Token expiré retourne false")
    void validateToken_WithExpiredToken_ReturnsFalse() throws InterruptedException {
        // Given - Provider avec expiration très courte
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(TEST_SECRET, 1L);
        Authentication auth = createMockAuthentication("manager@test.com", RoleUtilisateur.GESTIONNAIRE);
        String token = shortLivedProvider.generateToken(auth);

        // When - Attendre l'expiration
        Thread.sleep(50);
        boolean isValid = shortLivedProvider.validateToken(token);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Extraction username - Retourne l'email correct")
    void getUsername_WithValidToken_ReturnsCorrectEmail() {
        // Given
        String email = "manager@test.com";
        Authentication auth = createMockAuthentication(email, RoleUtilisateur.GESTIONNAIRE);
        String token = jwtTokenProvider.generateToken(auth);

        // When
        String extractedUsername = jwtTokenProvider.getUsername(token);

        // Then
        assertThat(extractedUsername).isEqualTo(email);
    }

    @Test
    @DisplayName("Extraction userId - Retourne l'ID correct")
    void getUserId_WithValidToken_ReturnsCorrectId() {
        // Given
        Authentication auth = createMockAuthentication("manager@test.com", RoleUtilisateur.GESTIONNAIRE);
        String token = jwtTokenProvider.generateToken(auth);

        // When
        String extractedUserId = jwtTokenProvider.getUserId(token);

        // Then
        assertThat(extractedUserId).isEqualTo("test-user-id");
    }

    @Test
    @DisplayName("Extraction roles - Retourne les rôles corrects")
    void getRoles_WithValidToken_ReturnsCorrectRoles() {
        // Given
        Authentication auth = createMockAuthentication("manager@test.com", RoleUtilisateur.GESTIONNAIRE);
        String token = jwtTokenProvider.generateToken(auth);

        // When
        String roles = jwtTokenProvider.getRoles(token);

        // Then
        assertThat(roles).contains("ROLE_MANAGER");
    }

    @Test
    @DisplayName("Expiration - Retourne la valeur configurée")
    void getExpirationMs_ReturnsConfiguredValue() {
        // When
        long expiration = jwtTokenProvider.getExpirationMs();

        // Then
        assertThat(expiration).isEqualTo(TEST_EXPIRATION);
    }

    /**
     * Crée une Authentication mock pour les tests.
     */
    private Authentication createMockAuthentication(String email, RoleUtilisateur role) {
        // Créer un mock Utilisateur avec le rôle correctement défini
        GestionnaireLogistique user = new GestionnaireLogistique();
        user.setId("test-user-id");
        user.setEmail(email);
        user.setNom("Test");
        user.setPrenom("User");
        user.setPassword("password");

        // Créer CustomUserDetails avec le rôle spécifié via une sous-classe anonyme
        CustomUserDetails userDetails = new CustomUserDetails(user) {
            @Override
            public String getId() {
                return "test-user-id";
            }

            @Override
            public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
                String authority = switch (role) {
                    case ADMIN -> "ROLE_ADMIN";
                    case GESTIONNAIRE -> "ROLE_MANAGER";
                    case LIVREUR -> "ROLE_DELIVERY";
                    case CLIENT_EXPEDITEUR -> "ROLE_CLIENT";
                    case DESTINATAIRE -> "ROLE_VIEWER";
                };
                return java.util.Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority(authority)
                );
            }
        };

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}

