package com.ruben.tfg.DTOs;

import lombok.Data;

@Data
public class PastMatchDTO {
	private Long id;  
    private String homeTeam;
    private String homeTeamEscudo;
    private String awayTeam;
    private String awayTeamEscudo;
    private String score;
}