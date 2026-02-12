package com.ruben.tfg.controllers;

import com.ruben.tfg.entities.TeamMatchStatsEntity;
import com.ruben.tfg.services.TeamMatchStatsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team-match-stats")
public class TeamMatchStatsController {

    private final TeamMatchStatsService service;

    public TeamMatchStatsController(TeamMatchStatsService service) {
        this.service = service;
    }

    @GetMapping
    public List<TeamMatchStatsEntity> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TeamMatchStatsEntity getById(@PathVariable Long id) {
        return service.getById(id).orElse(null);
    }

    @PostMapping
    public TeamMatchStatsEntity create(@RequestBody TeamMatchStatsEntity stats) {
        return service.save(stats);
    }

    @PutMapping("/{id}")
    public TeamMatchStatsEntity update(
            @PathVariable Long id,
            @RequestBody TeamMatchStatsEntity stats
    ) {
        stats.setId(id);
        return service.save(stats);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }



    @GetMapping("/match/{matchId}")
    public List<TeamMatchStatsEntity> getByMatch(@PathVariable String matchId) {
        return service.getByMatch(matchId);
    }

    @GetMapping("/team/{teamId}")
    public List<TeamMatchStatsEntity> getByTeam(@PathVariable String teamId) {
        return service.getByTeam(teamId);
    }

    @GetMapping("/side/{side}")
    public List<TeamMatchStatsEntity> getBySide(@PathVariable String side) {
        return service.getBySide(side);
    }

    @GetMapping("/match/{matchId}/side/{side}")
    public List<TeamMatchStatsEntity> getByMatchAndSide(
            @PathVariable String matchId,
            @PathVariable String side
    ) {
        return service.getByMatchAndSide(matchId, side);
    }
}