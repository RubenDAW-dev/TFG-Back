package com.ruben.tfg.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruben.tfg.DTOs.ComentarioResponseDTO;
import com.ruben.tfg.DTOs.CrearComentarioDTO;
import com.ruben.tfg.DTOs.actualizarComentarioDTO;
import com.ruben.tfg.services.ComentarioService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;

    // ── CREAR ─────────────────────────────────────────────────────────────────

    @PostMapping("/create")
    public ResponseEntity<ComentarioResponseDTO> crear(@RequestBody CrearComentarioDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comentarioService.crearComentario(req));
    }

    // ── ACTUALIZAR ────────────────────────────────────────────────────────────

    @PutMapping("/update")
    public ResponseEntity<?> actualizar(@RequestBody actualizarComentarioDTO req) {
        try {
            ComentarioResponseDTO actualizado = comentarioService.actualizarComentario(req);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Comentario no encontrado: " + e.getMessage());
        }
    }

    // ── ELIMINAR ──────────────────────────────────────────────────────────────

    @DeleteMapping("/delete/{comentarioId}")
    public ResponseEntity<?> eliminar(@PathVariable Integer comentarioId) {
        try {
            comentarioService.eliminarComentario(comentarioId);
            return ResponseEntity.ok("Comentario eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Comentario no encontrado: " + e.getMessage());
        }
    }

    // ── FORO GENERAL: lista de todos los topics raíz ──────────────────────────

    @GetMapping("/foro")
    public ResponseEntity<?> foro() {
        try {
            List<ComentarioResponseDTO> lista = comentarioService.listarTopicsForo();
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // ── TOPIC INDIVIDUAL (vista de hilo) ──────────────────────────────────────

    @GetMapping("/{comentarioId}")
    public ResponseEntity<?> topic(@PathVariable Integer comentarioId) {
        try {
            ComentarioResponseDTO dto = comentarioService.obtenerTopic(comentarioId);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Comentario no encontrado: " + e.getMessage());
        }
    }

    // ── CONTEXTUALIZADOS ──────────────────────────────────────────────────────

    @GetMapping("/partido/{matchId}")
    public ResponseEntity<?> porPartido(@PathVariable Long matchId) {
        try {
            List<ComentarioResponseDTO> lista = comentarioService.listarPorPartido(matchId);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/equipo/{teamId}")
    public ResponseEntity<?> porEquipo(@PathVariable String teamId) {
        try {
            List<ComentarioResponseDTO> lista = comentarioService.listarPorEquipo(teamId);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/jugador/{playerId}")
    public ResponseEntity<?> porJugador(@PathVariable String playerId) {
        try {
            List<ComentarioResponseDTO> lista = comentarioService.listarPorJugador(playerId);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // ── RESPUESTAS DE UN TOPIC ────────────────────────────────────────────────

    @GetMapping("/{comentarioId}/respuestas")
    public ResponseEntity<?> respuestas(@PathVariable Integer comentarioId) {
        try {
            List<ComentarioResponseDTO> lista = comentarioService.respuestasDe(comentarioId);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Comentario no encontrado: " + e.getMessage());
        }
    }
}