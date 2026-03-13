package com.ruben.tfg.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearComentarioDTO {

    @Size(max = 200, message = "El título no puede superar los 200 caracteres")
    private String titulo;

    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(max = 5000, message = "El comentario no puede superar los 5000 caracteres")
    private String comentario;

    @NotNull(message = "El usuario es obligatorio")
    private Integer usuarioId;

    // Exactamente uno de los tres debe estar presente
    private Long    partidoId;
    private String  equipoId;
    private String  jugadorId;

    // Null = topic raíz; relleno = respuesta a otro comentario
    private Integer comentarioPadreId;
}