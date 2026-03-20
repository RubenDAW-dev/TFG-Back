package com.ruben.tfg.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import com.ruben.tfg.DTOs.ForgotPasswordRequestDTO;
import com.ruben.tfg.DTOs.ResetPasswordRequestDTO;
import com.ruben.tfg.entities.TokenRecuperacionEntity;
import com.ruben.tfg.entities.UsuarioEntity;
import com.ruben.tfg.repositories.TokenRecuperacionRepository;
import com.ruben.tfg.repositories.UsuarioRepository;
import com.ruben.tfg.utilities.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {

    private final UsuarioRepository usuarioRepo;
    private final TokenRecuperacionRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Token expira en 60 minutos
    private static final int TOKEN_EXP_MINUTES = 60;

    /**
     * Ejecutar cada 1 hora para limpiar tokens expirados
     * Reduce el tamaño de la base de datos y mejora performance
     */
    @Scheduled(fixedRate = 3600000) // 1 hora en milisegundos = 3.600.000 ms
    @Transactional
    public void purgeExpiredTokens() {
        try {
            tokenRepo.deleteByFechaExpiracionBefore(LocalDateTime.now());
            log.info("Tarea de purga de tokens expirados ejecutada");
        } catch (Exception e) {
            log.error("Error al purgar tokens expirados", e);
        }
    }

    @Transactional
    public String createRecoveryToken(ForgotPasswordRequestDTO req, String appBaseUrl) {
        String email = req.getEmail().trim().toLowerCase();
        
        Optional<UsuarioEntity> opt = usuarioRepo.findByEmail(email);
        
        if (opt.isEmpty()) {
            // Seguridad: No revelamos si el email existe
            log.warn("Intento de recuperación con email inexistente: {}", email);
            return null;
        }

        UsuarioEntity user = opt.get();
        log.info("Iniciando recuperación de contraseña para: {}", email);

        try {
            // 1. Eliminar tokens anteriores no utilizados
            tokenRepo.deleteByUsuario(user);

            // 2. Generar token seguro
            String token = generateSecureToken();
            log.debug("Token generado para usuario: {}", user.getId());

            // 3. Guardar en BD
            TokenRecuperacionEntity tr = new TokenRecuperacionEntity();
            tr.setUsuario(user);
            tr.setToken(token);
            tr.setFechaExpiracion(LocalDateTime.now().plusMinutes(TOKEN_EXP_MINUTES));
            tokenRepo.save(tr);

            log.info("Token almacenado en BD. Expira en {} minutos", TOKEN_EXP_MINUTES);

            // 4. Construir link de reset
            String link = appBaseUrl.replaceAll("/+$", "") + "/reset-password?token=" + token;

            // 5. Enviar email
            emailService.enviarRecoveryEmail(user.getEmail(), link);
            log.info("Email de recuperación enviado a: {}", user.getEmail());

            return token;

        } catch (Exception e) {
            log.error("Error al crear token de recuperación para usuario: {}", user.getId(), e);
            throw new RuntimeException("Error al procesar la solicitud de recuperación", e);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            boolean valid = tokenRepo.findByToken(token)
                    .filter(t -> t.getFechaExpiracion().isAfter(LocalDateTime.now()))
                    .isPresent();

            if (!valid) {
                log.warn("Intento de verificación con token inválido o expirado");
            }

            return valid;
        } catch (Exception e) {
            log.error("Error al verificar token", e);
            return false;
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO req) {
        log.info("Iniciando reset de contraseña");

        try {
            // 1. Buscar token
            TokenRecuperacionEntity tr = tokenRepo.findByToken(req.getToken())
                    .orElseThrow(() -> {
                        log.warn("Intento de reset con token inexistente");
                        return new IllegalArgumentException("Token inválido");
                    });

            // 2. Verificar que no esté expirado
            if (tr.getFechaExpiracion().isBefore(LocalDateTime.now())) {
                log.warn("Intento de reset con token expirado");
                throw new IllegalArgumentException("Token caducado");
            }

            // 3. Obtener usuario
            UsuarioEntity user = tr.getUsuario();
            log.info("Reseteando contraseña para usuario: {}", user.getEmail());

            // 4. Actualizar contraseña (ya está validada por Bean Validation)
            String hashedPassword = passwordEncoder.encode(req.getNewPassword());
            user.setPasswordHash(hashedPassword);
            usuarioRepo.save(user);

            log.info("Contraseña actualizada para usuario: {}", user.getEmail());

            // 5. Invalidar el token usado
            tokenRepo.delete(tr);
            log.info("Token invalidado");

        } catch (IllegalArgumentException e) {
            log.warn("Error en reset: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado en reset de contraseña", e);
            throw new RuntimeException("Error al resetear contraseña", e);
        }
    }

    /**
     * Genera un token seguro de 256 bits
     * Usa SecureRandom + Base64 URL-safe
     */
    private String generateSecureToken() {
        byte[] bytes = new byte[32]; // 256 bits
        new SecureRandom().nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        log.debug("Nuevo token generado (32 bytes / 256 bits)");
        return token;
    }
}