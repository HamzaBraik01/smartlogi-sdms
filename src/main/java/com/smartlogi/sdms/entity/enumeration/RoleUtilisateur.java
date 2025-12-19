package com.smartlogi.sdms.entity.enumeration;

/**
 * Enumération des rôles utilisateur dans le système SDMS.
 */
public enum RoleUtilisateur {
    /**
     * Administrateur système - Gestion des rôles et permissions
     */
    ADMIN,

    /**
     * Gestionnaire logistique - Accès complet aux opérations métier
     */
    GESTIONNAIRE,

    /**
     * Livreur - Accès à ses colis assignés et mise à jour des statuts
     */
    LIVREUR,

    /**
     * Client expéditeur - Création et suivi de ses propres colis
     */
    CLIENT_EXPEDITEUR,

    /**
     * Destinataire - Consultation du suivi de ses colis
     */
    DESTINATAIRE
}
