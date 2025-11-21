package com.smartlogi.security;

import com.smartlogi.sdms.entity.ClientExpediteur;
import com.smartlogi.sdms.entity.GestionnaireLogistique;
import com.smartlogi.sdms.entity.Livreur;
import com.smartlogi.sdms.entity.Utilisateur;
import com.smartlogi.sdms.entity.enumeration.RoleUtilisateur;
import com.smartlogi.sdms.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private AppUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_should_return_userDetails_when_gestionnaire_exists() {
        GestionnaireLogistique gestionnaire = new GestionnaireLogistique();
        gestionnaire.setId("gest-123");
        gestionnaire.setEmail("admin@smartlogi.com");
        gestionnaire.setPassword("$2a$10$hashedPassword");
        gestionnaire.setNom("Dupont");
        gestionnaire.setPrenom("Jean");

        when(utilisateurRepository.findByEmail("admin@smartlogi.com"))
                .thenReturn(Optional.of(gestionnaire));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin@smartlogi.com");

        assertNotNull(userDetails);
        assertEquals("admin@smartlogi.com", userDetails.getUsername());
        assertEquals("$2a$10$hashedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_GESTIONNAIRE")));
        verify(utilisateurRepository, times(1)).findByEmail("admin@smartlogi.com");
    }

    @Test
    void loadUserByUsername_should_return_userDetails_when_livreur_exists() {
        Livreur livreur = new Livreur();
        livreur.setId("liv-456");
        livreur.setEmail("livreur@smartlogi.com");
        livreur.setPassword("$2a$10$anotherHashedPassword");
        livreur.setNom("Martin");
        livreur.setPrenom("Pierre");
        livreur.setVehicule("Camionnette");

        when(utilisateurRepository.findByEmail("livreur@smartlogi.com"))
                .thenReturn(Optional.of(livreur));

        UserDetails userDetails = userDetailsService.loadUserByUsername("livreur@smartlogi.com");

        assertNotNull(userDetails);
        assertEquals("livreur@smartlogi.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_LIVREUR")));
    }

    @Test
    void loadUserByUsername_should_return_userDetails_when_client_exists() {
        ClientExpediteur client = new ClientExpediteur();
        client.setId("cli-789");
        client.setEmail("client@example.com");
        client.setPassword("$2a$10$clientHashedPassword");
        client.setNom("Durand");
        client.setPrenom("Marie");
        client.setAdresse("123 Rue de Paris");

        when(utilisateurRepository.findByEmail("client@example.com"))
                .thenReturn(Optional.of(client));

        UserDetails userDetails = userDetailsService.loadUserByUsername("client@example.com");

        assertNotNull(userDetails);
        assertEquals("client@example.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENT_EXPEDITEUR")));
    }

    @Test
    void loadUserByUsername_should_throw_exception_when_user_not_found() {
        when(utilisateurRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown@example.com")
        );

        assertTrue(exception.getMessage().contains("Utilisateur introuvable avec l'email: unknown@example.com"));
        verify(utilisateurRepository, times(1)).findByEmail("unknown@example.com");
    }

    @Test
    void loadUserByUsername_should_handle_user_without_role() {
        GestionnaireLogistique userWithoutRole = new GestionnaireLogistique();
        userWithoutRole.setId("no-role-123");
        userWithoutRole.setEmail("norole@smartlogi.com");
        userWithoutRole.setPassword("$2a$10$password");

        when(utilisateurRepository.findByEmail("norole@smartlogi.com"))
                .thenReturn(Optional.of(userWithoutRole));


        UserDetails userDetails = userDetailsService.loadUserByUsername("norole@smartlogi.com");

        assertNotNull(userDetails);
        assertEquals("norole@smartlogi.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_GESTIONNAIRE")));
    }
}

