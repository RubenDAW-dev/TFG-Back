package com.ruben.tfg.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ruben.tfg.entities.TeamMatchStatsEntity;
import com.ruben.tfg.services.TeamMatchStatsService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/team-match-stats")
public class TeamMatchStatsController {

    private final TeamMatchStatsService service;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        try {
            List<TeamMatchStatsEntity> lista = service.getAll();
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return service.getById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TeamMatchStatsEntity stats) {
        try {
            TeamMatchStatsEntity creado = service.save(stats);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear stats: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody TeamMatchStatsEntity stats) {
        try {
            TeamMatchStatsEntity actualizado = service.save(stats);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stats no encontradas: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.ok("Stats eliminadas correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stats no encontradas: " + e.getMessage());
        }
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<?> getByMatch(@PathVariable String matchId) {
        try {
            List<TeamMatchStatsEntity> lista = service.getByMatch(matchId);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay stats para el partido: " + matchId);
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getByTeam(@PathVariable String teamId) {
        try {
            List<TeamMatchStatsEntity> lista = service.getByTeam(teamId);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay stats para el equipo: " + teamId);
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/match/{matchId}/side/{side}")
    public ResponseEntity<?> getByMatchAndSide(
            @PathVariable String matchId,
            @PathVariable String side) {
        try {
            List<TeamMatchStatsEntity> lista = service.getByMatchAndSide(matchId, side);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay stats para partido: " + matchId + " y lado: " + side);
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}