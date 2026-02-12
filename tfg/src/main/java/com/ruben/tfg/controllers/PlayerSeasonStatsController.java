package com.ruben.tfg.controllers;

import com.ruben.tfg.entities.PlayerSeasonStatsEntity;
import com.ruben.tfg.services.PlayerSeasonStatsService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/player-season-stats")
public class PlayerSeasonStatsController {

    private final PlayerSeasonStatsService service;

    public PlayerSeasonStatsController(PlayerSeasonStatsService service) {
        this.service = service;
    }

    // ---------------------
    // CRUD básico
    // ---------------------

    @GetMapping
    public List<PlayerSeasonStatsEntity> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public List<PlayerSeasonStatsEntity> getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public PlayerSeasonStatsEntity create(@RequestBody PlayerSeasonStatsEntity stats) {
        return service.save(stats);
    }

    @PutMapping("/{id}")
    public PlayerSeasonStatsEntity update(
            @PathVariable String id,
            @RequestBody PlayerSeasonStatsEntity stats
    ) {
        stats.setPlayer_id(id);
        return service.save(stats);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    // ---------------------
    // Filtros
    // ---------------------

    @GetMapping("/season/{season}")
    public List<PlayerSeasonStatsEntity> getBySeason(@PathVariable String season) {
        return service.getBySeason(season);
    }

    @GetMapping("/player/{playerId}/season/{season}")
    public List<PlayerSeasonStatsEntity> getByPlayerAndSeason(
            @PathVariable String playerId,
            @PathVariable String season
    ) {
        return service.getByPlayerAndSeason(playerId, season);
    }
}