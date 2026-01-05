package com.smartlogi.sdms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.sdms.config.TestSecurityConfig;
import com.smartlogi.sdms.dto.ProduitDTO;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.service.interfaces.ProduitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProduitController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
@DisplayName("Tests Unitaires pour ProduitController")
class ProduitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProduitService produitService;

    private ProduitDTO validProduitDTO;
    private ProduitDTO savedProduitDTO;
    private String produitId;

    @BeforeEach
    void setUp() {
        produitId = "produit-123";

        validProduitDTO = new ProduitDTO();
        validProduitDTO.setNom("Ordinateur Portable");
        validProduitDTO.setPoids(2.5);
        validProduitDTO.setPrix(new BigDecimal("999.99"));

        savedProduitDTO = new ProduitDTO();
        savedProduitDTO.setId(produitId);
        savedProduitDTO.setNom("Ordinateur Portable");
        savedProduitDTO.setPoids(2.5);
        savedProduitDTO.setPrix(new BigDecimal("999.99"));
    }

    @Test
    @DisplayName("POST /api/v1/produits - Créer un produit avec succès (201)")
    void testCreateProduit_Success() throws Exception {
        // Given
        when(produitService.save(any(ProduitDTO.class))).thenReturn(savedProduitDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduitDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/produits/" + produitId))
                .andExpect(jsonPath("$.id", is(produitId)))
                .andExpect(jsonPath("$.nom", is("Ordinateur Portable")))
                .andExpect(jsonPath("$.poids", is(2.5)))
                .andExpect(jsonPath("$.prix", is(999.99)));

        verify(produitService, times(1)).save(any(ProduitDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/produits - Validation échouée: nom manquant (400)")
    void testCreateProduit_ValidationFailed_NomMissing() throws Exception {
        // Given
        validProduitDTO.setNom(null);

        // When & Then
        mockMvc.perform(post("/api/v1/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduitDTO)))
                .andExpect(status().isBadRequest());

        verify(produitService, never()).save(any(ProduitDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/produits - Validation échouée: nom trop court (400)")
    void testCreateProduit_ValidationFailed_NomTooShort() throws Exception {
        // Given
        validProduitDTO.setNom("A");

        // When & Then
        mockMvc.perform(post("/api/v1/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduitDTO)))
                .andExpect(status().isBadRequest());

        verify(produitService, never()).save(any(ProduitDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/produits - Validation échouée: poids manquant (400)")
    void testCreateProduit_ValidationFailed_PoidsMissing() throws Exception {
        // Given
        validProduitDTO.setPoids(null);

        // When & Then
        mockMvc.perform(post("/api/v1/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduitDTO)))
                .andExpect(status().isBadRequest());

        verify(produitService, never()).save(any(ProduitDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/produits - Validation échouée: poids négatif (400)")
    void testCreateProduit_ValidationFailed_PoidsNegative() throws Exception {
        // Given
        validProduitDTO.setPoids(-1.0);

        // When & Then
        mockMvc.perform(post("/api/v1/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduitDTO)))
                .andExpect(status().isBadRequest());

        verify(produitService, never()).save(any(ProduitDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/produits - Validation échouée: prix manquant (400)")
    void testCreateProduit_ValidationFailed_PrixMissing() throws Exception {
        // Given
        validProduitDTO.setPrix(null);

        // When & Then
        mockMvc.perform(post("/api/v1/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduitDTO)))
                .andExpect(status().isBadRequest());

        verify(produitService, never()).save(any(ProduitDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/produits - Validation échouée: prix négatif (400)")
    void testCreateProduit_ValidationFailed_PrixNegative() throws Exception {
        // Given
        validProduitDTO.setPrix(new BigDecimal("-10.00"));

        // When & Then
        mockMvc.perform(post("/api/v1/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduitDTO)))
                .andExpect(status().isBadRequest());

        verify(produitService, never()).save(any(ProduitDTO.class));
    }

    @Test
    @DisplayName("GET /api/v1/produits/{id} - Récupérer un produit avec succès (200)")
    void testGetProduitById_Success() throws Exception {
        // Given
        when(produitService.findById(produitId)).thenReturn(savedProduitDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/produits/{id}", produitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(produitId)))
                .andExpect(jsonPath("$.nom", is("Ordinateur Portable")))
                .andExpect(jsonPath("$.poids", is(2.5)))
                .andExpect(jsonPath("$.prix", is(999.99)));

        verify(produitService, times(1)).findById(produitId);
    }

    @Test
    @DisplayName("GET /api/v1/produits/{id} - Produit non trouvé (404)")
    void testGetProduitById_NotFound() throws Exception {
        // Given
        when(produitService.findById(produitId))
                .thenThrow(new ResourceNotFoundException("Produit non trouvé"));

        // When & Then
        mockMvc.perform(get("/api/v1/produits/{id}", produitId))
                .andExpect(status().isNotFound());

        verify(produitService, times(1)).findById(produitId);
    }

    @Test
    @DisplayName("GET /api/v1/produits - Récupérer tous les produits (200)")
    void testGetAllProduits_Success() throws Exception {
        // Given
        ProduitDTO produit2 = new ProduitDTO();
        produit2.setId("produit-456");
        produit2.setNom("Smartphone");
        produit2.setPoids(0.2);
        produit2.setPrix(new BigDecimal("599.99"));

        List<ProduitDTO> produits = Arrays.asList(savedProduitDTO, produit2);
        Page<ProduitDTO> page = new PageImpl<>(produits, PageRequest.of(0, 10), 2);

        when(produitService.findAll(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/produits")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].nom", is("Ordinateur Portable")))
                .andExpect(jsonPath("$.content[1].nom", is("Smartphone")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(produitService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/produits - Récupérer liste vide (200)")
    void testGetAllProduits_EmptyList() throws Exception {
        // Given
        Page<ProduitDTO> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(produitService.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/v1/produits")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

        verify(produitService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("PUT /api/v1/produits/{id} - Mettre à jour un produit (200)")
    void testUpdateProduit_Success() throws Exception {
        // Given
        ProduitDTO updatedDTO = new ProduitDTO();
        updatedDTO.setId(produitId);
        updatedDTO.setNom("Ordinateur Portable Pro");
        updatedDTO.setPoids(3.0);
        updatedDTO.setPrix(new BigDecimal("1299.99"));

        when(produitService.update(eq(produitId), any(ProduitDTO.class))).thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/produits/{id}", produitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduitDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is("Ordinateur Portable Pro")))
                .andExpect(jsonPath("$.poids", is(3.0)))
                .andExpect(jsonPath("$.prix", is(1299.99)));

        verify(produitService, times(1)).update(eq(produitId), any(ProduitDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/produits/{id} - Validation échouée (400)")
    void testUpdateProduit_ValidationFailed() throws Exception {
        // Given
        validProduitDTO.setPoids(-5.0);

        // When & Then
        mockMvc.perform(put("/api/v1/produits/{id}", produitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduitDTO)))
                .andExpect(status().isBadRequest());

        verify(produitService, never()).update(eq(produitId), any(ProduitDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/produits/{id} - Produit non trouvé (404)")
    void testUpdateProduit_NotFound() throws Exception {
        // Given
        when(produitService.update(eq(produitId), any(ProduitDTO.class)))
                .thenThrow(new ResourceNotFoundException("Produit non trouvé"));

        // When & Then
        mockMvc.perform(put("/api/v1/produits/{id}", produitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduitDTO)))
                .andExpect(status().isNotFound());

        verify(produitService, times(1)).update(eq(produitId), any(ProduitDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/produits/{id} - Supprimer un produit (204)")
    void testDeleteProduit_Success() throws Exception {
        // Given
        doNothing().when(produitService).delete(produitId);

        // When & Then
        mockMvc.perform(delete("/api/v1/produits/{id}", produitId))
                .andExpect(status().isNoContent());

        verify(produitService, times(1)).delete(produitId);
    }

    @Test
    @DisplayName("DELETE /api/v1/produits/{id} - Produit non trouvé (404)")
    void testDeleteProduit_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Produit non trouvé"))
                .when(produitService).delete(produitId);

        // When & Then
        mockMvc.perform(delete("/api/v1/produits/{id}", produitId))
                .andExpect(status().isNotFound());

        verify(produitService, times(1)).delete(produitId);
    }
}

