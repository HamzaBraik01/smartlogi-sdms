package com.smartlogi.sdms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.sdms.dto.ColisDTO;
import com.smartlogi.sdms.dto.ColisProduitDTO;
import com.smartlogi.sdms.entity.*;
import com.smartlogi.sdms.entity.enumeration.Priorite;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import com.smartlogi.sdms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration REST pour ColisController
 * Teste les endpoints POST /api/v1/colis et GET /api/v1/colis/{id}
 * avec une vraie base de données H2 en mémoire
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests d'Intégration REST - ColisController")
public class ColisControllerRestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ColisRepository colisRepository;

    @Autowired
    private ClientExpediteurRepository clientExpediteurRepository;

    @Autowired
    private DestinataireRepository destinataireRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private LivreurRepository livreurRepository;

    private Zone zone;
    private ClientExpediteur clientExpediteur;
    private Destinataire destinataire;
    private Produit produit1;
    private Produit produit2;
    private Livreur livreur;

    @BeforeEach
    void setUp() {
        // Nettoyer la base de données avant chaque test
        colisRepository.deleteAll();
        livreurRepository.deleteAll();
        produitRepository.deleteAll();
        destinataireRepository.deleteAll();
        clientExpediteurRepository.deleteAll();
        zoneRepository.deleteAll();

        // Créer les données de test
        zone = new Zone();
        zone.setNom("Zone Nord Paris");
        zone.setCodePostal("75018");
        zone.setVille("Paris");
        zone = zoneRepository.save(zone);

        clientExpediteur = new ClientExpediteur();
        clientExpediteur.setNom("Dupont");
        clientExpediteur.setPrenom("Jean");
        clientExpediteur.setEmail("jean.dupont@example.com");
        clientExpediteur.setTelephone("0612345678");
        clientExpediteur.setAdresse("10 Rue de la Paix, Paris");
        clientExpediteur = clientExpediteurRepository.save(clientExpediteur);

        destinataire = new Destinataire();
        destinataire.setNom("Martin");
        destinataire.setPrenom("Sophie");
        destinataire.setEmail("sophie.martin@example.com");
        destinataire.setTelephone("0687654321");
        destinataire.setAdresse("25 Avenue des Champs, Lyon");
        destinataire = destinataireRepository.save(destinataire);

        produit1 = new Produit();
        produit1.setNom("Ordinateur Portable");
        produit1.setPoids(2.5);
        produit1.setPrix(new BigDecimal("999.99"));
        produit1 = produitRepository.save(produit1);

        produit2 = new Produit();
        produit2.setNom("Souris Sans Fil");
        produit2.setPoids(0.2);
        produit2.setPrix(new BigDecimal("29.99"));
        produit2 = produitRepository.save(produit2);

        livreur = new Livreur();
        livreur.setNom("Dubois");
        livreur.setPrenom("Pierre");
        livreur.setEmail("pierre.dubois@smartlogi.com");
        livreur.setTelephone("0698765432");
        livreur.setVehicule("Camionnette");
        livreur.setZone(zone);
        livreur = livreurRepository.save(livreur);
    }

    @Test
    @DisplayName("POST /api/v1/colis - Doit créer un colis avec succès et retourner 201 Created")
    void testCreateColis_Success_Returns201Created() throws Exception {
        // Given - Préparer le DTO de la demande
        ColisDTO colisDTO = new ColisDTO();
        colisDTO.setDescription("Matériel informatique fragile");
        colisDTO.setPriorite(Priorite.HAUTE);
        colisDTO.setVilleDestination("Lyon");
        colisDTO.setClientExpediteurId(clientExpediteur.getId());
        colisDTO.setDestinataireId(destinataire.getId());

        List<ColisProduitDTO> produits = new ArrayList<>();

        ColisProduitDTO produitDTO1 = new ColisProduitDTO();
        produitDTO1.setProduitId(produit1.getId());
        produitDTO1.setQuantite(1);
        produits.add(produitDTO1);

        ColisProduitDTO produitDTO2 = new ColisProduitDTO();
        produitDTO2.setProduitId(produit2.getId());
        produitDTO2.setQuantite(2);
        produits.add(produitDTO2);

        colisDTO.setProduits(produits);

        String colisJson = objectMapper.writeValueAsString(colisDTO);

        // When - Envoyer la requête POST
        MvcResult result = mockMvc.perform(post("/api/v1/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(colisJson))
                // Then - Vérifier la réponse
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/api/v1/colis/")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Matériel informatique fragile"))
                .andExpect(jsonPath("$.priorite").value("HAUTE"))
                .andExpect(jsonPath("$.statut").value("CREE"))
                .andExpect(jsonPath("$.villeDestination").value("Lyon"))
                .andExpect(jsonPath("$.poidsTotal").value(2.9)) // 2.5 + 0.2*2
                .andExpect(jsonPath("$.clientExpediteurId").value(clientExpediteur.getId()))
                .andExpect(jsonPath("$.destinataireId").value(destinataire.getId()))
                .andExpect(jsonPath("$.dateCreation").exists())
                .andExpect(jsonPath("$.dateDernierStatut").exists())
                .andReturn();

        // Vérifier que l'ID est retourné
        String responseBody = result.getResponse().getContentAsString();
        ColisDTO createdColis = objectMapper.readValue(responseBody, ColisDTO.class);
        assertThat(createdColis.getId()).isNotNull();
        assertThat(createdColis.getStatut()).isEqualTo(StatutColis.CREE);

        // Vérifier que le colis existe bien en base de données
        assertThat(colisRepository.findById(createdColis.getId())).isPresent();
    }

    @Test
    @DisplayName("GET /api/v1/colis/{id} - Doit récupérer un colis existant et retourner 200 OK")
    void testGetColisById_Success_Returns200OK() throws Exception {
        // Given - Créer d'abord un colis via le service
        ColisDTO colisDTO = new ColisDTO();
        colisDTO.setDescription("Colis de test pour GET");
        colisDTO.setPriorite(Priorite.NORMALE);
        colisDTO.setVilleDestination("Marseille");
        colisDTO.setClientExpediteurId(clientExpediteur.getId());
        colisDTO.setDestinataireId(destinataire.getId());

        List<ColisProduitDTO> produits = new ArrayList<>();
        ColisProduitDTO produitDTO = new ColisProduitDTO();
        produitDTO.setProduitId(produit1.getId());
        produitDTO.setQuantite(2);
        produits.add(produitDTO);
        colisDTO.setProduits(produits);

        String colisJson = objectMapper.writeValueAsString(colisDTO);

        // Créer le colis via POST
        MvcResult createResult = mockMvc.perform(post("/api/v1/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(colisJson))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponseBody = createResult.getResponse().getContentAsString();
        ColisDTO createdColis = objectMapper.readValue(createResponseBody, ColisDTO.class);
        String colisId = createdColis.getId();

        // When - Récupérer le colis via GET
        mockMvc.perform(get("/api/v1/colis/{id}", colisId)
                        .accept(MediaType.APPLICATION_JSON))
                // Then - Vérifier la réponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(colisId))
                .andExpect(jsonPath("$.description").value("Colis de test pour GET"))
                .andExpect(jsonPath("$.priorite").value("NORMALE"))
                .andExpect(jsonPath("$.statut").value("CREE"))
                .andExpect(jsonPath("$.villeDestination").value("Marseille"))
                .andExpect(jsonPath("$.poidsTotal").value(5.0)) // 2.5 * 2
                .andExpect(jsonPath("$.clientExpediteurId").value(clientExpediteur.getId()))
                .andExpect(jsonPath("$.destinataireId").value(destinataire.getId()))
                .andExpect(jsonPath("$.dateCreation").exists())
                .andExpect(jsonPath("$.dateDernierStatut").exists());
    }

    @Test
    @DisplayName("POST /api/v1/colis puis GET /api/v1/colis/{id} - Cycle complet de création et récupération")
    void testPostThenGet_CompleteFlow() throws Exception {
        // Given - Préparer un colis avec plusieurs produits
        ColisDTO colisDTO = new ColisDTO();
        colisDTO.setDescription("Commande e-commerce complète");
        colisDTO.setPriorite(Priorite.URGENTE);
        colisDTO.setVilleDestination("Toulouse");
        colisDTO.setClientExpediteurId(clientExpediteur.getId());
        colisDTO.setDestinataireId(destinataire.getId());

        List<ColisProduitDTO> produits = new ArrayList<>();

        ColisProduitDTO produitDTO1 = new ColisProduitDTO();
        produitDTO1.setProduitId(produit1.getId());
        produitDTO1.setQuantite(3);
        produits.add(produitDTO1);

        ColisProduitDTO produitDTO2 = new ColisProduitDTO();
        produitDTO2.setProduitId(produit2.getId());
        produitDTO2.setQuantite(5);
        produits.add(produitDTO2);

        colisDTO.setProduits(produits);

        String colisJson = objectMapper.writeValueAsString(colisDTO);

        // When - Étape 1 : POST pour créer le colis
        MvcResult postResult = mockMvc.perform(post("/api/v1/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(colisJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        // Extraire l'ID du colis créé
        String postResponseBody = postResult.getResponse().getContentAsString();
        ColisDTO createdColis = objectMapper.readValue(postResponseBody, ColisDTO.class);
        String colisId = createdColis.getId();

        // Then - Étape 2 : GET pour vérifier que les données sont bien persistées
        mockMvc.perform(get("/api/v1/colis/{id}", colisId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(colisId))
                .andExpect(jsonPath("$.description").value("Commande e-commerce complète"))
                .andExpect(jsonPath("$.priorite").value("URGENTE"))
                .andExpect(jsonPath("$.statut").value("CREE"))
                .andExpect(jsonPath("$.villeDestination").value("Toulouse"))
                .andExpect(jsonPath("$.poidsTotal").value(8.5)) // 2.5*3 + 0.2*5
                .andExpect(jsonPath("$.clientExpediteurId").value(clientExpediteur.getId()))
                .andExpect(jsonPath("$.destinataireId").value(destinataire.getId()));

        // Vérification supplémentaire : le colis existe en base
        Colis colisEnBase = colisRepository.findById(colisId).orElseThrow();
        assertThat(colisEnBase).isNotNull();
        assertThat(colisEnBase.getStatut()).isEqualTo(StatutColis.CREE);
        assertThat(colisEnBase.getDescription()).isEqualTo("Commande e-commerce complète");
        assertThat(colisEnBase.getPoidsTotal()).isEqualTo(8.5);
    }

    @Test
    @DisplayName("POST /api/v1/colis - Doit retourner 400 Bad Request si les données sont invalides")
    void testCreateColis_InvalidData_Returns400BadRequest() throws Exception {
        // Given - DTO invalide (pas de produits)
        ColisDTO colisDTO = new ColisDTO();
        colisDTO.setDescription("Colis sans produits");
        colisDTO.setPriorite(Priorite.NORMALE);
        colisDTO.setVilleDestination("Paris");
        colisDTO.setClientExpediteurId(clientExpediteur.getId());
        colisDTO.setDestinataireId(destinataire.getId());
        // Pas de produits -> violation de la validation @NotEmpty

        String colisJson = objectMapper.writeValueAsString(colisDTO);

        // When & Then - La requête doit échouer avec 400
        mockMvc.perform(post("/api/v1/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(colisJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/colis/{id} - Doit retourner 404 Not Found si le colis n'existe pas")
    void testGetColisById_NotFound_Returns404() throws Exception {
        // Given - Un ID qui n'existe pas
        String nonExistentId = "id-inexistant-12345";

        // When & Then - La requête doit retourner 404
        mockMvc.perform(get("/api/v1/colis/{id}", nonExistentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/colis - Doit retourner 400 si le client expéditeur n'existe pas")
    void testCreateColis_ClientNotFound_Returns400() throws Exception {
        // Given - DTO avec un client expéditeur inexistant
        ColisDTO colisDTO = new ColisDTO();
        colisDTO.setDescription("Colis avec client invalide");
        colisDTO.setPriorite(Priorite.NORMALE);
        colisDTO.setVilleDestination("Nice");
        colisDTO.setClientExpediteurId("client-inexistant-999");
        colisDTO.setDestinataireId(destinataire.getId());

        List<ColisProduitDTO> produits = new ArrayList<>();
        ColisProduitDTO produitDTO = new ColisProduitDTO();
        produitDTO.setProduitId(produit1.getId());
        produitDTO.setQuantite(1);
        produits.add(produitDTO);
        colisDTO.setProduits(produits);

        String colisJson = objectMapper.writeValueAsString(colisDTO);

        // When & Then - La requête doit échouer avec 400 (ou 404 selon la gestion d'erreur)
        mockMvc.perform(post("/api/v1/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(colisJson))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /api/v1/colis - Doit calculer automatiquement le poids total à partir des produits")
    void testCreateColis_AutoCalculateWeight() throws Exception {
        // Given - Colis avec 2 produits de poids différents
        ColisDTO colisDTO = new ColisDTO();
        colisDTO.setDescription("Test calcul automatique du poids");
        colisDTO.setPriorite(Priorite.NORMALE);
        colisDTO.setVilleDestination("Bordeaux");
        colisDTO.setClientExpediteurId(clientExpediteur.getId());
        colisDTO.setDestinataireId(destinataire.getId());

        List<ColisProduitDTO> produits = new ArrayList<>();

        // Produit 1 : 2.5 kg x 2 = 5 kg
        ColisProduitDTO produitDTO1 = new ColisProduitDTO();
        produitDTO1.setProduitId(produit1.getId());
        produitDTO1.setQuantite(2);
        produits.add(produitDTO1);

        // Produit 2 : 0.2 kg x 3 = 0.6 kg
        ColisProduitDTO produitDTO2 = new ColisProduitDTO();
        produitDTO2.setProduitId(produit2.getId());
        produitDTO2.setQuantite(3);
        produits.add(produitDTO2);

        colisDTO.setProduits(produits);
        // Total attendu : 5 + 0.6 = 5.6 kg

        String colisJson = objectMapper.writeValueAsString(colisDTO);

        // When & Then - Vérifier que le poids est bien calculé
        mockMvc.perform(post("/api/v1/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(colisJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.poidsTotal").value(5.6));
    }

    @Test
    @DisplayName("POST /api/v1/colis - Doit définir automatiquement le statut à CREE")
    void testCreateColis_AutoSetStatusToCree() throws Exception {
        // Given - Colis sans statut spécifié
        ColisDTO colisDTO = new ColisDTO();
        colisDTO.setDescription("Test statut automatique");
        colisDTO.setPriorite(Priorite.BASSE);
        colisDTO.setVilleDestination("Lille");
        colisDTO.setClientExpediteurId(clientExpediteur.getId());
        colisDTO.setDestinataireId(destinataire.getId());

        List<ColisProduitDTO> produits = new ArrayList<>();
        ColisProduitDTO produitDTO = new ColisProduitDTO();
        produitDTO.setProduitId(produit1.getId());
        produitDTO.setQuantite(1);
        produits.add(produitDTO);
        colisDTO.setProduits(produits);

        String colisJson = objectMapper.writeValueAsString(colisDTO);

        // When & Then - Le statut doit être automatiquement CREE
        mockMvc.perform(post("/api/v1/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(colisJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("CREE"));
    }
}

