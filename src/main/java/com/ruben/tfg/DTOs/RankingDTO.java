package com.ruben.tfg.DTOs;

public record RankingDTO(
        String playerId,
        String playerName,
        String teamId,
        Integer valor,     // goles o asistencias (según ranking)
        Integer minutos,
        Double valorPor90  // golesPor90 o asistenciasPor90
) {}