package com.smartlogi.sdms.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.sdms.dto.auth.LoginRequest;
import com.smartlogi.sdms.entity.GestionnaireLogistique;
import com.smartlogi.sdms.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour l'authentification.
 * Vérifie le flux complet de connexion et l'accès sécurisé aux endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_EMAIL = "test-manager@smartlogi.com";
    private static final String TEST_PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        // Nettoyer et créer un utilisateur de test
        utilisateurRepository.findByEmail(TEST_EMAIL).ifPresent(utilisateurRepository::delete);

        GestionnaireLogistique manager = new GestionnaireLogistique();
        manager.setEmail(TEST_EMAIL);
        manager.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        manager.setNom("Test");
        manager.setPrenom("Manager");
        manager.setTelephone("0600000000");
        utilisateurRepository.save(manager);
    }

    @Nested
    @DisplayName("Tests d'authentification /auth/login")
    class LoginTests {

        @Test
        @DisplayName("Login avec credentials valides - Retourne token JWT")
        void login_WithValidCredentials_ReturnsToken() throws Exception {
            LoginRequest request = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").isNumber())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.role").value("GESTIONNAIRE"));
        }

        @Test
        @DisplayName("Login avec mot de passe incorrect - Retourne 401")
        void login_WithWrongPassword_Returns401() throws Exception {
            LoginRequest request = new LoginRequest(TEST_EMAIL, "wrongpassword");

            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email ou mot de passe incorrect"));
        }

        @Test
        @DisplayName("Login avec email inexistant - Retourne 401")
        void login_WithNonExistentEmail_Returns401() throws Exception {
            LoginRequest request = new LoginRequest("unknown@test.com", TEST_PASSWORD);

            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Login avec email vide - Retourne 400")
        void login_WithEmptyEmail_Returns400() throws Exception {
            LoginRequest request = new LoginRequest("", TEST_PASSWORD);

            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Tests d'accès aux endpoints protégés")
    class ProtectedEndpointsTests {

        @Test
        @DisplayName("Accès sans token - Retourne 401")
        void accessProtectedEndpoint_WithoutToken_Returns401() throws Exception {
            mockMvc.perform(get("/api/v1/colis"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("Accès avec token invalide - Retourne 401")
        void accessProtectedEndpoint_WithInvalidToken_Returns401() throws Exception {
            mockMvc.perform(get("/api/v1/colis")
                    .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Accès avec token valide - Retourne 200")
        void accessProtectedEndpoint_WithValidToken_Returns200() throws Exception {
            // D'abord, obtenir un token valide
            String token = obtainValidToken();

            mockMvc.perform(get("/api/v1/colis")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Accès aux endpoints publics sans token - OK")
        void accessPublicEndpoint_WithoutToken_Returns200() throws Exception {
            mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests du endpoint /auth/me")
    class CurrentUserTests {

        @Test
        @DisplayName("GET /auth/me avec token valide - Retourne infos utilisateur")
        void getCurrentUser_WithValidToken_ReturnsUserInfo() throws Exception {
            String token = obtainValidToken();

            mockMvc.perform(get("/auth/me")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.nom").value("Test"))
                .andExpect(jsonPath("$.prenom").value("Manager"))
                .andExpect(jsonPath("$.role").value("GESTIONNAIRE"));
        }

        @Test
        @DisplayName("GET /auth/me sans token - Retourne 401")
        void getCurrentUser_WithoutToken_Returns401() throws Exception {
            mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Tests de validation de token")
    class TokenValidationTests {

        @Test
        @DisplayName("POST /auth/validate avec token valide - Retourne valid: true")
        void validateToken_WithValidToken_ReturnsValid() throws Exception {
            String token = obtainValidToken();

            mockMvc.perform(post("/auth/validate")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
        }

        @Test
        @DisplayName("POST /auth/validate avec token invalide - Retourne valid: false")
        void validateToken_WithInvalidToken_ReturnsInvalid() throws Exception {
            mockMvc.perform(post("/auth/validate")
                    .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false));
        }
    }

    /**
     * Utilitaire pour obtenir un token JWT valide.
     */
    private String obtainValidToken() throws Exception {
        LoginRequest request = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseBody).get("token").asText();
    }
}

