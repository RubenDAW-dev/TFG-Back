package com.ruben.tfg.DTOs;

public record TeamSummaryDTO(
    String teamId,
    String teamName,
    Integer partidos,
    Integer golesFavor,
    Integer golesContra,
    Integer victorias,
    Integer empates,
    Integer derrotas,
    Integer puntos
) {}