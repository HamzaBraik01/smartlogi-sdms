package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.dto.ZoneDTO;
import com.smartlogi.sdms.entity.Zone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests Unitaires pour ZoneMapper")
class ZoneMapperTest {

    private ZoneMapper mapper = Mappers.getMapper(ZoneMapper.class);

    @Test
    @DisplayName("doit mapper Entité vers DTO")
    void testToDto() {
        Zone entity = new Zone();
        entity.setId("zone-1");
        entity.setNom("Test Zone");

        ZoneDTO dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("zone-1");
        assertThat(dto.getNom()).isEqualTo("Test Zone");
    }

    @Test
    @DisplayName("doit mapper DTO vers Entité")
    void testToEntity() {
        ZoneDTO dto = new ZoneDTO();
        dto.setId("zone-1");
        dto.setNom("Test Zone");

        Zone entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo("zone-1");
        assertThat(entity.getNom()).isEqualTo("Test Zone");
    }
}