package com.smartlogi.sdms.dto;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HistoriqueLivraisonDTO {
    private String id;

    @NotNull(message = "Le statut est obligatoire")
    private StatutColis statut;

    private LocalDateTime dateChangement;

    @Size(max = 255, message = "Le commentaire ne doit pas dépasser 255 caractères")
    private String commentaire;

    @NotNull
    private String colisId;
}
