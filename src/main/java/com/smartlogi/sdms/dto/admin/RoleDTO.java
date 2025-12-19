package com.smartlogi.sdms.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class RoleDTO {

    private String id;

    @NotBlank(message = "Le nom du rôle est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;

    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
    private String description;

    private Boolean actif = true;

    private Set<PermissionDTO> permissions = new HashSet<>();

    public RoleDTO() {
    }

    public RoleDTO(String id, String nom, String description, Boolean actif) {
        this.id = id;
        this.nom = nom;
        this.description = description;
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

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Set<PermissionDTO> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionDTO> permissions) {
        this.permissions = permissions;
    }
}

