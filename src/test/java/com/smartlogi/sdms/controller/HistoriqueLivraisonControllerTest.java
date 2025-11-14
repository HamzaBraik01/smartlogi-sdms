package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.HistoriqueLivraisonDTO;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.service.interfaces.HistoriqueLivraisonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HistoriqueLivraisonController.class)
@DisplayName("Tests Unitaires pour HistoriqueLivraisonController")
class HistoriqueLivraisonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HistoriqueLivraisonService historiqueLivraisonService;

    private String colisId;
    private List<HistoriqueLivraisonDTO> historique;

    @BeforeEach
    void setUp() {
        colisId = "colis-123";

        HistoriqueLivraisonDTO hist1 = new HistoriqueLivraisonDTO();
        hist1.setId("hist-1");
        hist1.setColisId(colisId);
        hist1.setStatut(StatutColis.EN_STOCK);
        hist1.setDateChangement(LocalDateTime.now().minusDays(2));
        hist1.setCommentaire("Colis reçu en entrepôt");

        HistoriqueLivraisonDTO hist2 = new HistoriqueLivraisonDTO();
        hist2.setId("hist-2");
        hist2.setColisId(colisId);
        hist2.setStatut(StatutColis.EN_TRANSIT);
        hist2.setDateChangement(LocalDateTime.now().minusDays(1));
        hist2.setCommentaire("Colis en cours de livraison");

        HistoriqueLivraisonDTO hist3 = new HistoriqueLivraisonDTO();
        hist3.setId("hist-3");
        hist3.setColisId(colisId);
        hist3.setStatut(StatutColis.LIVRE);
        hist3.setDateChangement(LocalDateTime.now());
        hist3.setCommentaire("Colis livré avec succès");

        // Tri du plus récent au plus ancien
        historique = Arrays.asList(hist3, hist2, hist1);
    }

    @Test
    @DisplayName("GET /api/v1/colis/{id}/historique - Récupérer l'historique avec succès (200)")
    void testGetHistoriqueByColisId_Success() throws Exception {
        // Given
        when(historiqueLivraisonService.findHistoriqueByColisId(eq(colisId))).thenReturn(historique);

        // When & Then
        mockMvc.perform(get("/api/v1/colis/{id}/historique", colisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is("hist-3")))
                .andExpect(jsonPath("$[0].statut", is("LIVRE")))
                .andExpect(jsonPath("$[0].commentaire", is("Colis livré avec succès")))
                .andExpect(jsonPath("$[1].id", is("hist-2")))
                .andExpect(jsonPath("$[1].statut", is("EN_TRANSIT")))
                .andExpect(jsonPath("$[1].commentaire", is("Colis en cours de livraison")))
                .andExpect(jsonPath("$[2].id", is("hist-1")))
                .andExpect(jsonPath("$[2].statut", is("EN_STOCK")))
                .andExpect(jsonPath("$[2].commentaire", is("Colis reçu en entrepôt")));

        verify(historiqueLivraisonService, times(1)).findHistoriqueByColisId(eq(colisId));
    }

    @Test
    @DisplayName("GET /api/v1/colis/{id}/historique - Colis non trouvé (404)")
    void testGetHistoriqueByColisId_ColisNotFound() throws Exception {
        // Given
        when(historiqueLivraisonService.findHistoriqueByColisId(eq(colisId)))
                .thenThrow(new ResourceNotFoundException("Colis non trouvé avec l'ID: " + colisId));

        // When & Then
        mockMvc.perform(get("/api/v1/colis/{id}/historique", colisId))
                .andExpect(status().isNotFound());

        verify(historiqueLivraisonService, times(1)).findHistoriqueByColisId(eq(colisId));
    }

    @Test
    @DisplayName("GET /api/v1/colis/{id}/historique - Historique vide (200)")
    void testGetHistoriqueByColisId_EmptyHistory() throws Exception {
        // Given
        when(historiqueLivraisonService.findHistoriqueByColisId(eq(colisId))).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/colis/{id}/historique", colisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(historiqueLivraisonService, times(1)).findHistoriqueByColisId(eq(colisId));
    }

    @Test
    @DisplayName("GET /api/v1/colis/{id}/historique - Vérifier l'ordre chronologique (200)")
    void testGetHistoriqueByColisId_ChronologicalOrder() throws Exception {
        // Given
        when(historiqueLivraisonService.findHistoriqueByColisId(eq(colisId))).thenReturn(historique);

        // When & Then
        mockMvc.perform(get("/api/v1/colis/{id}/historique", colisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].statut", is("LIVRE"))) // Plus récent
                .andExpect(jsonPath("$[1].statut", is("EN_TRANSIT")))
                .andExpect(jsonPath("$[2].statut", is("EN_STOCK"))); // Plus ancien

        verify(historiqueLivraisonService, times(1)).findHistoriqueByColisId(eq(colisId));
    }

    @Test
    @DisplayName("GET /api/v1/colis/{id}/historique - Historique avec un seul événement (200)")
    void testGetHistoriqueByColisId_SingleEvent() throws Exception {
        // Given
        HistoriqueLivraisonDTO singleHist = new HistoriqueLivraisonDTO();
        singleHist.setId("hist-single");
        singleHist.setColisId(colisId);
        singleHist.setStatut(StatutColis.EN_STOCK);
        singleHist.setDateChangement(LocalDateTime.now());
        singleHist.setCommentaire("Premier événement");

        when(historiqueLivraisonService.findHistoriqueByColisId(eq(colisId)))
                .thenReturn(List.of(singleHist));

        // When & Then
        mockMvc.perform(get("/api/v1/colis/{id}/historique", colisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("hist-single")))
                .andExpect(jsonPath("$[0].statut", is("EN_STOCK")))
                .andExpect(jsonPath("$[0].commentaire", is("Premier événement")));

        verify(historiqueLivraisonService, times(1)).findHistoriqueByColisId(eq(colisId));
    }
}

