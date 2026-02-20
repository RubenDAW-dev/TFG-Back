package com.ruben.tfg.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "player_season_stats")
public class PlayerSeasonStatsEntity {

    @Id
    private String playerId; 

    @OneToOne
    @MapsId
    @JoinColumn(name = "player_id")
    private PlayerEntity player;

    private Integer partidos;            
    private Integer minutos;             
    private Integer goles;               
    private Integer asistencias;        
    private Integer disparos;           
    private Integer disparos_puerta;     
    private Integer amarillas;           
    private Integer rojas;               
    private Integer pases;
    private Integer pases_completados; 
    private Integer duelos_ganados;
    private Integer duelos_totales;

    private Double precision_pases;      
    private Double duelos_winrate;      
    private Double goles_por_90;   
    private Double asistencias_por_90;
}
