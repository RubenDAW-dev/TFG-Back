package com.ruben.tfg.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ruben.tfg.DTOs.ForgotPasswordRequestDTO;
import com.ruben.tfg.DTOs.ResetPasswordRequestDTO;
import com.ruben.tfg.entities.TokenRecuperacionEntity;
import com.ruben.tfg.entities.UsuarioEntity;
import com.ruben.tfg.repositories.TokenRecuperacionRepository;
import com.ruben.tfg.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {

    private final UsuarioRepository usuarioRepo;
    private final TokenRecuperacionRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;

    // Caducidad por defecto: 60 min
    private static final int TOKEN_EXP_MINUTES = 60;

    public void purgeExpiredTokens() {
        tokenRepo.deleteByFechaExpiracionBefore(LocalDateTime.now());
    }

    @Transactional
    public String createRecoveryToken(ForgotPasswordRequestDTO req, String appBaseUrl) {
        Optional<UsuarioEntity> opt = usuarioRepo.findByEmail(req.getEmail().trim().toLowerCase());
        if (opt.isEmpty()) {
            // Por seguridad, no revelamos si el email existe o no.
            return null;
        }

        UsuarioEntity user = opt.get();

        // 1 usuario → 1 token activo: eliminamos anteriores
        tokenRepo.deleteByUsuario(user);

        // Generar token seguro
        String token = generateSecureToken();

        TokenRecuperacionEntity tr = new TokenRecuperacionEntity();
        tr.setUsuario(user);
        tr.setToken(token);
        tr.setFechaExpiracion(LocalDateTime.now().plusMinutes(TOKEN_EXP_MINUTES));
        tokenRepo.save(tr);

        // Enlace (ajusta el front-route si lo manejas en Angular)
        String link = appBaseUrl.replaceAll("/+$", "") + "/reset-password?token=" + token;

        // Enviar “email” (por ahora, stub)
        sendEmailStub(user.getEmail(), link);

        return token;
    }

    public boolean isTokenValid(String token) {
        return tokenRepo.findByToken(token)
                .filter(t -> t.getFechaExpiracion().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO req) {
        TokenRecuperacionEntity tr = tokenRepo.findByToken(req.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (tr.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token caducado");
        }

        UsuarioEntity user = tr.getUsuario();
        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        usuarioRepo.save(user);

        // invalidar el token usado
        tokenRepo.delete(tr);
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32]; // 256 bits
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void sendEmailStub(String email, String link) {
        System.out.println("=== RECOVERY EMAIL ===");
        System.out.println("To: " + email);
        System.out.println("Link: " + link);
        System.out.println("======================");
        // Integra aquí JavaMailSender/SendGrid/etc. cuando quieras
    }
}
