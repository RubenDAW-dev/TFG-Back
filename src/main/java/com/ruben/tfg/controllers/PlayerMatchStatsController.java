package com.ruben.tfg.controllers;

import com.ruben.tfg.entities.PlayerMatchStatsEntity;
import com.ruben.tfg.services.PlayerMatchStatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/player-stats")
public class PlayerMatchStatsController {

    private final PlayerMatchStatsService service;

    public PlayerMatchStatsController(PlayerMatchStatsService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        try {
            List<PlayerMatchStatsEntity> lista = service.getAll();
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return service.getById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<?> getByMatch(@PathVariable Long matchId) {
        try {
            List<PlayerMatchStatsEntity> lista = service.getByMatch(matchId);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<?> getByPlayer(@PathVariable String playerId) {
        try {
            List<PlayerMatchStatsEntity> lista = service.getByPlayer(playerId);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PlayerMatchStatsEntity stats) {
        try {
            PlayerMatchStatsEntity creado = service.save(stats);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear stats: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody PlayerMatchStatsEntity stats) {
        try {
            stats.setIdInterno(id);
            PlayerMatchStatsEntity actualizado = service.save(stats);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stats no encontradas: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.ok("Stats eliminadas correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stats no encontradas: " + e.getMessage());
        }
    }
}