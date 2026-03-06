package com.ruben.tfg.DTOs;

public record PlayerStatsTableDTO(
    String playerId,
    String playerName,
    String teamName,
    Integer partidos,
    Integer minutos,
    Integer goles,
    Integer asistencias
) {}