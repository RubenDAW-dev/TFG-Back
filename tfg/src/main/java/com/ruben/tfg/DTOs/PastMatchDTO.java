package com.ruben.tfg.DTOs;

import lombok.Data;

@Data
public class PastMatchDTO {
    private String homeTeam;
    private String awayTeam;
    private String score;
}