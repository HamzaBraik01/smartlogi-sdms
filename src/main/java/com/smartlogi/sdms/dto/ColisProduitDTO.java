package com.smartlogi.sdms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ColisProduitDTO {

    @NotNull(message = "L'ID du produit est obligatoire")
    private String produitId;

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être au moins de 1")
    private Integer quantite;

}