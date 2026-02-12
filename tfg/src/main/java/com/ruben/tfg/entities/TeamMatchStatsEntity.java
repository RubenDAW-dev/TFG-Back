package com.ruben.tfg.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "team__match_stats")
public class TeamMatchStatsEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String match_id;
    private String team_id;
    private String side;

    private Integer possession;
    private Integer shots_on_target;
    private Integer shots_total;
    private Integer saves;
    private Integer cards;

} 