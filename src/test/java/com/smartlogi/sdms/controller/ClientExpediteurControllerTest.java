package com.smartlogi.sdms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.sdms.dto.ClientExpediteurDTO;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.exception.InvalidDataException;
import com.smartlogi.sdms.service.interfaces.ClientExpediteurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientExpediteurController.class)
@DisplayName("Tests Unitaires pour ClientExpediteurController")
class ClientExpediteurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientExpediteurService clientExpediteurService;

    private ClientExpediteurDTO validClientDTO;
    private ClientExpediteurDTO savedClientDTO;
    private String clientId;

    @BeforeEach
    void setUp() {
        clientId = "client-123";

        validClientDTO = new ClientExpediteurDTO();
        validClientDTO.setNom("Doe");
        validClientDTO.setPrenom("John");
        validClientDTO.setEmail("john.doe@example.com");
        validClientDTO.setTelephone("0612345678");
        validClientDTO.setAdresse("123 Rue de la Paix, Paris");

        savedClientDTO = new ClientExpediteurDTO();
        savedClientDTO.setId(clientId);
        savedClientDTO.setNom("Doe");
        savedClientDTO.setPrenom("John");
        savedClientDTO.setEmail("john.doe@example.com");
        savedClientDTO.setTelephone("0612345678");
        savedClientDTO.setAdresse("123 Rue de la Paix, Paris");
    }

    @Test
    @DisplayName("POST /api/v1/client-expediteurs - Créer un client avec succès (201)")
    void testCreateClientExpediteur_Success() throws Exception {
        // Given
        when(clientExpediteurService.save(any(ClientExpediteurDTO.class))).thenReturn(savedClientDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/client-expediteurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/client-expediteurs/" + clientId))
                .andExpect(jsonPath("$.id", is(clientId)))
                .andExpect(jsonPath("$.nom", is("Doe")))
                .andExpect(jsonPath("$.prenom", is("John")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.telephone", is("0612345678")))
                .andExpect(jsonPath("$.adresse", is("123 Rue de la Paix, Paris")));

        verify(clientExpediteurService, times(1)).save(any(ClientExpediteurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/client-expediteurs - Validation échouée: nom manquant (400)")
    void testCreateClientExpediteur_ValidationFailed_NomMissing() throws Exception {
        // Given
        validClientDTO.setNom(null);

        // When & Then
        mockMvc.perform(post("/api/v1/client-expediteurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientDTO)))
                .andExpect(status().isBadRequest());

        verify(clientExpediteurService, never()).save(any(ClientExpediteurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/client-expediteurs - Validation échouée: nom trop court (400)")
    void testCreateClientExpediteur_ValidationFailed_NomTooShort() throws Exception {
        // Given
        validClientDTO.setNom("D");

        // When & Then
        mockMvc.perform(post("/api/v1/client-expediteurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientDTO)))
                .andExpect(status().isBadRequest());

        verify(clientExpediteurService, never()).save(any(ClientExpediteurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/client-expediteurs - Validation échouée: prénom manquant (400)")
    void testCreateClientExpediteur_ValidationFailed_PrenomMissing() throws Exception {
        // Given
        validClientDTO.setPrenom(null);

        // When & Then
        mockMvc.perform(post("/api/v1/client-expediteurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientDTO)))
                .andExpect(status().isBadRequest());

        verify(clientExpediteurService, never()).save(any(ClientExpediteurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/client-expediteurs - Validation échouée: email invalide (400)")
    void testCreateClientExpediteur_ValidationFailed_EmailInvalid() throws Exception {
        // Given
        validClientDTO.setEmail("invalid-email");

        // When & Then
        mockMvc.perform(post("/api/v1/client-expediteurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientDTO)))
                .andExpect(status().isBadRequest());

        verify(clientExpediteurService, never()).save(any(ClientExpediteurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/client-expediteurs - Validation échouée: email manquant (400)")
    void testCreateClientExpediteur_ValidationFailed_EmailMissing() throws Exception {
        // Given
        validClientDTO.setEmail(null);

        // When & Then
        mockMvc.perform(post("/api/v1/client-expediteurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientDTO)))
                .andExpect(status().isBadRequest());

        verify(clientExpediteurService, never()).save(any(ClientExpediteurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/client-expediteurs - Validation échouée: téléphone manquant (400)")
    void testCreateClientExpediteur_ValidationFailed_TelephoneMissing() throws Exception {
        // Given
        validClientDTO.setTelephone(null);

        // When & Then
        mockMvc.perform(post("/api/v1/client-expediteurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientDTO)))
                .andExpect(status().isBadRequest());

        verify(clientExpediteurService, never()).save(any(ClientExpediteurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/client-expediteurs - Validation échouée: téléphone trop court (400)")
    void testCreateClientExpediteur_ValidationFailed_TelephoneTooShort() throws Exception {
        // Given
        validClientDTO.setTelephone("123");

        // When & Then
        mockMvc.perform(post("/api/v1/client-expediteurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientDTO)))
                .andExpect(status().isBadRequest());

        verify(clientExpediteurService, never()).save(any(ClientExpediteurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/client-expediteurs - Validation échouée: adresse manquante (400)")
    void testCreateClientExpediteur_ValidationFailed_AdresseMissing() throws Exception {
        // Given
        validClientDTO.setAdresse(null);

        // When & Then
        mockMvc.perform(post("/api/v1/client-expediteurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientDTO)))
                .andExpect(status().isBadRequest());

        verify(clientExpediteurService, never()).save(any(ClientExpediteurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/client-expediteurs - Validation échouée: adresse trop courte (400)")
    void testCreateClientExpediteur_ValidationFailed_AdresseTooShort() throws Exception {
        // Given
        validClientDTO.setAdresse("Rue 123");

        // When & Then
        mockMvc.perform(post("/api/v1/client-expediteurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientDTO)))
                .andExpect(status().isBadRequest());

        verify(clientExpediteurService, never()).save(any(ClientExpediteurDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/client-expediteurs - Email déjà utilisé (400)")
    void testCreateClientExpediteur_DuplicateEmail() throws Exception {
        // Given
        when(clientExpediteurService.save(any(ClientExpediteurDTO.class)))
                .thenThrow(new InvalidDataException("Email déjà utilisé"));

        // When & Then
        mockMvc.perform(post("/api/v1/client-expediteurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientDTO)))
                .andExpect(status().isBadRequest());

        verify(clientExpediteurService, times(1)).save(any(ClientExpediteurDTO.class));
    }

    @Test
    @DisplayName("GET /api/v1/client-expediteurs/{id} - Récupérer un client avec succès (200)")
    void testGetClientExpediteurById_Success() throws Exception {
        // Given
        when(clientExpediteurService.findById(clientId)).thenReturn(savedClientDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/client-expediteurs/{id}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(clientId)))
                .andExpect(jsonPath("$.nom", is("Doe")))
                .andExpect(jsonPath("$.prenom", is("John")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.telephone", is("0612345678")))
                .andExpect(jsonPath("$.adresse", is("123 Rue de la Paix, Paris")));

        verify(clientExpediteurService, times(1)).findById(clientId);
    }

    @Test
    @DisplayName("GET /api/v1/client-expediteurs/{id} - Client non trouvé (404)")
    void testGetClientExpediteurById_NotFound() throws Exception {
        // Given
        when(clientExpediteurService.findById(clientId))
                .thenThrow(new ResourceNotFoundException("Client non trouvé avec l'ID: " + clientId));

        // When & Then
        mockMvc.perform(get("/api/v1/client-expediteurs/{id}", clientId))
                .andExpect(status().isNotFound());

        verify(clientExpediteurService, times(1)).findById(clientId);
    }

    @Test
    @DisplayName("GET /api/v1/client-expediteurs - Récupérer tous les clients paginés (200)")
    void testGetAllClientExpediteurs_Success() throws Exception {
        // Given
        ClientExpediteurDTO client2 = new ClientExpediteurDTO();
        client2.setId("client-456");
        client2.setNom("Smith");
        client2.setPrenom("Jane");
        client2.setEmail("jane.smith@example.com");
        client2.setTelephone("0687654321");
        client2.setAdresse("456 Avenue des Champs, Lyon");

        List<ClientExpediteurDTO> clients = Arrays.asList(savedClientDTO, client2);
        Page<ClientExpediteurDTO> page = new PageImpl<>(clients, PageRequest.of(0, 10), 2);

        when(clientExpediteurService.findAll(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/client-expediteurs")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(clientId)))
                .andExpect(jsonPath("$.content[0].nom", is("Doe")))
                .andExpect(jsonPath("$.content[1].id", is("client-456")))
                .andExpect(jsonPath("$.content[1].nom", is("Smith")))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));

        verify(clientExpediteurService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/client-expediteurs - Récupérer liste vide (200)")
    void testGetAllClientExpediteurs_EmptyList() throws Exception {
        // Given
        Page<ClientExpediteurDTO> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(clientExpediteurService.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/v1/client-expediteurs")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

        verify(clientExpediteurService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("PUT /api/v1/client-expediteurs/{id} - Mettre à jour un client avec succès (200)")
    void testUpdateClientExpediteur_Success() throws Exception {
        // Given
        ClientExpediteurDTO updatedDTO = new ClientExpediteurDTO();
        updatedDTO.setId(clientId);
        updatedDTO.setNom("Doe Updated");
        updatedDTO.setPrenom("John Updated");
        updatedDTO.setEmail("john.updated@example.com");
        updatedDTO.setTelephone("0698765432");
        updatedDTO.setAdresse("789 Boulevard de la Liberté, Marseille");

        when(clientExpediteurService.update(eq(clientId), any(ClientExpediteurDTO.class)))
                .thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/client-expediteurs/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(clientId)))
                .andExpect(jsonPath("$.nom", is("Doe Updated")))
                .andExpect(jsonPath("$.prenom", is("John Updated")))
                .andExpect(jsonPath("$.email", is("john.updated@example.com")))
                .andExpect(jsonPath("$.telephone", is("0698765432")))
                .andExpect(jsonPath("$.adresse", is("789 Boulevard de la Liberté, Marseille")));

        verify(clientExpediteurService, times(1)).update(eq(clientId), any(ClientExpediteurDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/client-expediteurs/{id} - Validation échouée: données invalides (400)")
    void testUpdateClientExpediteur_ValidationFailed() throws Exception {
        // Given
        validClientDTO.setEmail("invalid-email");

        // When & Then
        mockMvc.perform(put("/api/v1/client-expediteurs/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientDTO)))
                .andExpect(status().isBadRequest());

        verify(clientExpediteurService, never()).update(eq(clientId), any(ClientExpediteurDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/client-expediteurs/{id} - Client non trouvé (404)")
    void testUpdateClientExpediteur_NotFound() throws Exception {
        // Given
        when(clientExpediteurService.update(eq(clientId), any(ClientExpediteurDTO.class)))
                .thenThrow(new ResourceNotFoundException("Client non trouvé avec l'ID: " + clientId));

        // When & Then
        mockMvc.perform(put("/api/v1/client-expediteurs/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientDTO)))
                .andExpect(status().isNotFound());

        verify(clientExpediteurService, times(1)).update(eq(clientId), any(ClientExpediteurDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/client-expediteurs/{id} - Email déjà utilisé par un autre client (400)")
    void testUpdateClientExpediteur_DuplicateEmail() throws Exception {
        // Given
        when(clientExpediteurService.update(eq(clientId), any(ClientExpediteurDTO.class)))
                .thenThrow(new InvalidDataException("Email déjà utilisé par un autre client"));

        // When & Then
        mockMvc.perform(put("/api/v1/client-expediteurs/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientDTO)))
                .andExpect(status().isBadRequest());

        verify(clientExpediteurService, times(1)).update(eq(clientId), any(ClientExpediteurDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/client-expediteurs/{id} - Supprimer un client avec succès (204)")
    void testDeleteClientExpediteur_Success() throws Exception {
        // Given
        doNothing().when(clientExpediteurService).delete(clientId);

        // When & Then
        mockMvc.perform(delete("/api/v1/client-expediteurs/{id}", clientId))
                .andExpect(status().isNoContent());

        verify(clientExpediteurService, times(1)).delete(clientId);
    }

    @Test
    @DisplayName("DELETE /api/v1/client-expediteurs/{id} - Client non trouvé (404)")
    void testDeleteClientExpediteur_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Client non trouvé avec l'ID: " + clientId))
                .when(clientExpediteurService).delete(clientId);

        // When & Then
        mockMvc.perform(delete("/api/v1/client-expediteurs/{id}", clientId))
                .andExpect(status().isNotFound());

        verify(clientExpediteurService, times(1)).delete(clientId);
    }
}

