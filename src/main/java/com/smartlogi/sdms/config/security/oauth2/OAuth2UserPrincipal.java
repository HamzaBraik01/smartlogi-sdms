package com.smartlogi.sdms.config.security.oauth2;

import com.smartlogi.sdms.entity.Utilisateur;
import com.smartlogi.sdms.entity.enumeration.RoleUtilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Principal OAuth2 qui encapsule les informations utilisateur
 * après une authentification OAuth2 réussie.
 *
 * Implémente à la fois OAuth2User et UserDetails pour une
 * compatibilité maximale avec Spring Security.
 */
public class OAuth2UserPrincipal implements OAuth2User, UserDetails {

    private final Utilisateur utilisateur;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public OAuth2UserPrincipal(Utilisateur utilisateur, Map<String, Object> attributes) {
        this.utilisateur = utilisateur;
        this.attributes = attributes;
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority(mapRoleToAuthority(utilisateur.getRole()))
        );
    }

    /**
     * Mappe le rôle utilisateur vers l'autorité Spring Security.
     */
    private String mapRoleToAuthority(RoleUtilisateur role) {
        if (role == null) {
            return "ROLE_CLIENT"; // Rôle par défaut pour OAuth2
        }
        return switch (role) {
            case ADMIN -> "ROLE_ADMIN";
            case GESTIONNAIRE -> "ROLE_MANAGER";
            case LIVREUR -> "ROLE_DELIVERY";
            case CLIENT_EXPEDITEUR -> "ROLE_CLIENT";
            case DESTINATAIRE -> "ROLE_VIEWER";
        };
    }

    // ==================== OAuth2User ====================

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return utilisateur.getEmail();
    }

    // ==================== UserDetails ====================

    @Override
    public String getPassword() {
        return utilisateur.getPassword();
    }

    @Override
    public String getUsername() {
        return utilisateur.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return utilisateur.getEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return utilisateur.getEnabled();
    }

    // ==================== Accesseurs personnalisés ====================

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public String getId() {
        return utilisateur.getId();
    }

    public String getEmail() {
        return utilisateur.getEmail();
    }

    public String getNom() {
        return utilisateur.getNom();
    }

    public String getPrenom() {
        return utilisateur.getPrenom();
    }

    public RoleUtilisateur getRole() {
        return utilisateur.getRole();
    }
}

