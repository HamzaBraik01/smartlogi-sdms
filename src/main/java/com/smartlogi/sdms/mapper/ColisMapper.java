package com.smartlogi.sdms.mapper;
import com.smartlogi.sdms.dto.ColisDTO;
import com.smartlogi.sdms.entity.ClientExpediteur;
import com.smartlogi.sdms.entity.Colis;
import com.smartlogi.sdms.entity.Destinataire;
import com.smartlogi.sdms.entity.Livreur;
import com.smartlogi.sdms.entity.Zone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ColisMapper {

    // --- Entité vers DTO ---
    @Mapping(source = "clientExpediteur.id", target = "clientExpediteurId")
    @Mapping(source = "destinataire.id", target = "destinataireId")
    @Mapping(source = "livreur.id", target = "livreurId")
    @Mapping(source = "zone.id", target = "zoneId")
    ColisDTO toDto(Colis entity);

    // --- DTO vers Entité ---
    @Mapping(source = "clientExpediteurId", target = "clientExpediteur", qualifiedByName = "clientFromId")
    @Mapping(source = "destinataireId", target = "destinataire", qualifiedByName = "destinataireFromId")
    @Mapping(source = "livreurId", target = "livreur", qualifiedByName = "livreurFromId")
    @Mapping(source = "zoneId", target = "zone", qualifiedByName = "zoneFromId")
    Colis toEntity(ColisDTO dto);

    @Named("clientFromId")
    default ClientExpediteur clientFromId(String id) {
        if (id == null) return null;
        ClientExpediteur client = new ClientExpediteur();
        client.setId(id);
        return client;
    }

    @Named("destinataireFromId")
    default Destinataire destinataireFromId(String id) {
        if (id == null) return null;
        Destinataire dest = new Destinataire();
        dest.setId(id);
        return dest;
    }

    @Named("livreurFromId")
    default Livreur livreurFromId(String id) {
        if (id == null) return null;
        Livreur livreur = new Livreur();
        livreur.setId(id);
        return livreur;
    }

    @Named("zoneFromId")
    default Zone zoneFromId(String id) {
        if (id == null) return null;
        Zone zone = new Zone();
        zone.setId(id);
        return zone;
    }
}
