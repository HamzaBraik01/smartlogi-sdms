package com.smartlogi.sdms.config.security.oauth2;

import com.smartlogi.sdms.dto.auth.OAuth2UserInfo;
import com.smartlogi.sdms.dto.auth.OAuth2UserInfoFactory;
import com.smartlogi.sdms.entity.ClientExpediteur;
import com.smartlogi.sdms.entity.Utilisateur;
import com.smartlogi.sdms.entity.enumeration.AuthProvider;
import com.smartlogi.sdms.exception.OAuth2AuthenticationProcessingException;
import com.smartlogi.sdms.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UtilisateurRepository utilisateurRepository;

    public CustomOAuth2UserService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Récupérer les informations utilisateur depuis le provider
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (OAuth2AuthenticationProcessingException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Erreur lors du traitement OAuth2: {}", ex.getMessage(), ex);
            throw new OAuth2AuthenticationProcessingException(
                    "Erreur lors du traitement de l'authentification OAuth2", ex);
        }
    }

    /**
     * Traite les informations utilisateur OAuth2 et crée/met à jour l'utilisateur.
     */
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // Extraire les informations utilisateur selon le provider
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId, oAuth2User.getAttributes());

        // Valider l'email
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException(
                    "Email non trouvé dans les informations OAuth2 de " + registrationId);
        }

        logger.info("Traitement OAuth2 pour email: {} via {}",
                oAuth2UserInfo.getEmail(), registrationId);

        // Rechercher l'utilisateur existant par email
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository
                .findByEmail(oAuth2UserInfo.getEmail());

        Utilisateur utilisateur;
        AuthProvider provider = OAuth2UserInfoFactory.getAuthProvider(registrationId);

        if (utilisateurOptional.isPresent()) {
            utilisateur = utilisateurOptional.get();

            // Vérifier la cohérence du provider
            if (utilisateur.getProvider() == AuthProvider.LOCAL) {
                // L'utilisateur existait en LOCAL, on le lie au provider OAuth2
                logger.info("Liaison du compte LOCAL {} au provider {}",
                        utilisateur.getEmail(), provider);
                utilisateur = linkOAuth2ToExistingUser(utilisateur, oAuth2UserInfo, provider);
            } else if (utilisateur.getProvider() != provider) {
                // L'utilisateur est déjà lié à un autre provider
                throw new OAuth2AuthenticationProcessingException(
                        "Cet email est déjà associé à un compte " + utilisateur.getProvider() +
                        ". Veuillez vous connecter avec " + utilisateur.getProvider());
            } else {
                // Même provider, mettre à jour les infos
                utilisateur = updateExistingOAuth2User(utilisateur, oAuth2UserInfo);
            }
        } else {
            // Nouvel utilisateur OAuth2
            logger.info("Création d'un nouveau compte OAuth2 pour: {}", oAuth2UserInfo.getEmail());
            utilisateur = registerNewOAuth2User(oAuth2UserInfo, provider);
        }

        return new OAuth2UserPrincipal(utilisateur, oAuth2User.getAttributes());
    }

    /**
     * Lie un compte OAuth2 à un utilisateur LOCAL existant.
     */
    private Utilisateur linkOAuth2ToExistingUser(Utilisateur utilisateur,
                                                   OAuth2UserInfo oAuth2UserInfo,
                                                   AuthProvider provider) {
        utilisateur.setProvider(provider);
        utilisateur.setProviderId(oAuth2UserInfo.getId());

        // Mettre à jour l'image si non définie
        if (!StringUtils.hasText(utilisateur.getImageUrl()) &&
                StringUtils.hasText(oAuth2UserInfo.getImageUrl())) {
            utilisateur.setImageUrl(oAuth2UserInfo.getImageUrl());
        }

        utilisateur.setEmailVerified(true);

        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Met à jour un utilisateur OAuth2 existant.
     */
    private Utilisateur updateExistingOAuth2User(Utilisateur utilisateur,
                                                   OAuth2UserInfo oAuth2UserInfo) {
        // Mettre à jour le nom si vide
        if (!StringUtils.hasText(utilisateur.getNom()) &&
                StringUtils.hasText(oAuth2UserInfo.getLastName())) {
            utilisateur.setNom(oAuth2UserInfo.getLastName());
        }

        // Mettre à jour le prénom si vide
        if (!StringUtils.hasText(utilisateur.getPrenom()) &&
                StringUtils.hasText(oAuth2UserInfo.getFirstName())) {
            utilisateur.setPrenom(oAuth2UserInfo.getFirstName());
        }

        // Mettre à jour l'image
        if (StringUtils.hasText(oAuth2UserInfo.getImageUrl())) {
            utilisateur.setImageUrl(oAuth2UserInfo.getImageUrl());
        }

        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Crée un nouvel utilisateur à partir des informations OAuth2.
     * Par défaut, crée un ClientExpediteur (rôle CLIENT).
     */
    private Utilisateur registerNewOAuth2User(OAuth2UserInfo oAuth2UserInfo,
                                               AuthProvider provider) {
        // Créer un ClientExpediteur par défaut pour les nouveaux utilisateurs OAuth2
        ClientExpediteur utilisateur = new ClientExpediteur();

        utilisateur.setEmail(oAuth2UserInfo.getEmail());
        utilisateur.setNom(oAuth2UserInfo.getLastName());
        utilisateur.setPrenom(oAuth2UserInfo.getFirstName());
        utilisateur.setImageUrl(oAuth2UserInfo.getImageUrl());
        utilisateur.setProvider(provider);
        utilisateur.setProviderId(oAuth2UserInfo.getId());
        utilisateur.setEnabled(true);
        utilisateur.setEmailVerified(true);
        utilisateur.setPassword(null);

        return utilisateurRepository.save(utilisateur);
    }
}

