package com.smartlogi.sdms.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProduitDTO {
    private String id;

    @NotNull(message = "Le nom du produit est obligatoire")
    @Size(min = 2, max = 255)
    private String nom;

    @NotNull(message = "Le poids est obligatoire")
    @Positive(message = "Le poids doit être un nombre positif")
    private Double poids;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix doit être positif")
    private BigDecimal prix;
}
