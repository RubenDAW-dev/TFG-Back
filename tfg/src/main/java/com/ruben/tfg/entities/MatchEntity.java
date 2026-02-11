package com.ruben.tfg.entities;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "matches")
public class MatchEntity {

    @Id
    private Long id; 
    private String homeTeamId;
    private String awayTeamId;

    private Integer wk; 
    private String day;

    private LocalDate date;
    private String time;

    private String score;

    private Integer attendance;
    private String venue;
    private String referee;
}