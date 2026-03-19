package com.ruben.tfg.exceptions;

/**
 * Excepción para recursos no encontrados (usuario, equipo, etc no existe)
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}