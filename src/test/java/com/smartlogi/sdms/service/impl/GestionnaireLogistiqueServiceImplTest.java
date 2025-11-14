package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.GestionnaireLogistiqueDTO;
import com.smartlogi.sdms.dto.GlobalSearchResponseDTO;
import com.smartlogi.sdms.dto.StatistiquesTourneeDTO;
import com.smartlogi.sdms.entity.GestionnaireLogistique;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.mapper.GestionnaireLogistiqueMapper;
import com.smartlogi.sdms.repository.GestionnaireLogistiqueRepository;
import com.smartlogi.sdms.service.interfaces.ColisService;
import com.smartlogi.sdms.service.interfaces.GlobalSearchService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires pour GestionnaireLogistiqueServiceImpl (Façade)")
class GestionnaireLogistiqueServiceImplTest {

    @Mock
    private GlobalSearchService globalSearchService;

    @Mock
    private ColisService colisService;

    @Mock
    private GestionnaireLogistiqueRepository gestionnaireRepository;

    @Mock
    private GestionnaireLogistiqueMapper gestionnaireMapper;

    @InjectMocks
    private GestionnaireLogistiqueServiceImpl gestionnaireService;

    @Test
    @DisplayName("doit déléguer l'appel des statistiques au ColisService")
    void testGetStatistiquesTournees_DoitDeleguer() {

        StatistiquesTourneeDTO statsDTO = new StatistiquesTourneeDTO();
        when(colisService.getStatistiquesTournees()).thenReturn(statsDTO);

        StatistiquesTourneeDTO resultat = gestionnaireService.getStatistiquesTournees();


        assertThat(resultat).isEqualTo(statsDTO);


        verify(colisService, times(1)).getStatistiquesTournees();


        verifyNoInteractions(globalSearchService, gestionnaireRepository, gestionnaireMapper);
    }


    @Test
    @DisplayName("doit déléguer l'appel de recherche au GlobalSearchService")
    void testRechercher_DoitDeleguer() {
        String motCle = "test";
        Pageable pageable = PageRequest.of(0, 10);
        GlobalSearchResponseDTO responseDTO = new GlobalSearchResponseDTO();

        when(globalSearchService.rechercher(motCle, pageable)).thenReturn(responseDTO);

        GlobalSearchResponseDTO resultat = gestionnaireService.rechercher(motCle, pageable);


        assertThat(resultat).isEqualTo(responseDTO);

        verify(globalSearchService, times(1)).rechercher(motCle, pageable);

        verifyNoInteractions(colisService, gestionnaireRepository, gestionnaireMapper);
    }


    @Test
    @DisplayName("doit retourner un GestionnaireDTO (CRUD findById)")
    void testFindById_CasNominal() {
        // --- Arrange (Given) ---
        GestionnaireLogistique entity = new GestionnaireLogistique();
        entity.setId("gest-1");
        GestionnaireLogistiqueDTO dto = new GestionnaireLogistiqueDTO();
        dto.setId("gest-1");

        when(gestionnaireRepository.findById("gest-1")).thenReturn(Optional.of(entity));
        when(gestionnaireMapper.toDto(entity)).thenReturn(dto);

        GestionnaireLogistiqueDTO resultat = gestionnaireService.findById("gest-1");

        assertThat(resultat).isEqualTo(dto);
        verify(gestionnaireRepository, times(1)).findById("gest-1");
        verify(gestionnaireMapper, times(1)).toDto(entity);
        verifyNoInteractions(colisService, globalSearchService);
    }


    @Test
    @DisplayName("doit jeter ResourceNotFoundException si l'ID n'existe pas (CRUD findById)")
    void testFindById_CasErreur_NonTrouve() {
        when(gestionnaireRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            gestionnaireService.findById("bad-id");
        });

        verify(gestionnaireMapper, never()).toDto(any());
    }


    @Test
    @DisplayName("doit retourner une Page de GestionnaireDTO (CRUD findAll)")
    void testFindAll_CasNominal() {
        Pageable pageable = PageRequest.of(0, 5);
        when(gestionnaireRepository.findAll(pageable)).thenReturn(Page.empty());

        Page<GestionnaireLogistiqueDTO> resultat = gestionnaireService.findAll(pageable);

        assertThat(resultat).isNotNull();
        verify(gestionnaireRepository, times(1)).findAll(pageable);
    }



    @Test
    @DisplayName("doit sauvegarder un nouveau gestionnaire (CRUD save)")
    void testSave_CasNominal() {
        GestionnaireLogistique entity = new GestionnaireLogistique();
        entity.setId("gest-1");
        entity.setEmail("new@smartlogi.com");

        GestionnaireLogistiqueDTO dto = new GestionnaireLogistiqueDTO();
        dto.setEmail("new@smartlogi.com");

        when(gestionnaireMapper.toEntity(dto)).thenReturn(entity);
        when(gestionnaireRepository.save(entity)).thenReturn(entity);
        when(gestionnaireMapper.toDto(entity)).thenReturn(dto);

        GestionnaireLogistiqueDTO resultat = gestionnaireService.save(dto);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getEmail()).isEqualTo("new@smartlogi.com");

        verify(gestionnaireMapper, times(1)).toEntity(dto);
        verify(gestionnaireRepository, times(1)).save(entity);
        verify(gestionnaireMapper, times(1)).toDto(entity);
    }


    @Test
    @DisplayName("doit mettre à jour un gestionnaire existant (CRUD update)")
    void testUpdate_CasNominal() {
        GestionnaireLogistique existingEntity = new GestionnaireLogistique();
        existingEntity.setId("gest-1");
        existingEntity.setNom("AncienNom");
        existingEntity.setEmail("ancien@smartlogi.com");

        GestionnaireLogistiqueDTO dto = new GestionnaireLogistiqueDTO();
        dto.setId("gest-1");
        dto.setNom("NouveauNom");
        dto.setEmail("nouveau@smartlogi.com");
        dto.setTelephone("0612345678");

        when(gestionnaireRepository.findById("gest-1")).thenReturn(Optional.of(existingEntity));

        when(gestionnaireRepository.save(any(GestionnaireLogistique.class))).thenReturn(existingEntity);

        when(gestionnaireMapper.toDto(existingEntity)).thenReturn(dto);

        ArgumentCaptor<GestionnaireLogistique> entityCaptor = ArgumentCaptor.forClass(GestionnaireLogistique.class);

        GestionnaireLogistiqueDTO resultat = gestionnaireService.update("gest-1", dto);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getNom()).isEqualTo("NouveauNom");

        verify(gestionnaireRepository, times(1)).save(entityCaptor.capture());

        GestionnaireLogistique capturedEntity = entityCaptor.getValue();
        assertThat(capturedEntity.getNom()).isEqualTo("NouveauNom");
        assertThat(capturedEntity.getEmail()).isEqualTo("nouveau@smartlogi.com");
        assertThat(capturedEntity.getTelephone()).isEqualTo("0612345678");
    }


    @Test
    @DisplayName("doit jeter ResourceNotFoundException si l'entité à mettre à jour n'existe pas (CRUD update)")
    void testUpdate_CasErreur_NonTrouve() {
        GestionnaireLogistiqueDTO dto = new GestionnaireLogistiqueDTO();
        when(gestionnaireRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            gestionnaireService.update("bad-id", dto);
        });

        verify(gestionnaireRepository, never()).save(any());
    }



    @Test
    @DisplayName("doit supprimer un gestionnaire (CRUD delete)")
    void testDelete_CasNominal() {
        when(gestionnaireRepository.existsById("gest-1")).thenReturn(true);
        doNothing().when(gestionnaireRepository).deleteById("gest-1");

        gestionnaireService.delete("gest-1");


        verify(gestionnaireRepository, times(1)).existsById("gest-1");
        verify(gestionnaireRepository, times(1)).deleteById("gest-1");
    }



    @Test
    @DisplayName("doit jeter ResourceNotFoundException si l'entité à supprimer n'existe pas (CRUD delete)")
    void testDelete_CasErreur_NonTrouve() {
        when(gestionnaireRepository.existsById("bad-id")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            gestionnaireService.delete("bad-id");
        });

        verify(gestionnaireRepository, never()).deleteById(anyString());
    }
}