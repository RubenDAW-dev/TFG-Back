package com.ruben.tfg.DTOs;

import lombok.Data;

@Data
public class FutureMatchDTO {
	private Long id;  
    private String homeTeam;
    private String awayTeam;
    private String day;
    private String time;
}