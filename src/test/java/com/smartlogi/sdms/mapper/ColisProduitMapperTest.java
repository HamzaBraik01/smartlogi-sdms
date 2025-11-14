package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.dto.ColisProduitDTO;
import com.smartlogi.sdms.entity.ColisProduit;
import com.smartlogi.sdms.entity.Produit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests Unitaires pour ColisProduitMapper")
class ColisProduitMapperTest {

    private ColisProduitMapper mapper = Mappers.getMapper(ColisProduitMapper.class);

    @Test
    @DisplayName("doit mapper produitId vers une entité Produit (toEntity)")
    void testToEntity() {
        ColisProduitDTO dto = new ColisProduitDTO();
        dto.setProduitId("prod-1");
        dto.setQuantite(5);

        ColisProduit entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getQuantite()).isEqualTo(5);
        assertThat(entity.getProduit()).isNotNull();
        assertThat(entity.getProduit().getId()).isEqualTo("prod-1");
        assertThat(entity.getId()).isNull();
        assertThat(entity.getColis()).isNull();
    }

    @Test
    @DisplayName("doit aplatir l'entité Produit vers produitId (toDto)")
    void testToDto() {
        ColisProduit entity = new ColisProduit();
        Produit produit = new Produit();
        produit.setId("prod-1");
        entity.setProduit(produit);
        entity.setQuantite(5);

        ColisProduitDTO dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getQuantite()).isEqualTo(5);
        assertThat(dto.getProduitId()).isEqualTo("prod-1");
    }
}