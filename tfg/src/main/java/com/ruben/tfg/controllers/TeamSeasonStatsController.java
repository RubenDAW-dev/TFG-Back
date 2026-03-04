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

import com.ruben.tfg.DTOs.TeamTableRowDTO;
import com.ruben.tfg.entities.TeamSeasonStatsEntity;
import com.ruben.tfg.services.TeamSeasonStatsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/team-season-stats")
@RequiredArgsConstructor
public class TeamSeasonStatsController {

    private final TeamSeasonStatsService service;

    // ===== CRUD =====

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        try {
            List<TeamSeasonStatsEntity> lista = service.getAll();
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            return service.getById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TeamSeasonStatsEntity stats) {
        try {
            TeamSeasonStatsEntity creado = service.save(stats);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear stats: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody TeamSeasonStatsEntity stats) {
        try {
            TeamSeasonStatsEntity actualizado = service.save(stats);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Stats no encontradas: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            service.delete(id);
            return ResponseEntity.ok("Stats eliminadas correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Stats no encontradas: " + e.getMessage());
        }
    }

    // ===== Dashboard / Lectura =====

    /**
     * Resumen del equipo para tarjetas del dashboard:
     * - partidos, goles_favor, goles_contra, victorias, empates, derrotas, puntos
     * - y nombre del equipo (vía relación OneToOne TeamEntity)
     */
    @GetMapping("/summary/{teamId}")
    public ResponseEntity<?> summary(@PathVariable String teamId) {
        try {
            return service.summary(teamId)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Resumen no encontrado para equipo: " + teamId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    /**
     * Radar del equipo con métricas medias:
     * - posesion_media, tiros_media, tiros_puerta_media, paradas_media, tarjetas_media
     * - (opcional) goles_por_partido derivado de goles_favor/partidos si quieres incluirlo
     */
    @GetMapping("/radar/{teamId}")
    public ResponseEntity<?> radar(@PathVariable String teamId) {
        try {
            return service.radar(teamId)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Radar no disponible para equipo: " + teamId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    /**
     * Clasificación de la liga:
     * Devuelve filas ordenadas por: puntos DESC, (goles_favor - goles_contra) DESC,
     * goles_favor DESC, victorias DESC.
     */
    @GetMapping("/table")
    public ResponseEntity<?> table() {
        try {
            List<TeamTableRowDTO> tabla = service.table();
            if (tabla.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(tabla);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}