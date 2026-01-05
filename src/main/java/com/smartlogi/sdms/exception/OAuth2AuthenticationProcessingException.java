package com.smartlogi.sdms.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception levée lors d'erreurs dans le processus d'authentification OAuth2.
 */
public class OAuth2AuthenticationProcessingException extends AuthenticationException {

    public OAuth2AuthenticationProcessingException(String message) {
        super(message);
    }

    public OAuth2AuthenticationProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

