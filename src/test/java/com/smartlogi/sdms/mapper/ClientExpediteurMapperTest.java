package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.dto.ClientExpediteurDTO;
import com.smartlogi.sdms.entity.ClientExpediteur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests Unitaires pour ClientExpediteurMapper")
class ClientExpediteurMapperTest {

    private ClientExpediteurMapper mapper = Mappers.getMapper(ClientExpediteurMapper.class);

    @Test
    @DisplayName("doit mapper Entité vers DTO")
    void testToDto() {
        ClientExpediteur entity = new ClientExpediteur();
        entity.setId("client-1");
        entity.setEmail("test@client.com");

        ClientExpediteurDTO dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("client-1");
        assertThat(dto.getEmail()).isEqualTo("test@client.com");
    }

    @Test
    @DisplayName("doit mapper DTO vers Entité")
    void testToEntity() {
        ClientExpediteurDTO dto = new ClientExpediteurDTO();
        dto.setId("client-1");
        dto.setEmail("test@client.com");

        ClientExpediteur entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo("client-1");
        assertThat(entity.getEmail()).isEqualTo("test@client.com");
    }
}