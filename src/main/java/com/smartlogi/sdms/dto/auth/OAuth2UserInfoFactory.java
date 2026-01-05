package com.smartlogi.sdms.dto.auth;

import com.smartlogi.sdms.entity.enumeration.AuthProvider;
import com.smartlogi.sdms.exception.OAuth2AuthenticationProcessingException;

import java.util.Map;


public class OAuth2UserInfoFactory {

    private OAuth2UserInfoFactory() {
        // Classe utilitaire - pas d'instanciation
    }


    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId,
                                                    Map<String, Object> attributes) {
        String providerId = registrationId.toUpperCase();

        try {
            AuthProvider provider = AuthProvider.valueOf(providerId);

            return switch (provider) {
                case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
                case FACEBOOK -> new FacebookOAuth2UserInfo(attributes);
                case APPLE -> new AppleOAuth2UserInfo(attributes);
                case OKTA -> new OktaOAuth2UserInfo(attributes);
                case LOCAL -> throw new OAuth2AuthenticationProcessingException(
                        "L'authentification LOCAL ne supporte pas OAuth2");
            };
        } catch (IllegalArgumentException e) {
            throw new OAuth2AuthenticationProcessingException(
                    "Provider OAuth2 non supporté: " + registrationId);
        }
    }


    public static AuthProvider getAuthProvider(String registrationId) {
        try {
            return AuthProvider.valueOf(registrationId.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OAuth2AuthenticationProcessingException(
                    "Provider OAuth2 non supporté: " + registrationId);
        }
    }
}

