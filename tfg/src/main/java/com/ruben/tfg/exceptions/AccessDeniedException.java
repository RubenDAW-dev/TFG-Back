package com.ruben.tfg.exceptions;

/**
 * Excepción para fallos de autorización (usuario sin permisos)
 */
public class AccessDeniedException extends RuntimeException {
    
    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}