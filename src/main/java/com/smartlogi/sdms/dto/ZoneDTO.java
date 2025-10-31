package com.smartlogi.sdms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ZoneDTO {
    private String id;

    @NotNull(message = "Le nom de la zone est obligatoire")
    @Size(min = 3, max = 100)
    private String nom;

    @NotNull(message = "Le code postal est obligatoire")
    @Size(min = 5, max = 10, message = "Le code postal doit avoir 5 chiffres")
    private String codePostal;

    @NotNull(message = "La ville est obligatoire")
    @Size(min = 2, max = 100)
    private String ville;
}
