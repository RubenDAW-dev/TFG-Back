package com.ruben.tfg.exceptions;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

   // ═════════════════════════════════════════════════════════════════════════
   // VALIDACIONES (Bean Validation)
   // ═════════════════════════════════════════════════════════════════════════

   @ExceptionHandler(MethodArgumentNotValidException.class)
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
       Map<String, Object> response = new HashMap<>();
       response.put("timestamp", LocalDateTime.now());
       response.put("status", HttpStatus.BAD_REQUEST.value());
       response.put("error", "Error de validación");

       Map<String, String> errors = ex.getBindingResult()
               .getFieldErrors()
               .stream()
               .collect(Collectors.toMap(
                       field -> field.getField(),
                       field -> field.getDefaultMessage(),
                       (existing, replacement) -> existing
               ));

       response.put("errors", errors);

       log.warn("Error de validación: {}", errors);

       return ResponseEntity.badRequest().body(response);
   }

   // ═════════════════════════════════════════════════════════════════════════
   // AUTENTICACIÓN
   // ═════════════════════════════════════════════════════════════════════════

   @ExceptionHandler(AuthenticationException.class)
   @ResponseStatus(HttpStatus.UNAUTHORIZED)
   public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
       Map<String, Object> response = new HashMap<>();
       response.put("timestamp", LocalDateTime.now());
       response.put("status", HttpStatus.UNAUTHORIZED.value());
       response.put("error", "Autenticación fallida");
       response.put("message", ex.getMessage());

       log.warn("Fallo de autenticación: {}", ex.getMessage());

       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
   }

   // ═════════════════════════════════════════════════════════════════════════
   // AUTORIZACIÓN
   // ═════════════════════════════════════════════════════════════════════════

   @ExceptionHandler(AccessDeniedException.class)
   @ResponseStatus(HttpStatus.FORBIDDEN)
   public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
       Map<String, Object> response = new HashMap<>();
       response.put("timestamp", LocalDateTime.now());
       response.put("status", HttpStatus.FORBIDDEN.value());
       response.put("error", "Acceso denegado");
       response.put("message", ex.getMessage());

       log.warn("Acceso denegado: {}", ex.getMessage());

       return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
   }

   // ═════════════════════════════════════════════════════════════════════════
   // RECURSOS NO ENCONTRADOS
   // ═════════════════════════════════════════════════════════════════════════

   @ExceptionHandler(ResourceNotFoundException.class)
   @ResponseStatus(HttpStatus.NOT_FOUND)
   public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
       Map<String, Object> response = new HashMap<>();
       response.put("timestamp", LocalDateTime.now());
       response.put("status", HttpStatus.NOT_FOUND.value());
       response.put("error", "Recurso no encontrado");
       response.put("message", ex.getMessage());

       log.warn("Recurso no encontrado: {}", ex.getMessage());

       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
   }

   // ═════════════════════════════════════════════════════════════════════════
   // ERRORES DE NEGOCIO (Token inválido, caducado, etc)
   // ═════════════════════════════════════════════════════════════════════════

   @ExceptionHandler(IllegalArgumentException.class)
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
       Map<String, Object> response = new HashMap<>();
       response.put("timestamp", LocalDateTime.now());
       response.put("status", HttpStatus.BAD_REQUEST.value());
       response.put("error", "Solicitud inválida");
       response.put("message", ex.getMessage());

       log.warn("Argumento ilegal: {}", ex.getMessage());

       return ResponseEntity.badRequest().body(response);
   }

   // ═════════════════════════════════════════════════════════════════════════
   // EXCEPCIONES GENÉRICAS
   // ═════════════════════════════════════════════════════════════════════════

   @ExceptionHandler(Exception.class)
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public ResponseEntity<?> handleGlobalException(Exception ex) {
       Map<String, Object> response = new HashMap<>();
       response.put("timestamp", LocalDateTime.now());
       response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
       response.put("error", "Error interno del servidor");
       response.put("message", "Algo salió mal. Por favor, intenta más tarde.");

       log.error("Error inesperado", ex);

       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
   }

   // ═════════════════════════════════════════════════════════════════════════
   // EXCEPCIONES DE RUNTIME
   // ═════════════════════════════════════════════════════════════════════════

   @ExceptionHandler(RuntimeException.class)
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
       Map<String, Object> response = new HashMap<>();
       response.put("timestamp", LocalDateTime.now());
       response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
       response.put("error", "Error de ejecución");
       response.put("message", ex.getMessage());

       log.error("RuntimeException: ", ex);

       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
   }
}
