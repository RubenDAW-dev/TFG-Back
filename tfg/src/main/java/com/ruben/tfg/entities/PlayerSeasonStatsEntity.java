package com.ruben.tfg.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "player_season_stats")
public class PlayerSeasonStatsEntity {

    @Id
    @Column(name = "player_id", nullable = false, length = 64)
    private String playerId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "player_id", nullable = false, updatable = false, insertable = false)
    @JsonIgnore
    private PlayerEntity player;

    @Column(name = "partidos")
    private Integer partidos;

    @Column(name = "minutos")
    private Integer minutos;

    @Column(name = "goles")
    private Integer goles;

    @Column(name = "asistencias")
    private Integer asistencias;

    @Column(name = "penaltis_marcados")
    private Integer penaltisMarcados;

    @Column(name = "penaltis_intentados")
    private Integer penaltisIntentados;

    @Column(name = "disparos")
    private Integer disparos;

    @Column(name = "disparos_puerta")
    private Integer disparosPuerta;

    @Column(name = "amarillas")
    private Integer amarillas;

    @Column(name = "rojas")
    private Integer rojas;

    @Column(name = "faltas_cometidas")
    private Integer faltasCometidas;

    @Column(name = "faltas_recibidas")
    private Integer faltasRecibidas;

    @Column(name = "fuera_de_juego")
    private Integer fueraDeJuego;

    @Column(name = "centros")
    private Integer centros;

    @Column(name = "entradas_ganadas")
    private Integer entradasGanadas;

    @Column(name = "intercepciones")
    private Integer intercepciones;

    @Column(name = "autogoles")
    private Integer autogoles;

    @Column(name = "goles_por_90")
    private Double golesPor90;

    @Column(name = "asistencias_por_90")
    private Double asistenciasPor90;

    @Column(name = "disparos_por_90")
    private Double disparosPor90;

    @Column(name = "disparos_puerta_por_90")
    private Double disparosPuertaPor90;

    @Column(name = "amarillas_por_90")
    private Double amarillasPor90;

    @Column(name = "rojas_por_90")
    private Double rojasPor90;

    @Column(name = "faltas_cometidas_por_90")
    private Double faltasCometidasPor90;

    @Column(name = "faltas_recibidas_por_90")
    private Double faltasRecibidasPor90;

    @Column(name = "fuera_de_juego_por_90")
    private Double fueraDeJuegoPor90;

    @Column(name = "centros_por_90")
    private Double centrosPor90;

    @Column(name = "entradas_ganadas_por_90")
    private Double entradasGanadasPor90;

    @Column(name = "intercepciones_por_90")
    private Double intercepcionesPor90;

    @Column(name = "precision_tiro")
    private Double precisionTiro;

    @Column(name = "conversion_penalti")
    private Double conversionPenalti;
}