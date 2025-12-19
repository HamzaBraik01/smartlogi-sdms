package com.smartlogi.sdms.config.security;

import com.smartlogi.sdms.entity.Utilisateur;
import com.smartlogi.sdms.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec l'email: " + email));

        return new CustomUserDetails(utilisateur);
    }


    @Transactional(readOnly = true)
    public UserDetails loadUserById(String id) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec l'ID: " + id));

        return new CustomUserDetails(utilisateur);
    }
}

