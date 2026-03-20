package com.ruben.tfg.utilities;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ruben.tfg.services.PasswordRecoveryService;

import lombok.RequiredArgsConstructor;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class LimpiezaTokensJob {

    private final PasswordRecoveryService recuperacionService;

    /**
     * Ejecuta cada hora (cron: minuto 0 cada hora).
     */
    @Scheduled(cron = "0 0 * * * *")
    public void limpiarTokens() {
        recuperacionService.purgeExpiredTokens();
    }
}