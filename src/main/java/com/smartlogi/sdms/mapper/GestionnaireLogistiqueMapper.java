package com.smartlogi.sdms.mapper;
import com.smartlogi.sdms.dto.GestionnaireLogistiqueDTO;
import com.smartlogi.sdms.entity.GestionnaireLogistique;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GestionnaireLogistiqueMapper {

    GestionnaireLogistiqueDTO toDto(GestionnaireLogistique entity);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    GestionnaireLogistique toEntity(GestionnaireLogistiqueDTO dto);
}
