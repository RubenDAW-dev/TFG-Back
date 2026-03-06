package com.ruben.tfg.utilities;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarRecoveryEmail(String to, String link) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject("Recuperación de contraseña");
        email.setText(
                "Has solicitado restablecer tu contraseña.\n\n" +
                "Haz clic en el siguiente enlace:\n" +
                link + "\n\n" +
                "Si no solicitaste este cambio, ignora este mensaje."
        );
        mailSender.send(email);
    }
}