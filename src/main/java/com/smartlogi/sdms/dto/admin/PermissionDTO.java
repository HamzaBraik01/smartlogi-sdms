package com.smartlogi.sdms.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class PermissionDTO {

    private String id;

    @NotBlank(message = "Le nom de la permission est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
    private String description;

    @Size(max = 50, message = "La ressource ne peut pas dépasser 50 caractères")
    private String ressource;

    @Size(max = 20, message = "L'action ne peut pas dépasser 20 caractères")
    private String action;

    private Boolean actif = true;

    public PermissionDTO() {
    }

    public PermissionDTO(String id, String nom, String description, String ressource, String action, Boolean actif) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.ressource = ressource;
        this.action = action;
        this.actif = actif;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRessource() {
        return ressource;
    }

    public void setRessource(String ressource) {
        this.ressource = ressource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }
}

