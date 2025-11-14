package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.HistoriqueLivraisonDTO;
import com.smartlogi.sdms.entity.HistoriqueLivraison;
import com.smartlogi.sdms.mapper.HistoriqueLivraisonMapper;
import com.smartlogi.sdms.repository.HistoriqueLivraisonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires pour HistoriqueLivraisonServiceImpl")
class HistoriqueLivraisonServiceImplTest {

    @Mock
    private HistoriqueLivraisonRepository historiqueRepository;

    @Mock
    private HistoriqueLivraisonMapper historiqueMapper;

    @InjectMocks
    private HistoriqueLivraisonServiceImpl historiqueService;

    @Test
    @DisplayName("doit retourner une liste d'historique DTO pour un colisId")
    void testFindHistoriqueByColisId() {
        String colisId = "colis-1";
        List<HistoriqueLivraison> entites = List.of(new HistoriqueLivraison(), new HistoriqueLivraison());
        when(historiqueRepository.findAllByColisIdOrderByDateChangementDesc(colisId))
                .thenReturn(entites);

        when(historiqueMapper.toDto(any(HistoriqueLivraison.class)))
                .thenReturn(new HistoriqueLivraisonDTO());

        List<HistoriqueLivraisonDTO> resultat = historiqueService.findHistoriqueByColisId(colisId);

        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(2);
        verify(historiqueRepository).findAllByColisIdOrderByDateChangementDesc(colisId);
        verify(historiqueMapper, times(2)).toDto(any(HistoriqueLivraison.class));
    }
}