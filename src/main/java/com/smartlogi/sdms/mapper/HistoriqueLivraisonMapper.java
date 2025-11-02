package com.smartlogi.sdms.mapper;
import com.smartlogi.sdms.dto.HistoriqueLivraisonDTO;
import com.smartlogi.sdms.entity.Colis;
import com.smartlogi.sdms.entity.HistoriqueLivraison;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface HistoriqueLivraisonMapper {

    @Mapping(source = "colis.id", target = "colisId")
    HistoriqueLivraisonDTO toDto(HistoriqueLivraison entity);

    @Mapping(source = "colisId", target = "colis", qualifiedByName = "colisFromId")
    HistoriqueLivraison toEntity(HistoriqueLivraisonDTO dto);

    @Named("colisFromId")
    default Colis colisFromId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("colisId ne peut pas être null pour un Historique");
        }
        Colis colis = new Colis();
        colis.setId(id);
        return colis;
    }
}
