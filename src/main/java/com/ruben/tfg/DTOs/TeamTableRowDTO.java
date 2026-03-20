package com.ruben.tfg.DTOs;

public record TeamTableRowDTO(
    String teamId,
    String teamName,
    Integer partidos,
    Integer golesFavor,
    Integer golesContra,
    Integer diferenciaGoles,
    Integer victorias,
    Integer empates,
    Integer derrotas,
    Integer puntos
) {}