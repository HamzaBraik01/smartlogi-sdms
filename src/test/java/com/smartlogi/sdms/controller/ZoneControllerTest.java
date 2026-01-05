package com.smartlogi.sdms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.sdms.config.TestSecurityConfig;
import com.smartlogi.sdms.dto.ZoneDTO;
import com.smartlogi.sdms.exception.InvalidDataException;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.service.interfaces.ZoneService;
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

@WebMvcTest(ZoneController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
@DisplayName("Tests Unitaires pour ZoneController")
class ZoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ZoneService zoneService;

    private ZoneDTO validZoneDTO;
    private ZoneDTO savedZoneDTO;
    private String zoneId;

    @BeforeEach
    void setUp() {
        zoneId = "zone-123";

        validZoneDTO = new ZoneDTO();
        validZoneDTO.setNom("Zone Nord");
        validZoneDTO.setCodePostal("75001");
        validZoneDTO.setVille("Paris");

        savedZoneDTO = new ZoneDTO();
        savedZoneDTO.setId(zoneId);
        savedZoneDTO.setNom("Zone Nord");
        savedZoneDTO.setCodePostal("75001");
        savedZoneDTO.setVille("Paris");
    }

    @Test
    @DisplayName("POST /api/v1/zones - Créer une zone avec succès (201)")
    void testCreateZone_Success() throws Exception {
        // Given
        when(zoneService.save(any(ZoneDTO.class))).thenReturn(savedZoneDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZoneDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/zones/" + zoneId))
                .andExpect(jsonPath("$.id", is(zoneId)))
                .andExpect(jsonPath("$.nom", is("Zone Nord")))
                .andExpect(jsonPath("$.codePostal", is("75001")))
                .andExpect(jsonPath("$.ville", is("Paris")));

        verify(zoneService, times(1)).save(any(ZoneDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/zones - Validation échouée: nom manquant (400)")
    void testCreateZone_ValidationFailed_NomMissing() throws Exception {
        // Given
        validZoneDTO.setNom(null);

        // When & Then
        mockMvc.perform(post("/api/v1/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZoneDTO)))
                .andExpect(status().isBadRequest());

        verify(zoneService, never()).save(any(ZoneDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/zones - Validation échouée: nom trop court (400)")
    void testCreateZone_ValidationFailed_NomTooShort() throws Exception {
        // Given
        validZoneDTO.setNom("AB");

        // When & Then
        mockMvc.perform(post("/api/v1/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZoneDTO)))
                .andExpect(status().isBadRequest());

        verify(zoneService, never()).save(any(ZoneDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/zones - Validation échouée: code postal manquant (400)")
    void testCreateZone_ValidationFailed_CodePostalMissing() throws Exception {
        // Given
        validZoneDTO.setCodePostal(null);

        // When & Then
        mockMvc.perform(post("/api/v1/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZoneDTO)))
                .andExpect(status().isBadRequest());

        verify(zoneService, never()).save(any(ZoneDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/zones - Validation échouée: code postal trop court (400)")
    void testCreateZone_ValidationFailed_CodePostalTooShort() throws Exception {
        // Given
        validZoneDTO.setCodePostal("123");

        // When & Then
        mockMvc.perform(post("/api/v1/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZoneDTO)))
                .andExpect(status().isBadRequest());

        verify(zoneService, never()).save(any(ZoneDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/zones - Validation échouée: ville manquante (400)")
    void testCreateZone_ValidationFailed_VilleMissing() throws Exception {
        // Given
        validZoneDTO.setVille(null);

        // When & Then
        mockMvc.perform(post("/api/v1/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZoneDTO)))
                .andExpect(status().isBadRequest());

        verify(zoneService, never()).save(any(ZoneDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/zones - Zone déjà existante (400)")
    void testCreateZone_DuplicateZone() throws Exception {
        // Given
        when(zoneService.save(any(ZoneDTO.class)))
                .thenThrow(new InvalidDataException("Zone déjà existante"));

        // When & Then
        mockMvc.perform(post("/api/v1/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZoneDTO)))
                .andExpect(status().isBadRequest());

        verify(zoneService, times(1)).save(any(ZoneDTO.class));
    }

    @Test
    @DisplayName("GET /api/v1/zones/{id} - Récupérer une zone avec succès (200)")
    void testGetZoneById_Success() throws Exception {
        // Given
        when(zoneService.findById(zoneId)).thenReturn(savedZoneDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/zones/{id}", zoneId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(zoneId)))
                .andExpect(jsonPath("$.nom", is("Zone Nord")))
                .andExpect(jsonPath("$.codePostal", is("75001")))
                .andExpect(jsonPath("$.ville", is("Paris")));

        verify(zoneService, times(1)).findById(zoneId);
    }

    @Test
    @DisplayName("GET /api/v1/zones/{id} - Zone non trouvée (404)")
    void testGetZoneById_NotFound() throws Exception {
        // Given
        when(zoneService.findById(zoneId))
                .thenThrow(new ResourceNotFoundException("Zone non trouvée avec l'ID: " + zoneId));

        // When & Then
        mockMvc.perform(get("/api/v1/zones/{id}", zoneId))
                .andExpect(status().isNotFound());

        verify(zoneService, times(1)).findById(zoneId);
    }

    @Test
    @DisplayName("GET /api/v1/zones - Récupérer toutes les zones paginées (200)")
    void testGetAllZones_Success() throws Exception {
        // Given
        ZoneDTO zone2 = new ZoneDTO();
        zone2.setId("zone-456");
        zone2.setNom("Zone Sud");
        zone2.setCodePostal("13001");
        zone2.setVille("Marseille");

        List<ZoneDTO> zones = Arrays.asList(savedZoneDTO, zone2);
        Page<ZoneDTO> page = new PageImpl<>(zones, PageRequest.of(0, 10), 2);

        when(zoneService.findAll(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/zones")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(zoneId)))
                .andExpect(jsonPath("$.content[0].nom", is("Zone Nord")))
                .andExpect(jsonPath("$.content[1].id", is("zone-456")))
                .andExpect(jsonPath("$.content[1].nom", is("Zone Sud")))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));

        verify(zoneService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/zones - Récupérer liste vide (200)")
    void testGetAllZones_EmptyList() throws Exception {
        // Given
        Page<ZoneDTO> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(zoneService.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/v1/zones")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

        verify(zoneService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("PUT /api/v1/zones/{id} - Mettre à jour une zone avec succès (200)")
    void testUpdateZone_Success() throws Exception {
        // Given
        ZoneDTO updatedDTO = new ZoneDTO();
        updatedDTO.setId(zoneId);
        updatedDTO.setNom("Zone Nord Modifiée");
        updatedDTO.setCodePostal("75002");
        updatedDTO.setVille("Paris Updated");

        when(zoneService.update(eq(zoneId), any(ZoneDTO.class))).thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/zones/{id}", zoneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZoneDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(zoneId)))
                .andExpect(jsonPath("$.nom", is("Zone Nord Modifiée")))
                .andExpect(jsonPath("$.codePostal", is("75002")))
                .andExpect(jsonPath("$.ville", is("Paris Updated")));

        verify(zoneService, times(1)).update(eq(zoneId), any(ZoneDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/zones/{id} - Validation échouée: données invalides (400)")
    void testUpdateZone_ValidationFailed() throws Exception {
        // Given
        validZoneDTO.setCodePostal("12");

        // When & Then
        mockMvc.perform(put("/api/v1/zones/{id}", zoneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZoneDTO)))
                .andExpect(status().isBadRequest());

        verify(zoneService, never()).update(eq(zoneId), any(ZoneDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/zones/{id} - Zone non trouvée (404)")
    void testUpdateZone_NotFound() throws Exception {
        // Given
        when(zoneService.update(eq(zoneId), any(ZoneDTO.class)))
                .thenThrow(new ResourceNotFoundException("Zone non trouvée avec l'ID: " + zoneId));

        // When & Then
        mockMvc.perform(put("/api/v1/zones/{id}", zoneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZoneDTO)))
                .andExpect(status().isNotFound());

        verify(zoneService, times(1)).update(eq(zoneId), any(ZoneDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/zones/{id} - Supprimer une zone avec succès (204)")
    void testDeleteZone_Success() throws Exception {
        // Given
        doNothing().when(zoneService).delete(zoneId);

        // When & Then
        mockMvc.perform(delete("/api/v1/zones/{id}", zoneId))
                .andExpect(status().isNoContent());

        verify(zoneService, times(1)).delete(zoneId);
    }

    @Test
    @DisplayName("DELETE /api/v1/zones/{id} - Zone non trouvée (404)")
    void testDeleteZone_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Zone non trouvée avec l'ID: " + zoneId))
                .when(zoneService).delete(zoneId);

        // When & Then
        mockMvc.perform(delete("/api/v1/zones/{id}", zoneId))
                .andExpect(status().isNotFound());

        verify(zoneService, times(1)).delete(zoneId);
    }
}

