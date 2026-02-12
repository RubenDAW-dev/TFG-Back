package com.ruben.tfg.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "team_season_stats")
public class TeamSeasonStatsEntity {
	@Id
    private String team_id;    
    private String season;      


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