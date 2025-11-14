package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.GlobalSearchResponseDTO;
import com.smartlogi.sdms.mapper.ClientExpediteurMapper;
import com.smartlogi.sdms.mapper.ColisMapper;
import com.smartlogi.sdms.mapper.LivreurMapper;
import com.smartlogi.sdms.repository.ClientExpediteurRepository;
import com.smartlogi.sdms.repository.ColisRepository;
import com.smartlogi.sdms.repository.LivreurRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires pour GlobalSearchServiceImpl")
class GlobalSearchServiceImplTest {

    @Mock private ColisRepository colisRepository;
    @Mock private ClientExpediteurRepository clientExpediteurRepository;
    @Mock private LivreurRepository livreurRepository;

    @Mock private ColisMapper colisMapper;
    @Mock private ClientExpediteurMapper clientExpediteurMapper;
    @Mock private LivreurMapper livreurMapper;

    @InjectMocks
    private GlobalSearchServiceImpl searchService;

    @Test
    @DisplayName("doit appeler les 3 repositories de recherche et mapper les résultats")
    void testRechercher_CasNominal() {
        String motCle = "test";
        Pageable pageable = Pageable.unpaged();

        when(colisRepository.searchByKeyword(anyString(), any(Pageable.class)))
                .thenReturn(Page.empty());
        when(clientExpediteurRepository.searchByKeyword(anyString(), any(Pageable.class)))
                .thenReturn(Page.empty());
        when(livreurRepository.searchByKeyword(anyString(), any(Pageable.class)))
                .thenReturn(Page.empty());

        GlobalSearchResponseDTO resultat = searchService.rechercher(motCle, pageable);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getColis()).isNotNull();
        assertThat(resultat.getClients()).isNotNull();
        assertThat(resultat.getLivreurs()).isNotNull();

        verify(colisRepository, times(1)).searchByKeyword(motCle, pageable);
        verify(clientExpediteurRepository, times(1)).searchByKeyword(motCle, pageable);
        verify(livreurRepository, times(1)).searchByKeyword(motCle, pageable);
    }
}