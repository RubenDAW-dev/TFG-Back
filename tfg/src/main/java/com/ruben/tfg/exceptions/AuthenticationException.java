package com.ruben.tfg.exceptions;

/**
 * Excepción para fallos de autenticación (credenciales inválidas, token inválido, etc)
 */
public class AuthenticationException extends RuntimeException {
    
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}