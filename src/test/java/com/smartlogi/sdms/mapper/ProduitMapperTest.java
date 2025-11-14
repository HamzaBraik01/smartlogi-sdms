package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.dto.ProduitDTO;
import com.smartlogi.sdms.entity.Produit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests Unitaires pour ProduitMapper")
class ProduitMapperTest {

    private ProduitMapper mapper = Mappers.getMapper(ProduitMapper.class);

    @Test
    @DisplayName("doit mapper Entité vers DTO")
    void testToDto() {
        Produit entity = new Produit();
        entity.setId("prod-1");
        entity.setPrix(new BigDecimal("100.50"));

        ProduitDTO dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("prod-1");
        assertThat(dto.getPrix()).isEqualTo(new BigDecimal("100.50"));
    }

    @Test
    @DisplayName("doit mapper DTO vers Entité")
    void testToEntity() {
        ProduitDTO dto = new ProduitDTO();
        dto.setId("prod-1");
        dto.setPrix(new BigDecimal("100.50"));

        Produit entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo("prod-1");
        assertThat(entity.getPrix()).isEqualTo(new BigDecimal("100.50"));
    }
}