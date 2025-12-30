package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.dto.ClientExpediteurDTO;
import com.smartlogi.sdms.entity.ClientExpediteur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientExpediteurMapper {

    ClientExpediteurDTO toDto(ClientExpediteur entity);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    ClientExpediteur toEntity(ClientExpediteurDTO dto);
}
