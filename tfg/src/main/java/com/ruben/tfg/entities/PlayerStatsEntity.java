package com.ruben.tfg.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "player_stats")
public class PlayerStatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInterno;   

    private Long matchId;     
    private String playerId; 

    private Integer number; 

    private String nation;
    private String pos; 
    private String age;

    private Integer minutes;

    private Integer gls;
    private Integer ast;
    private Integer pk;      
    private Integer pkAtt;    

    private Integer shots;        
    private Integer shotsOnTarget;

    private Integer redCards;

    private Integer foulsCommitted;
    private Integer foulsDrawn;

    private Integer offsides;
    private Integer crosses;

    private Integer tacklesWon;
    private Integer interceptions;

    private Integer ownGoals;
}