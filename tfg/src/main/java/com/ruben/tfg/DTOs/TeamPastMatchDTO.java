package com.ruben.tfg.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamPastMatchDTO {
    private Long id;
    private String homeTeamId;
    private String homeTeam;
    private String awayTeamId;
    private String awayTeam;
    private String day;
    private String time;
    private String venue;
    private Integer wk;
    private String date;
    private String score;
}
