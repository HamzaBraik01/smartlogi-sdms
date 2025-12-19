package com.smartlogi.sdms.exception;

/**
 * Exception levée lorsqu'une ressource existe déjà.
 * Exemple: tentative de création d'un utilisateur avec un email existant.
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

