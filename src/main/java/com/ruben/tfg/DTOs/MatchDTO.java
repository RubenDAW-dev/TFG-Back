package com.ruben.tfg.DTOs;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class MatchDTO {
    private Long id;  
    private Long homeTeam;
    private Long awayTeam;
    private Integer wk;
    private String day;
    private LocalDate date;
    private String time;
    private String score;
    private Integer attendance;
    private String venue;
    private String referee;
}
