package com.smartlogi.sdms.mapper;
import com.smartlogi.sdms.dto.GestionnaireLogistiqueDTO;
import com.smartlogi.sdms.entity.GestionnaireLogistique;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GestionnaireLogistiqueMapper {

    GestionnaireLogistiqueDTO toDto(GestionnaireLogistique entity);

    GestionnaireLogistique toEntity(GestionnaireLogistiqueDTO dto);
}
