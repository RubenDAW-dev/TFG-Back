package com.ruben.tfg.DTOs;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class ComentarioResponseDTO {

    private Integer id;
    private String  titulo;       
    private String  comentario;
    private LocalDate fecha;

    private Integer usuarioId;
    private String  usuarioNombre;

    private Long    partidoId;
    private String  equipoId;
    private String  jugadorId;

    private String  targetNombre;

    private Integer comentarioPadreId;
    private List<ComentarioResponseDTO> respuestas;

    private int totalRespuestas;
}