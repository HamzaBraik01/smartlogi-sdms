package com.smartlogi.sdms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Entity
@Table(name = "permission")
@Getter
@Setter
@NoArgsConstructor
public class Permission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    @Column(name = "nom", unique = true, nullable = false)
    private String nom;

    @Column(name = "description")
    private String description;


    @Column(name = "ressource")
    private String ressource;


    @Column(name = "action")
    private String action;

    @Column(name = "actif")
    private Boolean actif = true;

    public Permission(String nom, String description, String ressource, String action) {
        this.nom = nom;
        this.description = description;
        this.ressource = ressource;
        this.action = action;
        this.actif = true;
    }


    public static Permission manage(String ressource) {
        return new Permission(
                ressource + "_MANAGE",
                "Gestion complète de " + ressource.toLowerCase(),
                ressource,
                "MANAGE"
        );
    }


    public static Permission read(String ressource) {
        return new Permission(
                ressource + "_READ",
                "Lecture de " + ressource.toLowerCase(),
                ressource,
                "READ"
        );
    }


    public static Permission create(String ressource) {
        return new Permission(
                ressource + "_CREATE",
                "Création de " + ressource.toLowerCase(),
                ressource,
                "CREATE"
        );
    }


    public static Permission update(String ressource) {
        return new Permission(
                ressource + "_UPDATE",
                "Modification de " + ressource.toLowerCase(),
                ressource,
                "UPDATE"
        );
    }


    public static Permission delete(String ressource) {
        return new Permission(
                ressource + "_DELETE",
                "Suppression de " + ressource.toLowerCase(),
                ressource,
                "DELETE"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission permission)) return false;
        return id != null && id.equals(permission.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

