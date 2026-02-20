package com.ruben.tfg.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ruben.tfg.entities.PlayerSeasonStatsEntity;
import com.ruben.tfg.services.PlayerSeasonStatsService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/player-season-stats")
public class PlayerSeasonStatsController {

    private final PlayerSeasonStatsService service;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        try {
            List<PlayerSeasonStatsEntity> lista = service.getAll();
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            List<PlayerSeasonStatsEntity> lista = service.getById(id);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stats no encontradas para jugador: " + id);
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PlayerSeasonStatsEntity stats) {
        try {
            PlayerSeasonStatsEntity creado = service.save(stats);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear stats: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable String id,
            @RequestBody PlayerSeasonStatsEntity stats) {
        try {
            stats.setPlayerId(id);
            PlayerSeasonStatsEntity actualizado = service.save(stats);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stats no encontradas: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            service.delete(id);
            return ResponseEntity.ok("Stats eliminadas correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stats no encontradas: " + e.getMessage());
        }
    }
}