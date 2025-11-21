package com.smartlogi.security;

import com.smartlogi.sdms.entity.Utilisateur;
import com.smartlogi.sdms.repository.UtilisateurRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;


@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public AppUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur introuvable avec l'email: " + email));

        Collection<? extends GrantedAuthority> authorities = buildAuthorities(utilisateur);

        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getPassword())
                .authorities(authorities)
                .build();
    }


    private Collection<? extends GrantedAuthority> buildAuthorities(Utilisateur utilisateur) {
        String roleName = getRoleName(utilisateur);
        if (roleName == null || roleName.isEmpty()) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }


    private String getRoleName(Utilisateur utilisateur) {
        if (utilisateur.getRole() != null) {
            return utilisateur.getRole().name();
        }

        String className = utilisateur.getClass().getSimpleName();
        return switch (className) {
            case "GestionnaireLogistique" -> "GESTIONNAIRE";
            case "Livreur" -> "LIVREUR";
            case "ClientExpediteur" -> "CLIENT_EXPEDITEUR";
            case "Destinataire" -> "DESTINATAIRE";
            default -> null;
        };
    }
}

