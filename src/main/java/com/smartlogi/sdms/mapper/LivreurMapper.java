package com.smartlogi.sdms.mapper;
import com.smartlogi.sdms.dto.LivreurDTO;
import com.smartlogi.sdms.entity.Livreur;
import com.smartlogi.sdms.entity.Zone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface LivreurMapper {

    @Mapping(source = "zone.id", target = "zoneId")
    @Mapping(source = "zone.nom", target = "zoneNom")
    LivreurDTO toDto(Livreur entity);

    @Mapping(source = "zoneId", target = "zone", qualifiedByName = "zoneFromId")
    Livreur toEntity(LivreurDTO dto);


    @Named("zoneFromId")
    default Zone zoneFromId(String id) {
        if (id == null) {
            return null;
        }
        Zone zone = new Zone();
        zone.setId(id);
        return zone;
    }
}
