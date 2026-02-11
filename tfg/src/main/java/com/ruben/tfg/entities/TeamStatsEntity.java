package com.ruben.tfg.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "team_stats")
public class TeamStatsEntity {

    @Id
    private Long id;  // este es match_id, limpiando el .0 del CSV

    private String homeTeamId;
    private String awayTeamId;

    private Integer possHome;
    private Integer possAway;

    private Integer shotsOtHome;
    private Integer shotsTotalHome;

    private Integer shotsOtAway;
    private Integer shotsTotalAway;

    private Integer savesHome;
    private Integer savesAway;

    private Integer cardsHome;
    private Integer cardsAway;
}