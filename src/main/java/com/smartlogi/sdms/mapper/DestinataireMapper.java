package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.dto.DestinataireDTO;
import com.smartlogi.sdms.entity.Destinataire;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DestinataireMapper {

    DestinataireDTO toDto(Destinataire entity);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    Destinataire toEntity(DestinataireDTO dto);
}