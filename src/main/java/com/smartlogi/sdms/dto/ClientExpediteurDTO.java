package com.smartlogi.sdms.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientExpediteurDTO {
    private String id;

    @NotNull(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @NotNull(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères")
    private String prenom;

    @NotNull(message = "L'email est obligatoire")
    @Email(message = "Le format de l'email est invalide")
    @Size(max = 100)
    private String email;

    @NotNull(message = "Le téléphone est obligatoire")
    @Size(min = 10, max = 20, message = "Le numéro de téléphone doit être valide")
    private String telephone;

    @NotNull(message = "L'adresse est obligatoire")
    @Size(min = 10, max = 255, message = "L'adresse doit contenir entre 10 et 255 caractères")
    private String adresse;
}
