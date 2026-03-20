package com.ruben.tfg.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "team_season_stats")
@Data
public class TeamSeasonStatsEntity {

    @Id
    private String teamId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "team_id")
    private TeamEntity team;

    private Integer partidos;          
    private Integer goles_favor;       
    private Integer goles_contra;      
    private Integer victorias;         
    private Integer empates;
    private Integer derrotas;
    private Integer puntos;            

    private Double posesion_media;     
    private Double tiros_media;       
    private Double tiros_puerta_media;
    private Double paradas_media;
    private Double tarjetas_media;
}