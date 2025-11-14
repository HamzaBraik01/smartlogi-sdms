package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.ZoneDTO;
import com.smartlogi.sdms.entity.Zone;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.mapper.ZoneMapper;
import com.smartlogi.sdms.repository.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires pour ZoneServiceImpl")
class ZoneServiceImplTest {

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private ZoneMapper zoneMapper;

    @InjectMocks
    private ZoneServiceImpl zoneService;

    private Zone zone;
    private ZoneDTO zoneDTO;
    private String zoneId;

    @BeforeEach
    void setUp() {
        zoneId = "zone-123";

        zone = new Zone();
        zone.setId(zoneId);
        zone.setNom("Zone Test");
        zone.setCodePostal("10000");
        zone.setVille("Rabat");

        zoneDTO = new ZoneDTO();
        zoneDTO.setId(zoneId);
        zoneDTO.setNom("Zone Test");
        zoneDTO.setCodePostal("10000");
        zoneDTO.setVille("Rabat");
    }


    @Test
    @DisplayName("doit retourner un DTO quand findById est appelé avec un ID existant")
    void testFindById_CasNominal() {
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(zone));
        when(zoneMapper.toDto(zone)).thenReturn(zoneDTO);

        ZoneDTO resultat = zoneService.findById(zoneId);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getNom()).isEqualTo("Zone Test");
        verify(zoneRepository, times(1)).findById(zoneId);
        verify(zoneMapper, times(1)).toDto(zone);
    }

    @Test
    @DisplayName("doit jeter ResourceNotFoundException quand findById est appelé avec un ID inexistant")
    void testFindById_CasErreur_NonTrouve() {
        when(zoneRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            zoneService.findById("bad-id");
        });

        verify(zoneMapper, never()).toDto(any(Zone.class));
    }


    @Test
    @DisplayName("doit sauvegarder et retourner un DTO quand save est appelé")
    void testSave_CasNominal() {
        when(zoneMapper.toEntity(zoneDTO)).thenReturn(zone);
        when(zoneRepository.save(zone)).thenReturn(zone);
        when(zoneMapper.toDto(zone)).thenReturn(zoneDTO);

        ZoneDTO resultat = zoneService.save(zoneDTO);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getNom()).isEqualTo("Zone Test");
        verify(zoneMapper, times(1)).toEntity(zoneDTO);
        verify(zoneRepository, times(1)).save(zone);
        verify(zoneMapper, times(1)).toDto(zone);
    }


    @Test
    @DisplayName("doit appeler deleteById quand delete est appelé avec un ID existant")
    void testDelete_CasNominal() {
        when(zoneRepository.existsById(zoneId)).thenReturn(true);
        doNothing().when(zoneRepository).deleteById(zoneId);

        zoneService.delete(zoneId);

        verify(zoneRepository, times(1)).existsById(zoneId);
        verify(zoneRepository, times(1)).deleteById(zoneId);
    }

    @Test
    @DisplayName("doit jeter ResourceNotFoundException quand delete est appelé avec un ID inexistant")
    void testDelete_CasErreur_NonTrouve() {
        when(zoneRepository.existsById("bad-id")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            zoneService.delete("bad-id");
        });

        verify(zoneRepository, never()).deleteById(anyString());
    }




    @Test
    @DisplayName("doit mettre à jour une zone existante quand update est appelé")
    void testUpdate_CasNominal() {
        ZoneDTO dtoModifie = new ZoneDTO();
        dtoModifie.setNom("Zone Modifiée");
        dtoModifie.setCodePostal("99999");
        dtoModifie.setVille("Tanger");

        Zone entiteExistante = new Zone();
        entiteExistante.setId(zoneId);
        entiteExistante.setNom("Zone Ancienne");

        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(entiteExistante));

        when(zoneRepository.save(any(Zone.class))).thenReturn(entiteExistante);

        when(zoneMapper.toDto(entiteExistante)).thenReturn(dtoModifie);

        ArgumentCaptor<Zone> zoneCaptor = ArgumentCaptor.forClass(Zone.class);

        ZoneDTO resultat = zoneService.update(zoneId, dtoModifie);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getNom()).isEqualTo("Zone Modifiée");

        verify(zoneRepository).save(zoneCaptor.capture());

        Zone zoneSauvegardee = zoneCaptor.getValue();
        assertThat(zoneSauvegardee.getNom()).isEqualTo("Zone Modifiée");
        assertThat(zoneSauvegardee.getCodePostal()).isEqualTo("99999");
        assertThat(zoneSauvegardee.getVille()).isEqualTo("Tanger");
    }

    @Test
    @DisplayName("doit jeter ResourceNotFoundException quand update est appelé sur un ID inexistant")
    void testUpdate_CasErreur_NonTrouve() {
        when(zoneRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            zoneService.update("bad-id", zoneDTO);
        });

        verify(zoneRepository, never()).save(any(Zone.class));
    }


    @Test
    @DisplayName("doit retourner une page de ZoneDTO quand findAll est appelé")
    void testFindAll_CasNominal() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Zone> pageEntites = Page.empty(pageable);

        when(zoneRepository.findAll(pageable)).thenReturn(pageEntites);

        Page<ZoneDTO> resultat = zoneService.findAll(pageable);

        assertThat(resultat).isNotNull();
        assertThat(resultat.isEmpty()).isTrue();
        verify(zoneRepository, times(1)).findAll(pageable);
    }
}