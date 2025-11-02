package com.smartlogi.sdms.mapper;
import com.smartlogi.sdms.dto.ZoneDTO;
import com.smartlogi.sdms.entity.Zone;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ZoneMapper {

    ZoneDTO toDto(Zone entity);

    Zone toEntity(ZoneDTO dto);
}
