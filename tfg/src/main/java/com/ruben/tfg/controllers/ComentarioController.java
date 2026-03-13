package com.ruben.tfg.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ruben.tfg.DTOs.ComentarioResponseDTO;
import com.ruben.tfg.DTOs.CrearComentarioDTO;
import com.ruben.tfg.entities.ComentarioEntity;
import com.ruben.tfg.services.ComentarioService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;

    // ── CREAR ─────────────────────────────────────────────────────────────────

 // En ComentarioController, reemplaza el método crear():

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody CrearComentarioDTO req) {
        try {
            ComentarioEntity creado = comentarioService.crearComentario(req);
            ComentarioResponseDTO dto = new ComentarioResponseDTO();
            dto.setId(creado.getId());
            dto.setComentario(creado.getComentario());
            dto.setTitulo(creado.getTitulo());
            dto.setFecha(creado.getFecha());
            dto.setUsuarioId(creado.getUsuario().getId());
            dto.setUsuarioNombre(creado.getUsuario().getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Datos inválidos: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear comentario: " + e.getMessage());
        }
    }

    // ── ACTUALIZAR ────────────────────────────────────────────────────────────

    @PutMapping("/update")
    public ResponseEntity<?> actualizar(@RequestBody ComentarioEntity comentario) {
        try {
            ComentarioEntity actualizado = comentarioService.actualizarComentario(comentario);
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