package com.smartlogi.sdms.entity;

import com.smartlogi.sdms.entity.enumeration.RoleUtilisateur;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entité Administrateur du système.
 * Gère les rôles et permissions.
 */
@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@NoArgsConstructor
public class Admin extends Utilisateur {

    @Override
    public RoleUtilisateur getRole() {
        return RoleUtilisateur.ADMIN;
    }
}

