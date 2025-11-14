package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.dto.LivreurDTO;
import com.smartlogi.sdms.entity.Livreur;
import com.smartlogi.sdms.entity.Zone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests Unitaires pour LivreurMapper")
class LivreurMapperTest {

    private LivreurMapper mapper = Mappers.getMapper(LivreurMapper.class);

    @Test
    @DisplayName("doit mapper zoneId vers une entité Zone (toEntity)")
    void testToEntity() {
        LivreurDTO dto = new LivreurDTO();
        dto.setZoneId("zone-casa");

        Livreur entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getZone()).isNotNull();
        assertThat(entity.getZone().getId()).isEqualTo("zone-casa");
    }

    @Test
    @DisplayName("doit aplatir l'entité Zone vers zoneId et zoneNom (toDto)")
    void testToDto() {
        Livreur entity = new Livreur();
        Zone zone = new Zone();
        zone.setId("zone-casa");
        zone.setNom("Zone Casablanca Anfa");
        entity.setZone(zone);

        LivreurDTO dto = mapper.toDto(entity);


        assertThat(dto).isNotNull();
        assertThat(dto.getZoneId()).isEqualTo("zone-casa");
        assertThat(dto.getZoneNom()).isEqualTo("Zone Casablanca Anfa");
    }

    @Test
    @DisplayName("doit gérer une Zone nulle (toDto)")
    void testToDto_NullZone() {
        Livreur entity = new Livreur();
        entity.setZone(null);

        LivreurDTO dto = mapper.toDto(entity);


        assertThat(dto).isNotNull();
        assertThat(dto.getZoneId()).isNull();
        assertThat(dto.getZoneNom()).isNull();
    }
}