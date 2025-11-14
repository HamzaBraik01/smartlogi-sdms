package com.smartlogi.sdms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.sdms.dto.ColisDTO;
import com.smartlogi.sdms.dto.ColisProduitDTO;
import com.smartlogi.sdms.dto.UpdateStatutRequestDTO;
import com.smartlogi.sdms.entity.enumeration.Priorite;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import com.smartlogi.sdms.exception.InvalidDataException;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.service.interfaces.ColisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ColisController.class)
@DisplayName("Tests Unitaires pour ColisController")
class ColisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ColisService colisService;

    private ColisDTO validColisDTO;
    private ColisDTO savedColisDTO;
    private String colisId;

    @BeforeEach
    void setUp() {
        colisId = "colis-123";

        // Création d'un produit pour le colis
        ColisProduitDTO produit = new ColisProduitDTO();
        produit.setProduitId("produit-123");
        produit.setQuantite(2);

        validColisDTO = new ColisDTO();
        validColisDTO.setDescription("Colis contenant matériel informatique");
        validColisDTO.setPoidsTotal(5.0);
        validColisDTO.setPriorite(Priorite.NORMALE);
        validColisDTO.setVilleDestination("Paris");
        validColisDTO.setClientExpediteurId("client-123");
        validColisDTO.setDestinataireId("dest-123");
        validColisDTO.setProduits(List.of(produit));

        savedColisDTO = new ColisDTO();
        savedColisDTO.setId(colisId);
        savedColisDTO.setDescription("Colis contenant matériel informatique");
        savedColisDTO.setPoidsTotal(5.0);
        savedColisDTO.setStatut(StatutColis.EN_STOCK);
        savedColisDTO.setPriorite(Priorite.NORMALE);
        savedColisDTO.setVilleDestination("Paris");
        savedColisDTO.setClientExpediteurId("client-123");
        savedColisDTO.setDestinataireId("dest-123");
        savedColisDTO.setProduits(List.of(produit));
    }

    @Test
    @DisplayName("POST /api/v1/colis - Créer un colis avec succès (201)")
    void testCreateColis_Success() throws Exception {
        // Given
        when(colisService.creerDemandeLivraison(any(ColisDTO.class))).thenReturn(savedColisDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validColisDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/colis/" + colisId))
                .andExpect(jsonPath("$.id", is(colisId)))
                .andExpect(jsonPath("$.description", is("Colis contenant matériel informatique")))
                .andExpect(jsonPath("$.poidsTotal", is(5.0)))
                .andExpect(jsonPath("$.priorite", is("NORMALE")))
                .andExpect(jsonPath("$.villeDestination", is("Paris")));

        verify(colisService, times(1)).creerDemandeLivraison(any(ColisDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/colis - Validation échouée: priorité manquante (400)")
    void testCreateColis_ValidationFailed_PrioriteMissing() throws Exception {
        // Given
        validColisDTO.setPriorite(null);

        // When & Then
        mockMvc.perform(post("/api/v1/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validColisDTO)))
                .andExpect(status().isBadRequest());

        verify(colisService, never()).creerDemandeLivraison(any(ColisDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/colis - Validation échouée: ville destination manquante (400)")
    void testCreateColis_ValidationFailed_VilleDestinationMissing() throws Exception {
        // Given
        validColisDTO.setVilleDestination(null);

        // When & Then
        mockMvc.perform(post("/api/v1/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validColisDTO)))
                .andExpect(status().isBadRequest());

        verify(colisService, never()).creerDemandeLivraison(any(ColisDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/colis - Validation échouée: client expéditeur manquant (400)")
    void testCreateColis_ValidationFailed_ClientExpediteurMissing() throws Exception {
        // Given
        validColisDTO.setClientExpediteurId(null);

        // When & Then
        mockMvc.perform(post("/api/v1/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validColisDTO)))
                .andExpect(status().isBadRequest());

        verify(colisService, never()).creerDemandeLivraison(any(ColisDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/colis - Validation échouée: destinataire manquant (400)")
    void testCreateColis_ValidationFailed_DestinataireMissing() throws Exception {
        // Given
        validColisDTO.setDestinataireId(null);

        // When & Then
        mockMvc.perform(post("/api/v1/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validColisDTO)))
                .andExpect(status().isBadRequest());

        verify(colisService, never()).creerDemandeLivraison(any(ColisDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/colis - Validation échouée: liste produits vide (400)")
    void testCreateColis_ValidationFailed_ProduitsEmpty() throws Exception {
        // Given
        validColisDTO.setProduits(List.of());

        // When & Then
        mockMvc.perform(post("/api/v1/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validColisDTO)))
                .andExpect(status().isBadRequest());

        verify(colisService, never()).creerDemandeLivraison(any(ColisDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/colis - Poids total négatif (400)")
    void testCreateColis_ValidationFailed_PoidsNegatif() throws Exception {
        // Given
        validColisDTO.setPoidsTotal(-1.0);

        // When & Then
        mockMvc.perform(post("/api/v1/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validColisDTO)))
                .andExpect(status().isBadRequest());

        verify(colisService, never()).creerDemandeLivraison(any(ColisDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/colis - Client non trouvé (400)")
    void testCreateColis_ClientNotFound() throws Exception {
        // Given
        when(colisService.creerDemandeLivraison(any(ColisDTO.class)))
                .thenThrow(new InvalidDataException("Client non trouvé"));

        // When & Then
        mockMvc.perform(post("/api/v1/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validColisDTO)))
                .andExpect(status().isBadRequest());

        verify(colisService, times(1)).creerDemandeLivraison(any(ColisDTO.class));
    }

    @Test
    @DisplayName("GET /api/v1/colis/{id} - Récupérer un colis avec succès (200)")
    void testGetColisById_Success() throws Exception {
        // Given
        when(colisService.findById(colisId)).thenReturn(savedColisDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/colis/{id}", colisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(colisId)))
                .andExpect(jsonPath("$.description", is("Colis contenant matériel informatique")))
                .andExpect(jsonPath("$.statut", is("EN_STOCK")));

        verify(colisService, times(1)).findById(colisId);
    }

    @Test
    @DisplayName("GET /api/v1/colis/{id} - Colis non trouvé (404)")
    void testGetColisById_NotFound() throws Exception {
        // Given
        when(colisService.findById(colisId))
                .thenThrow(new ResourceNotFoundException("Colis non trouvé"));

        // When & Then
        mockMvc.perform(get("/api/v1/colis/{id}", colisId))
                .andExpect(status().isNotFound());

        verify(colisService, times(1)).findById(colisId);
    }

    @Test
    @DisplayName("GET /api/v1/colis - Filtrer les colis (200)")
    void testFilterColis_Success() throws Exception {
        // Given
        List<ColisDTO> colis = List.of(savedColisDTO);
        Page<ColisDTO> page = new PageImpl<>(colis, PageRequest.of(0, 10), 1);

        when(colisService.findAllColisByCriteria(
                any(), any(), any(), any(), any(Pageable.class)
        )).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/colis")
                        .param("statut", "EN_STOCK")
                        .param("ville", "Paris")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(colisId)));

        verify(colisService, times(1)).findAllColisByCriteria(
                any(), any(), any(), any(), any(Pageable.class)
        );
    }

    @Test
    @DisplayName("PATCH /api/v1/colis/{colisId}/statut - Mettre à jour le statut (200)")
    void testUpdateStatut_Success() throws Exception {
        // Given
        UpdateStatutRequestDTO request = new UpdateStatutRequestDTO();
        request.setStatut(StatutColis.EN_TRANSIT);
        request.setCommentaire("Colis en route");

        ColisDTO updatedColis = new ColisDTO();
        updatedColis.setId(colisId);
        updatedColis.setStatut(StatutColis.EN_TRANSIT);

        when(colisService.updateStatutColis(eq(colisId), eq(StatutColis.EN_TRANSIT), eq("Colis en route")))
                .thenReturn(updatedColis);

        // When & Then
        mockMvc.perform(patch("/api/v1/colis/{colisId}/statut", colisId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(colisId)))
                .andExpect(jsonPath("$.statut", is("EN_TRANSIT")));

        verify(colisService, times(1)).updateStatutColis(
                eq(colisId), eq(StatutColis.EN_TRANSIT), eq("Colis en route")
        );
    }

    @Test
    @DisplayName("PATCH /api/v1/colis/{colisId}/statut - Validation échouée: statut manquant (400)")
    void testUpdateStatut_ValidationFailed() throws Exception {
        // Given
        UpdateStatutRequestDTO request = new UpdateStatutRequestDTO();
        request.setStatut(null);

        // When & Then
        mockMvc.perform(patch("/api/v1/colis/{colisId}/statut", colisId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(colisService, never()).updateStatutColis(any(), any(), any());
    }

    @Test
    @DisplayName("PATCH /api/v1/colis/{colisId}/statut - Transition invalide (400)")
    void testUpdateStatut_InvalidTransition() throws Exception {
        // Given
        UpdateStatutRequestDTO request = new UpdateStatutRequestDTO();
        request.setStatut(StatutColis.EN_STOCK);

        when(colisService.updateStatutColis(eq(colisId), eq(StatutColis.EN_STOCK), any()))
                .thenThrow(new InvalidDataException("Transition de statut invalide"));

        // When & Then
        mockMvc.perform(patch("/api/v1/colis/{colisId}/statut", colisId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(colisService, times(1)).updateStatutColis(eq(colisId), eq(StatutColis.EN_STOCK), any());
    }

    @Test
    @DisplayName("GET /api/v1/colis/client/{clientId} - Récupérer colis par client (200)")
    void testGetColisByClient_Success() throws Exception {
        // Given
        String clientId = "client-123";
        List<ColisDTO> colis = List.of(savedColisDTO);
        Page<ColisDTO> page = new PageImpl<>(colis, PageRequest.of(0, 10), 1);

        when(colisService.findColisByClientExpediteur(eq(clientId), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/colis/client/{clientId}", clientId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].clientExpediteurId", is(clientId)));

        verify(colisService, times(1)).findColisByClientExpediteur(eq(clientId), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/colis/destinataire/{destinataireId} - Récupérer colis par destinataire (200)")
    void testGetColisByDestinataire_Success() throws Exception {
        // Given
        String destinataireId = "dest-123";
        List<ColisDTO> colis = List.of(savedColisDTO);
        Page<ColisDTO> page = new PageImpl<>(colis, PageRequest.of(0, 10), 1);

        when(colisService.findColisByDestinataire(eq(destinataireId), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/colis/destinataire/{destinataireId}", destinataireId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].destinataireId", is(destinataireId)));

        verify(colisService, times(1)).findColisByDestinataire(eq(destinataireId), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/colis/livreur/{livreurId} - Récupérer colis par livreur (200)")
    void testGetColisByLivreur_Success() throws Exception {
        // Given
        String livreurId = "livreur-123";
        savedColisDTO.setLivreurId(livreurId);
        List<ColisDTO> colis = List.of(savedColisDTO);
        Page<ColisDTO> page = new PageImpl<>(colis, PageRequest.of(0, 10), 1);

        when(colisService.findColisByLivreur(eq(livreurId), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/colis/livreur/{livreurId}", livreurId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].livreurId", is(livreurId)));

        verify(colisService, times(1)).findColisByLivreur(eq(livreurId), any(Pageable.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/colis/{id} - Supprimer un colis avec succès (204)")
    void testDeleteColis_Success() throws Exception {
        // Given
        doNothing().when(colisService).delete(colisId);

        // When & Then
        mockMvc.perform(delete("/api/v1/colis/{id}", colisId))
                .andExpect(status().isNoContent());

        verify(colisService, times(1)).delete(colisId);
    }

    @Test
    @DisplayName("DELETE /api/v1/colis/{id} - Colis non trouvé (404)")
    void testDeleteColis_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Colis non trouvé avec l'ID: " + colisId))
                .when(colisService).delete(colisId);

        // When & Then
        mockMvc.perform(delete("/api/v1/colis/{id}", colisId))
                .andExpect(status().isNotFound());

        verify(colisService, times(1)).delete(colisId);
    }
}
