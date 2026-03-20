package com.ruben.tfg.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerDTO {
    private String nombre;
    private String posicion;
    private Integer dorsal;
    private Integer edad;
    private String nacionalidad;
}
