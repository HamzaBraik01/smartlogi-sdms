package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.DestinataireDTO;
import com.smartlogi.sdms.entity.Destinataire;
import com.smartlogi.sdms.exception.InvalidDataException;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.mapper.DestinataireMapper;
import com.smartlogi.sdms.repository.DestinataireRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
@DisplayName("Tests Unitaires pour DestinataireServiceImpl")
class DestinataireServiceImplTest {

    @Mock
    private DestinataireRepository destinataireRepository;

    @Mock
    private DestinataireMapper destinataireMapper;

    @InjectMocks
    private DestinataireServiceImpl destinataireService;

    private Destinataire destinataire;
    private DestinataireDTO destinataireDTO;
    private String destId;

    @BeforeEach
    void setUp() {
        destId = "dest-123";

        destinataire = new Destinataire();
        destinataire.setId(destId);
        destinataire.setEmail("test@destinataire.com");

        destinataireDTO = new DestinataireDTO();
        destinataireDTO.setId(destId);
        destinataireDTO.setEmail("test@destinataire.com");
    }

    @Test
    @DisplayName("doit retourner un DTO quand findById est appelé")
    void testFindById_CasNominal() {
        when(destinataireRepository.findById(destId)).thenReturn(Optional.of(destinataire));
        when(destinataireMapper.toDto(destinataire)).thenReturn(destinataireDTO);

        DestinataireDTO resultat = destinataireService.findById(destId);

        assertThat(resultat).isNotNull();
        verify(destinataireRepository).findById(destId);
    }

    @Test
    @DisplayName("doit jeter ResourceNotFoundException quand findById est inexistant")
    void testFindById_CasErreur_NonTrouve() {
        when(destinataireRepository.findById("bad-id")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            destinataireService.findById("bad-id");
        });
    }

    @Test
    @DisplayName("doit sauvegarder le destinataire si l'email est unique")
    void testSave_CasNominal() {
        when(destinataireRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(destinataireMapper.toEntity(destinataireDTO)).thenReturn(destinataire);
        when(destinataireRepository.save(destinataire)).thenReturn(destinataire);
        when(destinataireMapper.toDto(destinataire)).thenReturn(destinataireDTO);

        destinataireService.save(destinataireDTO);

        verify(destinataireRepository, times(1)).findByEmail(anyString());
        verify(destinataireRepository, times(1)).save(destinataire);
    }

    @Test
    @DisplayName("doit jeter InvalidDataException si l'email existe déjà (save)")
    void testSave_CasErreur_EmailExistant() {
        when(destinataireRepository.findByEmail(anyString())).thenReturn(Optional.of(new Destinataire()));

        assertThrows(InvalidDataException.class, () -> {
            destinataireService.save(destinataireDTO);
        });
        verify(destinataireRepository, never()).save(any(Destinataire.class));
    }

    @Test
    @DisplayName("doit mettre à jour le destinataire")
    void testUpdate_CasNominal() {
        when(destinataireRepository.findById(destId)).thenReturn(Optional.of(destinataire));
        when(destinataireRepository.save(any(Destinataire.class))).thenReturn(destinataire);
        when(destinataireMapper.toDto(destinataire)).thenReturn(destinataireDTO);

        destinataireService.update(destId, destinataireDTO);

        verify(destinataireRepository, times(1)).findById(destId);
        verify(destinataireRepository, times(1)).save(destinataire);
    }

    @Test
    @DisplayName("doit appeler deleteById quand delete est appelé")
    void testDelete_CasNominal() {
        when(destinataireRepository.existsById(destId)).thenReturn(true);
        doNothing().when(destinataireRepository).deleteById(destId);

        destinataireService.delete(destId);

        verify(destinataireRepository).existsById(destId);
        verify(destinataireRepository).deleteById(destId);
    }

    @Test
    @DisplayName("doit retourner une page de DTO quand findAll est appelé")
    void testFindAll_CasNominal() {
        Pageable pageable = PageRequest.of(0, 10);
        when(destinataireRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<DestinataireDTO> resultat = destinataireService.findAll(pageable);

        assertThat(resultat).isNotNull();
        verify(destinataireRepository).findAll(pageable);
    }
}