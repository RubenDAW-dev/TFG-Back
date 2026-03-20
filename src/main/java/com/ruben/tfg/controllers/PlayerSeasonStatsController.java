package com.ruben.tfg.controllers;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruben.tfg.DTOs.PlayerSeasonStatsDTO;
import com.ruben.tfg.DTOs.PlayerStatsTableDTO;
import com.ruben.tfg.DTOs.RankingDTO;
import com.ruben.tfg.entities.PlayerSeasonStatsEntity;
import com.ruben.tfg.services.PlayerSeasonStatsService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/player-season-stats")
public class PlayerSeasonStatsController {

    private final PlayerSeasonStatsService service;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "goles") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            List<PlayerStatsTableDTO> resultado = service.getAllWithNamesSorted(sortField, sortDir);
            if (resultado.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            List<PlayerSeasonStatsEntity> lista = service.getById(id);
            if (lista.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stats no encontradas para jugador: " + id);
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
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody PlayerSeasonStatsEntity stats) {
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

    @GetMapping("/team/top-scorers")
    public ResponseEntity<List<RankingDTO>> topScorers(@RequestParam(required = false) String teamId,
            @RequestParam(defaultValue = "5") int limit) {
        var data = service.topScorers(teamId, PageRequest.of(0, Math.max(1, limit)));
        return ResponseEntity.ok(data);
    }

    @GetMapping("/team/top-assists")
    public ResponseEntity<List<RankingDTO>> topAssists(@RequestParam(required = false) String teamId,
            @RequestParam(defaultValue = "5") int limit) {
        var data = service.topAssists(teamId, PageRequest.of(0, Math.max(1, limit)));
        return ResponseEntity.ok(data);
    }

    @GetMapping("/team/{teamId}/players")
    public ResponseEntity<List<PlayerStatsTableDTO>> aggregateByTeam(@PathVariable String teamId) {
        var result = service.getByTeamId(teamId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/top-scorers")
    public ResponseEntity<List<RankingDTO>> topScorersOverall(@RequestParam(defaultValue = "5") int limit) {
        var data = service.AlltopScorers(PageRequest.of(0, Math.max(1, limit)));
        return ResponseEntity.ok(data);
    }

    @GetMapping("/top-assists")
    public ResponseEntity<List<RankingDTO>> topAssistsOverall(@RequestParam(defaultValue = "5") int limit) {
        var data = service.AlltopAssists(PageRequest.of(0, Math.max(1, limit)));
        return ResponseEntity.ok(data);
    }
}