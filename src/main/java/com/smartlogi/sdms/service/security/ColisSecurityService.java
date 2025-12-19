package com.smartlogi.sdms.service.security;

import com.smartlogi.sdms.config.security.CustomUserDetails;
import com.smartlogi.sdms.entity.Colis;
import com.smartlogi.sdms.repository.ColisRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Service de sécurité pour les opérations sur les colis.
 * Fournit des méthodes de vérification d'accès utilisables dans les expressions @PreAuthorize.
 */
@Service("colisSecurityService")
public class ColisSecurityService {

    private final ColisRepository colisRepository;

    public ColisSecurityService(ColisRepository colisRepository) {
        this.colisRepository = colisRepository;
    }

    /**
     * Vérifie si l'utilisateur authentifié est le propriétaire (client expéditeur) du colis.
     *
     * @param colisId L'ID du colis
     * @param authentication L'authentification courante
     * @return true si l'utilisateur est le propriétaire du colis
     */
    public boolean isOwner(String colisId, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return colisRepository.findById(colisId)
                .map(colis -> {
                    if (colis.getClientExpediteur() == null) {
                        return false;
                    }
                    return colis.getClientExpediteur().getId().equals(userDetails.getId());
                })
                .orElse(false);
    }

    /**
     * Vérifie si l'utilisateur authentifié est le livreur assigné au colis.
     *
     * @param colisId L'ID du colis
     * @param authentication L'authentification courante
     * @return true si l'utilisateur est le livreur assigné
     */
    public boolean isAssignedDelivery(String colisId, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return colisRepository.findById(colisId)
                .map(colis -> {
                    if (colis.getLivreur() == null) {
                        return false;
                    }
                    return colis.getLivreur().getId().equals(userDetails.getId());
                })
                .orElse(false);
    }

    /**
     * Vérifie si l'utilisateur peut accéder au colis.
     * Un utilisateur peut accéder si :
     * - Il a le rôle MANAGER
     * - Il est le propriétaire (client expéditeur)
     * - Il est le livreur assigné
     * - Il est le destinataire
     *
     * @param colisId L'ID du colis
     * @param authentication L'authentification courante
     * @return true si l'utilisateur peut accéder au colis
     */
    public boolean canAccess(String colisId, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }

        // Les managers ont accès à tout
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            return true;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = userDetails.getId();

        return colisRepository.findById(colisId)
                .map(colis ->
                    isClientExpediteur(colis, userId) ||
                    isLivreur(colis, userId) ||
                    isDestinataire(colis, userId)
                )
                .orElse(false);
    }

    /**
     * Vérifie si l'utilisateur peut modifier le statut du colis.
     * Seuls les managers et les livreurs assignés peuvent modifier le statut.
     *
     * @param colisId L'ID du colis
     * @param authentication L'authentification courante
     * @return true si l'utilisateur peut modifier le statut
     */
    public boolean canUpdateStatus(String colisId, Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        // Les managers peuvent tout modifier
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            return true;
        }

        // Les livreurs ne peuvent modifier que leurs colis assignés
        return isAssignedDelivery(colisId, authentication);
    }

    private boolean isClientExpediteur(Colis colis, String userId) {
        return colis.getClientExpediteur() != null &&
               colis.getClientExpediteur().getId().equals(userId);
    }

    private boolean isLivreur(Colis colis, String userId) {
        return colis.getLivreur() != null &&
               colis.getLivreur().getId().equals(userId);
    }

    private boolean isDestinataire(Colis colis, String userId) {
        return colis.getDestinataire() != null &&
               colis.getDestinataire().getId().equals(userId);
    }

    /**
     * Vérifie si l'ID fourni correspond à l'utilisateur authentifié courant.
     *
     * @param userId L'ID de l'utilisateur à vérifier
     * @param authentication L'authentification courante
     * @return true si l'ID correspond à l'utilisateur authentifié
     */
    public boolean isCurrentUser(String userId, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userId != null && userId.equals(userDetails.getId());
    }
}

