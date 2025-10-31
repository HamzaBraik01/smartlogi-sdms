package com.smartlogi.sdms.dto;
import com.smartlogi.sdms.entity.enumeration.Priorite;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ColisDTO {
    private String id;

    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    private String description;

    @Positive(message = "Le poids total doit être positif")
    private Double poidsTotal;

    @NotNull(message = "Le statut est obligatoire")
    private StatutColis statut;

    @NotNull(message = "La priorité est obligatoire")
    private Priorite priorite;

    @NotNull(message = "La ville de destination est obligatoire")
    @Size(min = 2, max = 100)
    private String villeDestination;

    // Dates (pour affichage, non modifiables par l'utilisateur)
    private LocalDateTime dateCreation;
    private LocalDateTime dateDernierStatut;

    // --- Clés étrangères (pour la création et la liaison) ---

    @NotNull(message = "L'expéditeur (client) est obligatoire")
    private String clientExpediteurId;

    @NotNull(message = "Le destinataire est obligatoire")
    private String destinataireId;

    // Le livreur et la zone peuvent être null au début
    private String livreurId;
    private String zoneId;


}
