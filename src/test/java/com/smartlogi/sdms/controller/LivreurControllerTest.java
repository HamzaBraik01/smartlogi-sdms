package com.smartlogi.sdms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.sdms.config.TestSecurityConfig;
import com.smartlogi.sdms.dto.LivreurDTO;
import com.smartlogi.sdms.exception.InvalidDataException;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.service.interfaces.LivreurService;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LivreurController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
@DisplayName("Tests Unitaires pour LivreurController")
class LivreurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LivreurService livreurService;

    private LivreurDTO validLivreurDTO;
    private LivreurDTO savedLivreurDTO;
    private String livreurId;

    @BeforeEach
    void setUp() {
        livreurId = "livreur-123";

        validLivreurDTO = new LivreurDTO();
        validLivreurDTO.setNom("Dupont");
        validLivreurDTO.setPrenom("Jean");
        validLivreurDTO.setEmail("jean.dupont@delivery.com");
        validLivreurDTO.setTelephone("0612345678");
        validLivreurDTO.setVehicule("Moto 125cc");
        validLivreurDTO.setZoneId("zone-123");

        savedLivreurDTO = new LivreurDTO();
        savedLivreurDTO.setId(livreurId);
        savedLivreurDTO.setNom("Dupont");
        savedLivreurDTO.setPrenom("Jean");
        savedLivreurDTO.setEmail("jean.dupont@delivery.com");
        savedLivreurDTO.setTelephone("0612345678");
        savedLivreurDTO.setVehicule("Moto 125cc");
        savedLivreurDTO.setZoneId("zone-123");
        savedLivreurDTO.setZoneNom("Zone Nord");
    }

    @Test
    @DisplayName("POST /api/v1/livreurs - Créer un livreur avec succès (201)")
    void testCreateLivreur_Success() throws Exception {
        // Given
        when(livreurService.save(any(LivreurDTO.class))).thenReturn(savedLivreurDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/livreurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLivreurDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/livreurs/" + livreurId))
                .andExpect(jsonPath("$.id", is(livreurId)))
                .andExpect(jsonPath("$.nom", is("Dupont")))
                .andExpect(jsonPath("$.prenom", is("Jean")))
                .andExpect(jsonPath("$.email", is("jean.dupont@delivery.com")))
                .andExpect(jsonPath("$.telephone", is("0612345678")))
                .andExpect(jsonPath("$.vehicule", is("Moto 125cc")))
                .andExpect(jsonPath("$.zoneId", is("zone-123")));

        verify(livreurService, times(1)).save(any(LivreurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/livreurs - Validation échouée: nom manquant (400)")
    void testCreateLivreur_ValidationFailed_NomMissing() throws Exception {
        // Given
        validLivreurDTO.setNom(null);

        // When & Then
        mockMvc.perform(post("/api/v1/livreurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLivreurDTO)))
                .andExpect(status().isBadRequest());

        verify(livreurService, never()).save(any(LivreurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/livreurs - Validation échouée: nom trop court (400)")
    void testCreateLivreur_ValidationFailed_NomTooShort() throws Exception {
        // Given
        validLivreurDTO.setNom("D");

        // When & Then
        mockMvc.perform(post("/api/v1/livreurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLivreurDTO)))
                .andExpect(status().isBadRequest());

        verify(livreurService, never()).save(any(LivreurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/livreurs - Validation échouée: prénom manquant (400)")
    void testCreateLivreur_ValidationFailed_PrenomMissing() throws Exception {
        // Given
        validLivreurDTO.setPrenom(null);

        // When & Then
        mockMvc.perform(post("/api/v1/livreurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLivreurDTO)))
                .andExpect(status().isBadRequest());

        verify(livreurService, never()).save(any(LivreurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/livreurs - Validation échouée: email invalide (400)")
    void testCreateLivreur_ValidationFailed_EmailInvalid() throws Exception {
        // Given
        validLivreurDTO.setEmail("invalid-email");

        // When & Then
        mockMvc.perform(post("/api/v1/livreurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLivreurDTO)))
                .andExpect(status().isBadRequest());

        verify(livreurService, never()).save(any(LivreurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/livreurs - Validation échouée: email manquant (400)")
    void testCreateLivreur_ValidationFailed_EmailMissing() throws Exception {
        // Given
        validLivreurDTO.setEmail(null);

        // When & Then
        mockMvc.perform(post("/api/v1/livreurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLivreurDTO)))
                .andExpect(status().isBadRequest());

        verify(livreurService, never()).save(any(LivreurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/livreurs - Validation échouée: téléphone manquant (400)")
    void testCreateLivreur_ValidationFailed_TelephoneMissing() throws Exception {
        // Given
        validLivreurDTO.setTelephone(null);

        // When & Then
        mockMvc.perform(post("/api/v1/livreurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLivreurDTO)))
                .andExpect(status().isBadRequest());

        verify(livreurService, never()).save(any(LivreurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/livreurs - Validation échouée: téléphone trop court (400)")
    void testCreateLivreur_ValidationFailed_TelephoneTooShort() throws Exception {
        // Given
        validLivreurDTO.setTelephone("123");

        // When & Then
        mockMvc.perform(post("/api/v1/livreurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLivreurDTO)))
                .andExpect(status().isBadRequest());

        verify(livreurService, never()).save(any(LivreurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/livreurs - Email déjà utilisé (400)")
    void testCreateLivreur_DuplicateEmail() throws Exception {
        // Given
        when(livreurService.save(any(LivreurDTO.class)))
                .thenThrow(new InvalidDataException("Email déjà utilisé"));

        // When & Then
        mockMvc.perform(post("/api/v1/livreurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLivreurDTO)))
                .andExpect(status().isBadRequest());

        verify(livreurService, times(1)).save(any(LivreurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/livreurs - Zone invalide (400)")
    void testCreateLivreur_InvalidZone() throws Exception {
        // Given
        when(livreurService.save(any(LivreurDTO.class)))
                .thenThrow(new InvalidDataException("Zone invalide"));

        // When & Then
        mockMvc.perform(post("/api/v1/livreurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLivreurDTO)))
                .andExpect(status().isBadRequest());

        verify(livreurService, times(1)).save(any(LivreurDTO.class));
    }

    @Test
    @DisplayName("GET /api/v1/livreurs/{id} - Récupérer un livreur avec succès (200)")
    void testGetLivreurById_Success() throws Exception {
        // Given
        when(livreurService.findById(livreurId)).thenReturn(savedLivreurDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/livreurs/{id}", livreurId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(livreurId)))
                .andExpect(jsonPath("$.nom", is("Dupont")))
                .andExpect(jsonPath("$.prenom", is("Jean")))
                .andExpect(jsonPath("$.email", is("jean.dupont@delivery.com")))
                .andExpect(jsonPath("$.telephone", is("0612345678")))
                .andExpect(jsonPath("$.vehicule", is("Moto 125cc")))
                .andExpect(jsonPath("$.zoneId", is("zone-123")));

        verify(livreurService, times(1)).findById(livreurId);
    }

    @Test
    @DisplayName("GET /api/v1/livreurs/{id} - Livreur non trouvé (404)")
    void testGetLivreurById_NotFound() throws Exception {
        // Given
        when(livreurService.findById(livreurId))
                .thenThrow(new ResourceNotFoundException("Livreur non trouvé avec l'ID: " + livreurId));

        // When & Then
        mockMvc.perform(get("/api/v1/livreurs/{id}", livreurId))
                .andExpect(status().isNotFound());

        verify(livreurService, times(1)).findById(livreurId);
    }

    @Test
    @DisplayName("GET /api/v1/livreurs - Récupérer tous les livreurs paginés (200)")
    void testGetAllLivreurs_Success() throws Exception {
        // Given
        LivreurDTO livreur2 = new LivreurDTO();
        livreur2.setId("livreur-456");
        livreur2.setNom("Martin");
        livreur2.setPrenom("Sophie");
        livreur2.setEmail("sophie.martin@delivery.com");
        livreur2.setTelephone("0687654321");
        livreur2.setVehicule("Voiture");
        livreur2.setZoneId("zone-456");

        List<LivreurDTO> livreurs = Arrays.asList(savedLivreurDTO, livreur2);
        Page<LivreurDTO> page = new PageImpl<>(livreurs, PageRequest.of(0, 10), 2);

        when(livreurService.findAll(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/livreurs")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(livreurId)))
                .andExpect(jsonPath("$.content[0].nom", is("Dupont")))
                .andExpect(jsonPath("$.content[1].id", is("livreur-456")))
                .andExpect(jsonPath("$.content[1].nom", is("Martin")))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));

        verify(livreurService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/livreurs - Récupérer liste vide (200)")
    void testGetAllLivreurs_EmptyList() throws Exception {
        // Given
        Page<LivreurDTO> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(livreurService.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/v1/livreurs")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

        verify(livreurService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("PUT /api/v1/livreurs/{id} - Mettre à jour un livreur avec succès (200)")
    void testUpdateLivreur_Success() throws Exception {
        // Given
        LivreurDTO updatedDTO = new LivreurDTO();
        updatedDTO.setId(livreurId);
        updatedDTO.setNom("Dupont Updated");
        updatedDTO.setPrenom("Jean Updated");
        updatedDTO.setEmail("jean.updated@delivery.com");
        updatedDTO.setTelephone("0698765432");
        updatedDTO.setVehicule("Camionnette");
        updatedDTO.setZoneId("zone-789");

        when(livreurService.update(eq(livreurId), any(LivreurDTO.class))).thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/livreurs/{id}", livreurId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLivreurDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(livreurId)))
                .andExpect(jsonPath("$.nom", is("Dupont Updated")))
                .andExpect(jsonPath("$.prenom", is("Jean Updated")))
                .andExpect(jsonPath("$.email", is("jean.updated@delivery.com")))
                .andExpect(jsonPath("$.telephone", is("0698765432")))
                .andExpect(jsonPath("$.vehicule", is("Camionnette")));

        verify(livreurService, times(1)).update(eq(livreurId), any(LivreurDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/livreurs/{id} - Validation échouée: données invalides (400)")
    void testUpdateLivreur_ValidationFailed() throws Exception {
        // Given
        validLivreurDTO.setEmail("invalid-email");

        // When & Then
        mockMvc.perform(put("/api/v1/livreurs/{id}", livreurId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLivreurDTO)))
                .andExpect(status().isBadRequest());

        verify(livreurService, never()).update(eq(livreurId), any(LivreurDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/livreurs/{id} - Livreur non trouvé (404)")
    void testUpdateLivreur_NotFound() throws Exception {
        // Given
        when(livreurService.update(eq(livreurId), any(LivreurDTO.class)))
                .thenThrow(new ResourceNotFoundException("Livreur non trouvé avec l'ID: " + livreurId));

        // When & Then
        mockMvc.perform(put("/api/v1/livreurs/{id}", livreurId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLivreurDTO)))
                .andExpect(status().isNotFound());

        verify(livreurService, times(1)).update(eq(livreurId), any(LivreurDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/livreurs/{id} - Supprimer un livreur avec succès (204)")
    void testDeleteLivreur_Success() throws Exception {
        // Given
        doNothing().when(livreurService).delete(livreurId);

        // When & Then
        mockMvc.perform(delete("/api/v1/livreurs/{id}", livreurId))
                .andExpect(status().isNoContent());

        verify(livreurService, times(1)).delete(livreurId);
    }

    @Test
    @DisplayName("DELETE /api/v1/livreurs/{id} - Livreur non trouvé (404)")
    void testDeleteLivreur_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Livreur non trouvé avec l'ID: " + livreurId))
                .when(livreurService).delete(livreurId);

        // When & Then
        mockMvc.perform(delete("/api/v1/livreurs/{id}", livreurId))
                .andExpect(status().isNotFound());

        verify(livreurService, times(1)).delete(livreurId);
    }
}

