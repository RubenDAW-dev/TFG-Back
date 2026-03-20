package com.ruben.tfg.utilities;

import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.ruben.tfg.repositories.TokenRecuperacionRepository;
import lombok.RequiredArgsConstructor;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final TokenRecuperacionRepository tokenRepo;

    // cada hora
    @Scheduled(cron = "0 0 * * * *")
    public void purgeExpired() {
        tokenRepo.deleteByFechaExpiracionBefore(LocalDateTime.now());
    }
}