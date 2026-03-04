package com.ruben.tfg.DTOs;

import lombok.Data;

@Data
public class FutureMatchDTO {
    private String homeTeam;
    private String awayTeam;
    private String day;
    private String time;
}