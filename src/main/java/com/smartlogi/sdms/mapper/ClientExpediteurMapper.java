package com.smartlogi.sdms.mapper;
import com.smartlogi.sdms.dto.ClientExpediteurDTO;
import com.smartlogi.sdms.entity.ClientExpediteur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientExpediteurMapper {

    ClientExpediteurDTO toDto(ClientExpediteur entity);

    ClientExpediteur toEntity(ClientExpediteurDTO dto);
}
