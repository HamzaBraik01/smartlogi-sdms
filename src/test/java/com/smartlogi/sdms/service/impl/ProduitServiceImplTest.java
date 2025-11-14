package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.ProduitDTO;
import com.smartlogi.sdms.entity.Produit;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.mapper.ProduitMapper;
import com.smartlogi.sdms.repository.ProduitRepository;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires pour ProduitServiceImpl")
class ProduitServiceImplTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private ProduitMapper produitMapper;

    @InjectMocks
    private ProduitServiceImpl produitService;

    private Produit produit;
    private ProduitDTO produitDTO;
    private String produitId;

    @BeforeEach
    void setUp() {
        produitId = "prod-123";

        produit = new Produit();
        produit.setId(produitId);
        produit.setNom("PC Portable");
        produit.setPrix(new BigDecimal("15000"));

        produitDTO = new ProduitDTO();
        produitDTO.setId(produitId);
        produitDTO.setNom("PC Portable");
        produitDTO.setPrix(new BigDecimal("15000"));
    }

    @Test
    @DisplayName("doit retourner un DTO quand findById est appelé")
    void testFindById_CasNominal() {
        when(produitRepository.findById(produitId)).thenReturn(Optional.of(produit));
        when(produitMapper.toDto(produit)).thenReturn(produitDTO);

        ProduitDTO resultat = produitService.findById(produitId);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getNom()).isEqualTo("PC Portable");
        verify(produitRepository).findById(produitId);
    }

    @Test
    @DisplayName("doit jeter ResourceNotFoundException quand findById est inexistant")
    void testFindById_CasErreur_NonTrouve() {
        when(produitRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            produitService.findById("bad-id");
        });
    }

    @Test
    @DisplayName("doit sauvegarder et retourner un DTO quand save est appelé")
    void testSave_CasNominal() {
        when(produitMapper.toEntity(produitDTO)).thenReturn(produit);
        when(produitRepository.save(produit)).thenReturn(produit);
        when(produitMapper.toDto(produit)).thenReturn(produitDTO);

        ProduitDTO resultat = produitService.save(produitDTO);

        assertThat(resultat).isNotNull();
        verify(produitMapper).toEntity(produitDTO);
        verify(produitRepository).save(produit);
    }

    @Test
    @DisplayName("doit mettre à jour un produit existant")
    void testUpdate_CasNominal() {
        when(produitRepository.findById(produitId)).thenReturn(Optional.of(produit));
        when(produitRepository.save(any(Produit.class))).thenReturn(produit);
        when(produitMapper.toDto(produit)).thenReturn(produitDTO);

        ProduitDTO resultat = produitService.update(produitId, produitDTO);

        assertThat(resultat).isNotNull();
        verify(produitRepository).findById(produitId);
        verify(produitRepository).save(produit);
    }

    @Test
    @DisplayName("doit jeter ResourceNotFoundException quand update est appelé sur un ID inexistant")
    void testUpdate_CasErreur_NonTrouve() {
        when(produitRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            produitService.update("bad-id", produitDTO);
        });
        verify(produitRepository, never()).save(any(Produit.class));
    }

    @Test
    @DisplayName("doit appeler deleteById quand delete est appelé avec un ID existant")
    void testDelete_CasNominal() {
        when(produitRepository.existsById(produitId)).thenReturn(true);
        doNothing().when(produitRepository).deleteById(produitId);

        produitService.delete(produitId);

        verify(produitRepository).existsById(produitId);
        verify(produitRepository).deleteById(produitId);
    }

    @Test
    @DisplayName("doit jeter ResourceNotFoundException quand delete est appelé sur un ID inexistant")
    void testDelete_CasErreur_NonTrouve() {
        when(produitRepository.existsById("bad-id")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            produitService.delete("bad-id");
        });
        verify(produitRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("doit retourner une page de ProduitDTO quand findAll est appelé")
    void testFindAll_CasNominal() {
        Pageable pageable = PageRequest.of(0, 10);
        when(produitRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<ProduitDTO> resultat = produitService.findAll(pageable);

        assertThat(resultat).isNotNull();
        verify(produitRepository).findAll(pageable);
    }
}