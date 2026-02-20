package com.ruben.tfg.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CrearComentarioDTO {
	private String comentario; // texto del comentario

    // uno de estos tres (exactamente uno)
    private String equipoId;   // teams.id
    private String jugadorId;  // players.id
    private Long partidoId;    // matches.id

    private Integer usuarioId;         // usuarios.id (autor)
    private Integer comentarioPadreId; // opcional (para responder)


}
