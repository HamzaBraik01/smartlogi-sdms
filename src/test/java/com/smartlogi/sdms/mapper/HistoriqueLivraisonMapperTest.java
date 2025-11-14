package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.dto.HistoriqueLivraisonDTO;
import com.smartlogi.sdms.entity.Colis;
import com.smartlogi.sdms.entity.HistoriqueLivraison;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Tests Unitaires pour HistoriqueLivraisonMapper")
class HistoriqueLivraisonMapperTest {

    private HistoriqueLivraisonMapper mapper = Mappers.getMapper(HistoriqueLivraisonMapper.class);

    @Test
    @DisplayName("doit mapper colisId vers une entité Colis (toEntity)")
    void testToEntity() {
        HistoriqueLivraisonDTO dto = new HistoriqueLivraisonDTO();
        dto.setColisId("colis-1");

        HistoriqueLivraison entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getColis()).isNotNull();
        assertThat(entity.getColis().getId()).isEqualTo("colis-1");
    }

    @Test
    @DisplayName("doit aplatir l'entité Colis vers colisId (toDto)")
    void testToDto() {
        HistoriqueLivraison entity = new HistoriqueLivraison();
        Colis colis = new Colis();
        colis.setId("colis-1");
        entity.setColis(colis);

        HistoriqueLivraisonDTO dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getColisId()).isEqualTo("colis-1");
    }

    @Test
    @DisplayName("doit jeter une exception si colisId est null (toEntity)")
    void testToEntity_NullColisId() {
        HistoriqueLivraisonDTO dto = new HistoriqueLivraisonDTO();
        dto.setColisId(null);

        assertThrows(IllegalArgumentException.class, () -> {
            mapper.toEntity(dto);
        });
    }
}