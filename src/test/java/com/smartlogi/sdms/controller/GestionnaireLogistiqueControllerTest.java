package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.ColisDTO;
import com.smartlogi.sdms.dto.GlobalSearchResponseDTO;
import com.smartlogi.sdms.dto.HistoriqueLivraisonDTO;
import com.smartlogi.sdms.dto.StatistiquesTourneeDTO;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.service.interfaces.ColisService;
import com.smartlogi.sdms.service.interfaces.GestionnaireLogistiqueService;
import com.smartlogi.sdms.service.interfaces.HistoriqueLivraisonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GestionnaireLogistiqueController.class)
@DisplayName("Tests Unitaires pour GestionnaireLogistiqueController")
class GestionnaireLogistiqueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GestionnaireLogistiqueService gestionnaireLogistiqueService;

    @MockitoBean
    private ColisService colisService;

    @MockitoBean
    private HistoriqueLivraisonService historiqueLivraisonService;

    private String colisId;
    private String livreurId;
    private ColisDTO colisDTO;

    @BeforeEach
    void setUp() {
        colisId = "colis-123";
        livreurId = "livreur-456";

        colisDTO = new ColisDTO();
        colisDTO.setId(colisId);
        colisDTO.setStatut(StatutColis.EN_STOCK);
        colisDTO.setLivreurId(livreurId);
    }

    @Test
    @DisplayName("PATCH /api/v1/gestion/colis/{colisId}/assigner/{livreurId} - Assigner un colis avec succès (200)")
    void testAssignerColis_Success() throws Exception {
        // Given
        ColisDTO assignedColis = new ColisDTO();
        assignedColis.setId(colisId);
        assignedColis.setLivreurId(livreurId);

        when(colisService.assignerColisLivreur(eq(colisId), eq(livreurId))).thenReturn(assignedColis);

        // When & Then
        mockMvc.perform(patch("/api/v1/gestion/colis/{colisId}/assigner/{livreurId}", colisId, livreurId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(colisId)))
                .andExpect(jsonPath("$.livreurId", is(livreurId)));

        verify(colisService, times(1)).assignerColisLivreur(eq(colisId), eq(livreurId));
    }

    @Test
    @DisplayName("PATCH /api/v1/gestion/colis/{colisId}/assigner/{livreurId} - Colis non trouvé (404)")
    void testAssignerColis_ColisNotFound() throws Exception {
        // Given
        when(colisService.assignerColisLivreur(eq(colisId), eq(livreurId)))
                .thenThrow(new ResourceNotFoundException("Colis non trouvé avec l'ID: " + colisId));

        // When & Then
        mockMvc.perform(patch("/api/v1/gestion/colis/{colisId}/assigner/{livreurId}", colisId, livreurId))
                .andExpect(status().isNotFound());

        verify(colisService, times(1)).assignerColisLivreur(eq(colisId), eq(livreurId));
    }

    @Test
    @DisplayName("PATCH /api/v1/gestion/colis/{colisId}/assigner/{livreurId} - Livreur non trouvé (404)")
    void testAssignerColis_LivreurNotFound() throws Exception {
        // Given
        when(colisService.assignerColisLivreur(eq(colisId), eq(livreurId)))
                .thenThrow(new ResourceNotFoundException("Livreur non trouvé avec l'ID: " + livreurId));

        // When & Then
        mockMvc.perform(patch("/api/v1/gestion/colis/{colisId}/assigner/{livreurId}", colisId, livreurId))
                .andExpect(status().isNotFound());

        verify(colisService, times(1)).assignerColisLivreur(eq(colisId), eq(livreurId));
    }

    @Test
    @DisplayName("GET /api/v1/gestion/recherche - Recherche globale avec résultats (200)")
    void testRechercher_Success() throws Exception {
        // Given
        String searchTerm = "Dupont";
        GlobalSearchResponseDTO searchResponse = new GlobalSearchResponseDTO();
        // Vous pouvez configurer des clients, colis, livreurs correspondants ici

        when(gestionnaireLogistiqueService.rechercher(eq(searchTerm), any(Pageable.class)))
                .thenReturn(searchResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/gestion/recherche")
                        .param("q", searchTerm)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(gestionnaireLogistiqueService, times(1)).rechercher(eq(searchTerm), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/gestion/recherche - Recherche avec résultats vides (200)")
    void testRechercher_EmptyResults() throws Exception {
        // Given
        String searchTerm = "NonExistant";
        GlobalSearchResponseDTO emptyResponse = new GlobalSearchResponseDTO();

        when(gestionnaireLogistiqueService.rechercher(eq(searchTerm), any(Pageable.class)))
                .thenReturn(emptyResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/gestion/recherche")
                        .param("q", searchTerm)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(gestionnaireLogistiqueService, times(1)).rechercher(eq(searchTerm), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/gestion/statistiques - Obtenir les statistiques (200)")
    void testGetStatistiques_Success() throws Exception {
        // Given
        StatistiquesTourneeDTO stats = new StatistiquesTourneeDTO();
        // Configurer des statistiques de test

        when(gestionnaireLogistiqueService.getStatistiquesTournees()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/v1/gestion/statistiques"))
                .andExpect(status().isOk());

        verify(gestionnaireLogistiqueService, times(1)).getStatistiquesTournees();
    }

    @Test
    @DisplayName("GET /api/v1/gestion/colis/{colisId}/historique - Récupérer l'historique d'un colis (200)")
    void testGetHistoriqueColis_Success() throws Exception {
        // Given
        HistoriqueLivraisonDTO hist1 = new HistoriqueLivraisonDTO();
        hist1.setId("hist-1");
        hist1.setColisId(colisId);
        hist1.setStatut(StatutColis.EN_STOCK);
        hist1.setDateChangement(LocalDateTime.now().minusDays(2));
        hist1.setCommentaire("Colis en stock");

        HistoriqueLivraisonDTO hist2 = new HistoriqueLivraisonDTO();
        hist2.setId("hist-2");
        hist2.setColisId(colisId);
        hist2.setStatut(StatutColis.EN_TRANSIT);
        hist2.setDateChangement(LocalDateTime.now().minusDays(1));
        hist2.setCommentaire("Colis en transit");

        List<HistoriqueLivraisonDTO> historique = Arrays.asList(hist2, hist1);

        when(historiqueLivraisonService.findHistoriqueByColisId(eq(colisId))).thenReturn(historique);

        // When & Then
        mockMvc.perform(get("/api/v1/gestion/colis/{colisId}/historique", colisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("hist-2")))
                .andExpect(jsonPath("$[0].statut", is("EN_TRANSIT")))
                .andExpect(jsonPath("$[1].id", is("hist-1")))
                .andExpect(jsonPath("$[1].statut", is("EN_STOCK")));

        verify(historiqueLivraisonService, times(1)).findHistoriqueByColisId(eq(colisId));
    }

    @Test
    @DisplayName("GET /api/v1/gestion/colis/{colisId}/historique - Colis non trouvé (404)")
    void testGetHistoriqueColis_ColisNotFound() throws Exception {
        // Given
        when(historiqueLivraisonService.findHistoriqueByColisId(eq(colisId)))
                .thenThrow(new ResourceNotFoundException("Colis non trouvé avec l'ID: " + colisId));

        // When & Then
        mockMvc.perform(get("/api/v1/gestion/colis/{colisId}/historique", colisId))
                .andExpect(status().isNotFound());

        verify(historiqueLivraisonService, times(1)).findHistoriqueByColisId(eq(colisId));
    }

    @Test
    @DisplayName("GET /api/v1/gestion/colis/{colisId}/historique - Historique vide (200)")
    void testGetHistoriqueColis_EmptyHistory() throws Exception {
        // Given
        when(historiqueLivraisonService.findHistoriqueByColisId(eq(colisId))).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/gestion/colis/{colisId}/historique", colisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(historiqueLivraisonService, times(1)).findHistoriqueByColisId(eq(colisId));
    }
}

