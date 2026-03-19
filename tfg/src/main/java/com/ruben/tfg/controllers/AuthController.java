package com.ruben.tfg.controllers;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruben.tfg.DTOs.ForgotPasswordRequestDTO;
import com.ruben.tfg.DTOs.LoginRequestDTO;
import com.ruben.tfg.DTOs.ResetPasswordRequestDTO;
import com.ruben.tfg.entities.UsuarioEntity;
import com.ruben.tfg.exceptions.AuthenticationException;
import com.ruben.tfg.repositories.UsuarioRepository;
import com.ruben.tfg.services.JwtService;
import com.ruben.tfg.services.PasswordRecoveryService;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final PasswordRecoveryService recoveryService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO req) {
        recoveryService.createRecoveryToken(req, "http://localhost:4200");
        // 200 OK aunque el email no exista (no exponemos información)
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify-reset-token")
    public ResponseEntity<?> verifyToken(@RequestParam("token") String token) {
        boolean ok = recoveryService.isTokenValid(token);
        return ok ? ResponseEntity.ok().build()
                  : ResponseEntity.badRequest().body("Token inválido o caducado");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO req) {
        recoveryService.resetPassword(req);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO req) {
        log.info("Intento de login para: {}", req.getEmail());
        
        UsuarioEntity user = usuarioRepo.findByEmail(req.getEmail().trim().toLowerCase())
            .orElseThrow(() -> {
                log.warn("Usuario no encontrado: {}", req.getEmail());
                return new AuthenticationException("Credenciales inválidas");
            });
        
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            log.warn("Contraseña incorrecta para usuario: {}", req.getEmail());
            throw new AuthenticationException("Credenciales inválidas");
        }
        
        String token = jwtService.generateToken(user);
        log.info("Login exitoso para usuario: {}", user.getEmail());
        
        return ResponseEntity.ok(Map.of(
            "token", token,
            "user", Map.of(
                "id", user.getId(),
                "nombre", user.getNombre(),
                "email", user.getEmail(),
                "rol", user.getRol()
            )
        ));
    }
}