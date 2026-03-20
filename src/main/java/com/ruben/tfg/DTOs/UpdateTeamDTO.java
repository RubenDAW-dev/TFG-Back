package com.ruben.tfg.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTeamDTO {
    private String nombre;
    private String estadio;
    private String ciudad;
    private Integer capacidad;
    private String escudo;
}
