package com.ruben.tfg.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ruben.tfg.DTOs.CrearComentarioDTO;
import com.ruben.tfg.entities.ComentarioEntity;
import com.ruben.tfg.services.ComentarioService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody CrearComentarioDTO req) {
        try {
            ComentarioEntity creado = comentarioService.crearComentario(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear comentario: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> actualizar(@RequestBody ComentarioEntity comentario) {
        try {
            ComentarioEntity actualizado = comentarioService.actualizarComentario(comentario);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comentario no encontrado: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{comentarioId}")
    public ResponseEntity<?> eliminar(@PathVariable Integer comentarioId) {
        try {
            comentarioService.eliminarComentario(comentarioId);
            return ResponseEntity.ok("Comentario eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comentario no encontrado: " + e.getMessage());
        }
    }

    @GetMapping("/partido/{matchId}")
    public ResponseEntity<?> porPartido(@PathVariable Long matchId) {
        try {
            List<ComentarioEntity> lista = comentarioService.listarPorPartido(matchId);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/equipo/{teamId}")
    public ResponseEntity<?> porEquipo(@PathVariable String teamId) {
        try {
            List<ComentarioEntity> lista = comentarioService.listarPorEquipo(teamId);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/jugador/{playerId}")
    public ResponseEntity<?> porJugador(@PathVariable String playerId) {
        try {
            List<ComentarioEntity> lista = comentarioService.listarPorJugador(playerId);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{comentarioId}/respuestas")
    public ResponseEntity<?> respuestas(@PathVariable Integer comentarioId) {
        try {
            List<ComentarioEntity> lista = comentarioService.respuestasDe(comentarioId);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comentario no encontrado: " + e.getMessage());
        }
    }
}