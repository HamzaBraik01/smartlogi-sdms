package com.smartlogi.sdms.mapper;
import com.smartlogi.sdms.dto.ProduitDTO;
import com.smartlogi.sdms.entity.Produit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProduitMapper {

    ProduitDTO toDto(Produit entity);

    Produit toEntity(ProduitDTO dto);
}
