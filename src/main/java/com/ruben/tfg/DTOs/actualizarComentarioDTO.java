package com.ruben.tfg.DTOs;

import lombok.Data;

@Data
public class actualizarComentarioDTO {
    private Integer id;
    private String titulo;
    private String comentario;
}