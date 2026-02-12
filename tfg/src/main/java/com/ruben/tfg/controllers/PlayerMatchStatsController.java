package com.ruben.tfg.controllers;

import com.ruben.tfg.entities.PlayerMatchStatsEntity;
import com.ruben.tfg.services.PlayerMatchStatsService;
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
    public List<PlayerMatchStatsEntity> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public PlayerMatchStatsEntity getById(@PathVariable Long id) {
        return service.getById(id).orElse(null);
    }

    @GetMapping("/match/{matchId}")
    public List<PlayerMatchStatsEntity> getByMatch(@PathVariable Long matchId) {
        return service.getByMatch(matchId);
    }

    @GetMapping("/player/{playerId}")
    public List<PlayerMatchStatsEntity> getByPlayer(@PathVariable String playerId) {
        return service.getByPlayer(playerId);
    }

    @PostMapping
    public PlayerMatchStatsEntity create(@RequestBody PlayerMatchStatsEntity stats) {
        return service.save(stats);
    }

    @PutMapping("/{id}")
    public PlayerMatchStatsEntity update(
            @PathVariable Long id,
            @RequestBody PlayerMatchStatsEntity stats
    ) {
        stats.setIdInterno(id);
        return service.save(stats);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}