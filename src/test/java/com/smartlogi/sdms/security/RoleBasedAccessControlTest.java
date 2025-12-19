package com.smartlogi.sdms.security;

import com.smartlogi.sdms.config.security.JwtTokenProvider;
import com.smartlogi.sdms.config.security.CustomUserDetails;
import com.smartlogi.sdms.entity.ClientExpediteur;
import com.smartlogi.sdms.entity.GestionnaireLogistique;
import com.smartlogi.sdms.entity.Livreur;
import com.smartlogi.sdms.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour le contrôle d'accès basé sur les rôles.
 * Vérifie que chaque rôle a accès uniquement aux ressources autorisées.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RoleBasedAccessControlTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String managerToken;
    private String deliveryToken;
    private String clientToken;

    @BeforeEach
    void setUp() {
        // Créer un MANAGER
        GestionnaireLogistique manager = new GestionnaireLogistique();
        manager.setEmail("manager@test.com");
        manager.setPassword(passwordEncoder.encode("password"));
        manager.setNom("Manager");
        manager.setPrenom("Test");
        utilisateurRepository.save(manager);
        managerToken = generateTokenForUser(manager);

        // Créer un LIVREUR
        Livreur livreur = new Livreur();
        livreur.setEmail("livreur@test.com");
        livreur.setPassword(passwordEncoder.encode("password"));
        livreur.setNom("Livreur");
        livreur.setPrenom("Test");
        livreur.setVehicule("Camionnette");
        utilisateurRepository.save(livreur);
        deliveryToken = generateTokenForUser(livreur);

        // Créer un CLIENT
        ClientExpediteur client = new ClientExpediteur();
        client.setEmail("client@test.com");
        client.setPassword(passwordEncoder.encode("password"));
        client.setNom("Client");
        client.setPrenom("Test");
        client.setAdresse("123 Rue Test");
        utilisateurRepository.save(client);
        clientToken = generateTokenForUser(client);
    }

    private String generateTokenForUser(com.smartlogi.sdms.entity.Utilisateur utilisateur) {
        CustomUserDetails userDetails = new CustomUserDetails(utilisateur);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        return jwtTokenProvider.generateToken(auth);
    }

    @Nested
    @DisplayName("Tests d'accès MANAGER (ROLE_MANAGER)")
    class ManagerAccessTests {

        @Test
        @DisplayName("MANAGER peut lister tous les colis")
        void manager_CanListAllColis() throws Exception {
            mockMvc.perform(get("/api/v1/colis")
                    .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("MANAGER peut gérer les livreurs")
        void manager_CanManageLivreurs() throws Exception {
            mockMvc.perform(get("/api/v1/livreurs")
                    .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("MANAGER peut gérer les zones")
        void manager_CanManageZones() throws Exception {
            mockMvc.perform(get("/api/v1/zones")
                    .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("MANAGER peut accéder aux statistiques")
        void manager_CanAccessStatistics() throws Exception {
            mockMvc.perform(get("/api/v1/statistiques/livreurs")
                    .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests d'accès LIVREUR (ROLE_DELIVERY)")
    class DeliveryAccessTests {

        @Test
        @DisplayName("LIVREUR ne peut PAS lister tous les livreurs")
        void delivery_CannotListAllLivreurs() throws Exception {
            mockMvc.perform(get("/api/v1/livreurs")
                    .header("Authorization", "Bearer " + deliveryToken))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("LIVREUR ne peut PAS gérer les zones")
        void delivery_CannotManageZones() throws Exception {
            mockMvc.perform(get("/api/v1/zones")
                    .header("Authorization", "Bearer " + deliveryToken))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("LIVREUR ne peut PAS accéder aux statistiques")
        void delivery_CannotAccessStatistics() throws Exception {
            mockMvc.perform(get("/api/v1/statistiques/livreurs")
                    .header("Authorization", "Bearer " + deliveryToken))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Tests d'accès CLIENT (ROLE_CLIENT)")
    class ClientAccessTests {

        @Test
        @DisplayName("CLIENT ne peut PAS lister tous les livreurs")
        void client_CannotListAllLivreurs() throws Exception {
            mockMvc.perform(get("/api/v1/livreurs")
                    .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("CLIENT ne peut PAS gérer les zones")
        void client_CannotManageZones() throws Exception {
            mockMvc.perform(get("/api/v1/zones")
                    .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("CLIENT ne peut PAS accéder aux statistiques")
        void client_CannotAccessStatistics() throws Exception {
            mockMvc.perform(get("/api/v1/statistiques/livreurs")
                    .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("CLIENT ne peut PAS créer de livreurs")
        void client_CannotCreateLivreur() throws Exception {
            String livreurJson = """
                {
                    "nom": "Nouveau",
                    "prenom": "Livreur",
                    "email": "nouveau@test.com",
                    "vehicule": "Moto"
                }
                """;

            mockMvc.perform(post("/api/v1/livreurs")
                    .header("Authorization", "Bearer " + clientToken)
                    .contentType("application/json")
                    .content(livreurJson))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Tests d'accès aux endpoints publics")
    class PublicEndpointsTests {

        @Test
        @DisplayName("Swagger UI est accessible sans authentification")
        void swaggerUI_IsPublic() throws Exception {
            mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("API Docs est accessible sans authentification")
        void apiDocs_IsPublic() throws Exception {
            mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Health check est accessible sans authentification")
        void healthCheck_IsPublic() throws Exception {
            mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Login est accessible sans authentification")
        void login_IsPublic() throws Exception {
            mockMvc.perform(post("/auth/login")
                    .contentType("application/json")
                    .content("{\"email\":\"test@test.com\",\"password\":\"test\"}"))
                .andExpect(status().isUnauthorized()); // 401 car credentials invalides, pas 403
        }
    }
}

