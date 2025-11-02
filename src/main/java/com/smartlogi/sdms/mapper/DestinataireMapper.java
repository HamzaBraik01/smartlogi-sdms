package com.smartlogi.sdms.mapper;
import com.smartlogi.sdms.dto.DestinataireDTO;
import com.smartlogi.sdms.entity.Destinataire;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DestinataireMapper {

    DestinataireDTO toDto(Destinataire entity);

    Destinataire toEntity(DestinataireDTO dto);
}