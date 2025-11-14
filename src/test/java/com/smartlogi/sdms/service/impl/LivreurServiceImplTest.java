package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.LivreurDTO;
import com.smartlogi.sdms.entity.Livreur;
import com.smartlogi.sdms.entity.Zone;
import com.smartlogi.sdms.exception.InvalidDataException;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.mapper.LivreurMapper;
import com.smartlogi.sdms.repository.LivreurRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires pour LivreurServiceImpl")
class LivreurServiceImplTest {

    @Mock
    private LivreurRepository livreurRepository;

    @Mock
    private LivreurMapper livreurMapper;

    @Mock
    private ZoneRepository zoneRepository; // Dépendance spéciale pour 'update'

    @InjectMocks
    private LivreurServiceImpl livreurService;

    private Livreur livreur;
    private LivreurDTO livreurDTO;
    private Zone zone;
    private String livreurId;
    private String zoneId;

    @BeforeEach
    void setUp() {
        livreurId = "livreur-123";
        zoneId = "zone-123";

        zone = new Zone();
        zone.setId(zoneId);

        livreur = new Livreur();
        livreur.setId(livreurId);
        livreur.setEmail("test@livreur.com");

        livreurDTO = new LivreurDTO();
        livreurDTO.setId(livreurId);
        livreurDTO.setEmail("test@livreur.com");
        livreurDTO.setZoneId(zoneId);
    }

    @Test
    @DisplayName("doit sauvegarder le livreur si l'email est unique")
    void testSave_CasNominal() {
        when(livreurRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(livreurMapper.toEntity(livreurDTO)).thenReturn(livreur);
        when(livreurRepository.save(livreur)).thenReturn(livreur);
        when(livreurMapper.toDto(livreur)).thenReturn(livreurDTO);

        livreurService.save(livreurDTO);

        verify(livreurRepository, times(1)).findByEmail(anyString());
        verify(livreurRepository, times(1)).save(livreur);
    }

    @Test
    @DisplayName("doit jeter InvalidDataException si l'email existe déjà (save)")
    void testSave_CasErreur_EmailExistant() {
        when(livreurRepository.findByEmail(anyString())).thenReturn(Optional.of(new Livreur()));

        assertThrows(InvalidDataException.class, () -> {
            livreurService.save(livreurDTO);
        });
        verify(livreurRepository, never()).save(any(Livreur.class));
    }

    @Test
    @DisplayName("doit mettre à jour le livreur et assigner une nouvelle zone")
    void testUpdate_CasNominal_NouvelleZone() {
        Livreur existingLivreur = new Livreur();
        existingLivreur.setId(livreurId);
        existingLivreur.setZone(null);

        when(livreurRepository.findById(livreurId)).thenReturn(Optional.of(existingLivreur));
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(zone));
        when(livreurRepository.save(any(Livreur.class))).thenReturn(existingLivreur);
        when(livreurMapper.toDto(existingLivreur)).thenReturn(livreurDTO);

        ArgumentCaptor<Livreur> livreurCaptor = ArgumentCaptor.forClass(Livreur.class);

        livreurService.update(livreurId, livreurDTO);

        verify(livreurRepository).save(livreurCaptor.capture());
        assertThat(livreurCaptor.getValue().getZone()).isEqualTo(zone);
    }

    @Test
    @DisplayName("doit jeter ResourceNotFoundException si la zone à assigner n'existe pas (update)")
    void testUpdate_CasErreur_ZoneNonTrouvee() {
        when(livreurRepository.findById(livreurId)).thenReturn(Optional.of(livreur));
        // La nouvelle zone n'existe pas
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            livreurService.update(livreurId, livreurDTO);
        });
        verify(livreurRepository, never()).save(any(Livreur.class));
    }
    @Test
    @DisplayName("doit retourner un LivreurDTO quand findById est appelé")
    void testFindById_CasNominal() {
        when(livreurRepository.findById(livreurId)).thenReturn(Optional.of(livreur));
        when(livreurMapper.toDto(livreur)).thenReturn(livreurDTO);

        LivreurDTO resultat = livreurService.findById(livreurId);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getEmail()).isEqualTo("test@livreur.com");
        verify(livreurRepository, times(1)).findById(livreurId);
        verify(livreurMapper, times(1)).toDto(livreur);
    }

    @Test
    @DisplayName("doit jeter ResourceNotFoundException quand findById est inexistant")
    void testFindById_CasErreur_NonTrouve() {
        when(livreurRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            livreurService.findById("bad-id");
        });
        verify(livreurMapper, never()).toDto(any());
    }


    @Test
    @DisplayName("doit retourner une page de LivreurDTO quand findAll est appelé")
    void testFindAll_CasNominal() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Livreur> pageEntites = Page.empty(pageable); // Une page vide suffit

        when(livreurRepository.findAll(pageable)).thenReturn(pageEntites);

        Page<LivreurDTO> resultat = livreurService.findAll(pageable);

        assertThat(resultat).isNotNull();
        assertThat(resultat.isEmpty()).isTrue();
        verify(livreurRepository, times(1)).findAll(pageable);
    }


    @Test
    @DisplayName("doit appeler deleteById quand delete est appelé avec un ID existant")
    void testDelete_CasNominal() {
        when(livreurRepository.existsById(livreurId)).thenReturn(true);
        doNothing().when(livreurRepository).deleteById(livreurId);


        livreurService.delete(livreurId);

        verify(livreurRepository, times(1)).existsById(livreurId);
        verify(livreurRepository, times(1)).deleteById(livreurId);
    }

    @Test
    @DisplayName("doit jeter ResourceNotFoundException quand delete est appelé sur un ID inexistant")
    void testDelete_CasErreur_NonTrouve() {
        when(livreurRepository.existsById("bad-id")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            livreurService.delete("bad-id");
        });

        verify(livreurRepository, never()).deleteById(anyString());
    }
}