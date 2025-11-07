package com.smartlogi.sdms.dto;

import com.smartlogi.sdms.entity.enumeration.StatutColis;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatutRequestDTO {

    @NotNull(message = "Le nouveau statut ne peut pas être null")
    private StatutColis statut;

    private String commentaire;
}