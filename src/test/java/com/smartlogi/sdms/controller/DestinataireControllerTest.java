package com.smartlogi.sdms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.sdms.config.TestSecurityConfig;
import com.smartlogi.sdms.dto.DestinataireDTO;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.service.interfaces.DestinataireService;
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

@WebMvcTest(DestinataireController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
@DisplayName("Tests Unitaires pour DestinataireController")
class DestinataireControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DestinataireService destinataireService;

    private DestinataireDTO validDestinataireDTO;
    private DestinataireDTO savedDestinataireDTO;
    private String destinataireId;

    @BeforeEach
    void setUp() {
        destinataireId = "dest-123";

        validDestinataireDTO = new DestinataireDTO();
        validDestinataireDTO.setNom("Martin");
        validDestinataireDTO.setPrenom("Sophie");
        validDestinataireDTO.setEmail("sophie.martin@example.com");
        validDestinataireDTO.setTelephone("0612345678");
        validDestinataireDTO.setAdresse("456 Avenue des Lilas, Lyon");

        savedDestinataireDTO = new DestinataireDTO();
        savedDestinataireDTO.setId(destinataireId);
        savedDestinataireDTO.setNom("Martin");
        savedDestinataireDTO.setPrenom("Sophie");
        savedDestinataireDTO.setEmail("sophie.martin@example.com");
        savedDestinataireDTO.setTelephone("0612345678");
        savedDestinataireDTO.setAdresse("456 Avenue des Lilas, Lyon");
    }

    @Test
    @DisplayName("POST /api/v1/destinataires - Créer un destinataire avec succès (201)")
    void testCreateDestinataire_Success() throws Exception {
        // Given
        when(destinataireService.save(any(DestinataireDTO.class))).thenReturn(savedDestinataireDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/destinataires")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDestinataireDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/destinataires/" + destinataireId))
                .andExpect(jsonPath("$.id", is(destinataireId)))
                .andExpect(jsonPath("$.nom", is("Martin")))
                .andExpect(jsonPath("$.prenom", is("Sophie")))
                .andExpect(jsonPath("$.email", is("sophie.martin@example.com")))
                .andExpect(jsonPath("$.telephone", is("0612345678")))
                .andExpect(jsonPath("$.adresse", is("456 Avenue des Lilas, Lyon")));

        verify(destinataireService, times(1)).save(any(DestinataireDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/destinataires - Validation échouée: nom manquant (400)")
    void testCreateDestinataire_ValidationFailed_NomMissing() throws Exception {
        // Given
        validDestinataireDTO.setNom(null);

        // When & Then
        mockMvc.perform(post("/api/v1/destinataires")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDestinataireDTO)))
                .andExpect(status().isBadRequest());

        verify(destinataireService, never()).save(any(DestinataireDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/destinataires - Validation échouée: email invalide (400)")
    void testCreateDestinataire_ValidationFailed_EmailInvalid() throws Exception {
        // Given
        validDestinataireDTO.setEmail("invalid-email");

        // When & Then
        mockMvc.perform(post("/api/v1/destinataires")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDestinataireDTO)))
                .andExpect(status().isBadRequest());

        verify(destinataireService, never()).save(any(DestinataireDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/destinataires - Validation échouée: adresse trop courte (400)")
    void testCreateDestinataire_ValidationFailed_AdresseTooShort() throws Exception {
        // Given
        validDestinataireDTO.setAdresse("Rue 123");

        // When & Then
        mockMvc.perform(post("/api/v1/destinataires")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDestinataireDTO)))
                .andExpect(status().isBadRequest());

        verify(destinataireService, never()).save(any(DestinataireDTO.class));
    }

    @Test
    @DisplayName("GET /api/v1/destinataires/{id} - Récupérer un destinataire avec succès (200)")
    void testGetDestinataireById_Success() throws Exception {
        // Given
        when(destinataireService.findById(destinataireId)).thenReturn(savedDestinataireDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/destinataires/{id}", destinataireId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(destinataireId)))
                .andExpect(jsonPath("$.nom", is("Martin")))
                .andExpect(jsonPath("$.prenom", is("Sophie")))
                .andExpect(jsonPath("$.email", is("sophie.martin@example.com")));

        verify(destinataireService, times(1)).findById(destinataireId);
    }

    @Test
    @DisplayName("GET /api/v1/destinataires/{id} - Destinataire non trouvé (404)")
    void testGetDestinataireById_NotFound() throws Exception {
        // Given
        when(destinataireService.findById(destinataireId))
                .thenThrow(new ResourceNotFoundException("Destinataire non trouvé"));

        // When & Then
        mockMvc.perform(get("/api/v1/destinataires/{id}", destinataireId))
                .andExpect(status().isNotFound());

        verify(destinataireService, times(1)).findById(destinataireId);
    }

    @Test
    @DisplayName("GET /api/v1/destinataires - Récupérer tous les destinataires (200)")
    void testGetAllDestinataires_Success() throws Exception {
        // Given
        DestinataireDTO dest2 = new DestinataireDTO();
        dest2.setId("dest-456");
        dest2.setNom("Bernard");
        dest2.setPrenom("Claire");
        dest2.setEmail("claire.bernard@example.com");
        dest2.setTelephone("0687654321");
        dest2.setAdresse("789 Rue du Commerce, Paris");

        List<DestinataireDTO> destinataires = Arrays.asList(savedDestinataireDTO, dest2);
        Page<DestinataireDTO> page = new PageImpl<>(destinataires, PageRequest.of(0, 10), 2);

        when(destinataireService.findAll(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/destinataires")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].nom", is("Martin")))
                .andExpect(jsonPath("$.content[1].nom", is("Bernard")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(destinataireService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("PUT /api/v1/destinataires/{id} - Mettre à jour un destinataire (200)")
    void testUpdateDestinataire_Success() throws Exception {
        // Given
        DestinataireDTO updatedDTO = new DestinataireDTO();
        updatedDTO.setId(destinataireId);
        updatedDTO.setNom("Martin Updated");
        updatedDTO.setPrenom("Sophie Updated");
        updatedDTO.setEmail("sophie.updated@example.com");
        updatedDTO.setTelephone("0698765432");
        updatedDTO.setAdresse("999 Boulevard Nouveau, Marseille");

        when(destinataireService.update(eq(destinataireId), any(DestinataireDTO.class)))
                .thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/destinataires/{id}", destinataireId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDestinataireDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is("Martin Updated")))
                .andExpect(jsonPath("$.email", is("sophie.updated@example.com")));

        verify(destinataireService, times(1)).update(eq(destinataireId), any(DestinataireDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/destinataires/{id} - Destinataire non trouvé (404)")
    void testUpdateDestinataire_NotFound() throws Exception {
        // Given
        when(destinataireService.update(eq(destinataireId), any(DestinataireDTO.class)))
                .thenThrow(new ResourceNotFoundException("Destinataire non trouvé"));

        // When & Then
        mockMvc.perform(put("/api/v1/destinataires/{id}", destinataireId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDestinataireDTO)))
                .andExpect(status().isNotFound());

        verify(destinataireService, times(1)).update(eq(destinataireId), any(DestinataireDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/destinataires/{id} - Supprimer un destinataire (204)")
    void testDeleteDestinataire_Success() throws Exception {
        // Given
        doNothing().when(destinataireService).delete(destinataireId);

        // When & Then
        mockMvc.perform(delete("/api/v1/destinataires/{id}", destinataireId))
                .andExpect(status().isNoContent());

        verify(destinataireService, times(1)).delete(destinataireId);
    }

    @Test
    @DisplayName("DELETE /api/v1/destinataires/{id} - Destinataire non trouvé (404)")
    void testDeleteDestinataire_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Destinataire non trouvé"))
                .when(destinataireService).delete(destinataireId);

        // When & Then
        mockMvc.perform(delete("/api/v1/destinataires/{id}", destinataireId))
                .andExpect(status().isNotFound());

        verify(destinataireService, times(1)).delete(destinataireId);
    }
}

