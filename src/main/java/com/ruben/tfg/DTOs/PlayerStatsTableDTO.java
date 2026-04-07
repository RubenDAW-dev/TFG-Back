package com.ruben.tfg.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerStatsTableDTO{
    private String playerId;
    private String playerName;
    private String teamName;
    private Integer partidos;
    private Integer minutos;
    private  Integer goles;
    private  Integer asistencias;
    private Integer penaltisMarcados;
    private Integer penaltisIntentados;
    private Integer disparos;
    private Integer disparosPuerta;
    private Integer amarillas;
    private Integer rojas;
    private Integer faltasCometidas;
    private Integer faltasRecibidas;
    private Integer fueraDeJuego;
    private Integer centros;
    private Integer entradasGanadas;
    private Integer intercepciones;
    private Integer autogoles;
    private Double golesPor90;
    private Double asistenciasPor90;
    private Double disparosPor90;
    private Double disparosPuertaPor90;
    private Double amarillasPor90;
    private Double rojasPor90;
    private Double faltasCometidasPor90;
    private Double faltasRecibidasPor90;
    private Double fueraDeJuegoPor90;
    private Double centrosPor90;
    private Double entradasGanadasPor90;
    private Double intercepcionesPor90;
    private Double precisionTiro;
    private Double conversionPenalti;
    private String imageUrl;
}