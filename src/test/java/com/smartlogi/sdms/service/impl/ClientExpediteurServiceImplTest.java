package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.ClientExpediteurDTO;
import com.smartlogi.sdms.entity.ClientExpediteur;
import com.smartlogi.sdms.exception.InvalidDataException;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.mapper.ClientExpediteurMapper;
import com.smartlogi.sdms.repository.ClientExpediteurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires pour ClientExpediteurServiceImpl")
class ClientExpediteurServiceImplTest {

    @Mock
    private ClientExpediteurRepository clientRepository;

    @Mock
    private ClientExpediteurMapper clientMapper;

    @InjectMocks
    private ClientExpediteurServiceImpl clientService;

    private ClientExpediteur client;
    private ClientExpediteurDTO clientDTO;
    private String clientId;

    @BeforeEach
    void setUp() {
        clientId = "client-123";

        client = new ClientExpediteur();
        client.setId(clientId);
        client.setEmail("test@client.com");

        clientDTO = new ClientExpediteurDTO();
        clientDTO.setId(clientId);
        clientDTO.setEmail("test@client.com");
    }


    @Test
    @DisplayName("doit retourner un DTO quand findById est appelé avec un ID existant")
    void testFindById_CasNominal() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientMapper.toDto(client)).thenReturn(clientDTO);

        ClientExpediteurDTO resultat = clientService.findById(clientId);

        assertThat(resultat).isNotNull();
        verify(clientRepository).findById(clientId);
    }

    @Test
    @DisplayName("doit jeter ResourceNotFoundException quand findById est appelé avec un ID inexistant")
    void testFindById_CasErreur_NonTrouve() {
        when(clientRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            clientService.findById("bad-id");
        });
    }

    // --- Test pour save ---

    @Test
    @DisplayName("doit sauvegarder le client si l'email est unique")
    void testSave_CasNominal() {
        // Simuler que l'email n'existe pas
        when(clientRepository.findByEmail("test@client.com")).thenReturn(Optional.empty());
        // Simuler les mappings et la sauvegarde
        when(clientMapper.toEntity(clientDTO)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toDto(client)).thenReturn(clientDTO);

        ClientExpediteurDTO resultat = clientService.save(clientDTO);

        assertThat(resultat).isNotNull();
        verify(clientRepository, times(1)).findByEmail("test@client.com");
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    @DisplayName("doit jeter InvalidDataException si l'email existe déjà (save)")
    void testSave_CasErreur_EmailExistant() {
        when(clientRepository.findByEmail("test@client.com")).thenReturn(Optional.of(new ClientExpediteur()));

        assertThrows(InvalidDataException.class, () -> {
            clientService.save(clientDTO);
        });

        verify(clientRepository, never()).save(any(ClientExpediteur.class));
    }


    @Test
    @DisplayName("doit appeler deleteById quand delete est appelé avec un ID existant")
    void testDelete_CasNominal() {
        when(clientRepository.existsById(clientId)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(clientId);

        clientService.delete(clientId);

        verify(clientRepository, times(1)).existsById(clientId);
        verify(clientRepository, times(1)).deleteById(clientId);
    }

    @Test
    @DisplayName("doit jeter ResourceNotFoundException quand delete est appelé avec un ID inexistant")
    void testDelete_CasErreur_NonTrouve() {
        when(clientRepository.existsById("bad-id")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            clientService.delete("bad-id");
        });
    }



    @Test
    @DisplayName("doit mettre à jour le client si l'email n'est pas changé")
    void testUpdate_CasNominal_EmailInchange() {
        ClientExpediteurDTO dtoModifie = new ClientExpediteurDTO();
        dtoModifie.setNom("Client Modifié");
        dtoModifie.setEmail(clientDTO.getEmail()); // Email identique

        client.setNom("Client Ancien");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(ClientExpediteur.class))).thenReturn(client);
        when(clientMapper.toDto(client)).thenReturn(dtoModifie);

        ArgumentCaptor<ClientExpediteur> clientCaptor = ArgumentCaptor.forClass(ClientExpediteur.class);

        ClientExpediteurDTO resultat = clientService.update(clientId, dtoModifie);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getNom()).isEqualTo("Client Modifié");

        verify(clientRepository, never()).findByEmail(anyString());

        verify(clientRepository).save(clientCaptor.capture());
        assertThat(clientCaptor.getValue().getNom()).isEqualTo("Client Modifié");
    }

    @Test
    @DisplayName("doit mettre à jour le client si le nouvel email est unique")
    void testUpdate_CasNominal_EmailChangeEtUnique() {
        String nouvelEmail = "nouveau@client.com";
        ClientExpediteurDTO dtoModifie = new ClientExpediteurDTO();
        dtoModifie.setEmail(nouvelEmail); // L'email change

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.findByEmail(nouvelEmail)).thenReturn(Optional.empty());
        when(clientRepository.save(any(ClientExpediteur.class))).thenReturn(client);
        when(clientMapper.toDto(client)).thenReturn(dtoModifie);

        clientService.update(clientId, dtoModifie);

        verify(clientRepository, times(1)).findByEmail(nouvelEmail);
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    @DisplayName("doit jeter InvalidDataException si le nouvel email est déjà pris (update)")
    void testUpdate_CasErreur_EmailExistant() {
        String emailDejaPris = "nouveau@client.com";
        ClientExpediteurDTO dtoModifie = new ClientExpediteurDTO();
        dtoModifie.setEmail(emailDejaPris); // L'email change

        ClientExpediteur autreClient = new ClientExpediteur();
        autreClient.setId("client-456");
        autreClient.setEmail(emailDejaPris);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.findByEmail(emailDejaPris)).thenReturn(Optional.of(autreClient));

        assertThrows(InvalidDataException.class, () -> {
            clientService.update(clientId, dtoModifie);
        });

        verify(clientRepository, never()).save(any(ClientExpediteur.class));
    }


    @Test
    @DisplayName("doit retourner une page de ClientExpediteurDTO quand findAll est appelé")
    void testFindAll_CasNominal() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClientExpediteur> pageEntites = Page.empty(pageable);

        when(clientRepository.findAll(pageable)).thenReturn(pageEntites);

        Page<ClientExpediteurDTO> resultat = clientService.findAll(pageable);

        assertThat(resultat).isNotNull();
        assertThat(resultat.isEmpty()).isTrue();
        verify(clientRepository, times(1)).findAll(pageable);
    }
}