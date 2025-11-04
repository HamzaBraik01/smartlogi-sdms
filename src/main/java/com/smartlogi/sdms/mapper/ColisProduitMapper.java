package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.dto.ColisProduitDTO;
import com.smartlogi.sdms.entity.ColisProduit;
import com.smartlogi.sdms.entity.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ColisProduitMapper {

    @Mapping(source = "produit.id", target = "produitId")
    ColisProduitDTO toDto(ColisProduit entity);

    @Mapping(source = "produitId", target = "produit", qualifiedByName = "produitFromId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "colis", ignore = true)
    ColisProduit toEntity(ColisProduitDTO dto);

    @Named("produitFromId")
    default Produit produitFromId(String id) {
        if (id == null) return null;
        Produit produit = new Produit();
        produit.setId(id);
        return produit;
    }
}