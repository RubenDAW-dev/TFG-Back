package com.ruben.tfg.DTOs;

import lombok.Data;

@Data
public class FutureMatchDTO {
	private Long id;  
    private String homeTeam;
    private String homeTeamEscudo;
    private String awayTeam;
    private String awayTeamEscudo;
    private String day;
    private String time;
}