package com.ruben.tfg.DTOs;

public record TeamPlayerAggDTO(
    String teamId,
    Long goles,
    Long asistencias,
    Long disparos,
    Long disparosPuerta,
    Long amarillas,
    Long rojas,
    Long faltasCometidas,
    Long centros,
    Long entradasGanadas,
    Long intercepciones,
    Long autogoles,
    Double precisionTiroMedia,
    Double conversionPenaltiMedia
) {}