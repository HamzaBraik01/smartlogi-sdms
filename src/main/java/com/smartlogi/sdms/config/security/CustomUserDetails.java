package com.smartlogi.sdms.config.security;

import com.smartlogi.sdms.entity.Utilisateur;
import com.smartlogi.sdms.entity.enumeration.AuthProvider;
import com.smartlogi.sdms.entity.enumeration.RoleUtilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * UserDetails personnalisé pour l'authentification.
 * Supporte à la fois les utilisateurs LOCAL et OAuth2.
 */
public class CustomUserDetails implements UserDetails {

    private final String id;
    private final String email;
    private final String password;
    private final String nom;
    private final String prenom;
    private final RoleUtilisateur role;
    private final AuthProvider provider;
    private final Boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Utilisateur utilisateur) {
        this.id = utilisateur.getId();
        this.email = utilisateur.getEmail();
        this.password = utilisateur.getPassword(); // Peut être null pour OAuth2
        this.nom = utilisateur.getNom();
        this.prenom = utilisateur.getPrenom();
        this.role = utilisateur.getRole();
        this.provider = utilisateur.getProvider();
        this.enabled = utilisateur.getEnabled() != null ? utilisateur.getEnabled() : true;
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority(mapRoleToAuthority(utilisateur.getRole()))
        );
    }


    private String mapRoleToAuthority(RoleUtilisateur role) {
        if (role == null) {
            return "ROLE_CLIENT"; // Rôle par défaut
        }
        return switch (role) {
            case ADMIN -> "ROLE_ADMIN";
            case GESTIONNAIRE -> "ROLE_MANAGER";
            case LIVREUR -> "ROLE_DELIVERY";
            case CLIENT_EXPEDITEUR -> "ROLE_CLIENT";
            case DESTINATAIRE -> "ROLE_VIEWER";
        };
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public RoleUtilisateur getRole() {
        return role;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

