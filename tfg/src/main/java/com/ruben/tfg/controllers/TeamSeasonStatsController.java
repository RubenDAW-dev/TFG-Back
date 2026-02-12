package com.ruben.tfg.controllers;


import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruben.tfg.entities.TeamSeasonStatsEntity;
import com.ruben.tfg.services.TeamSeasonStatsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/team-season-stats")
@RequiredArgsConstructor
public class TeamSeasonStatsController {

    private final TeamSeasonStatsService service;

    @GetMapping("/{id}")
    public TeamSeasonStatsEntity getById(@PathVariable String id) {
        return service.getById(id)
                .orElseThrow(() -> 
                        new RuntimeException("TeamSeasonStats not found with id: " + id));
    }

    @GetMapping
    public List<TeamSeasonStatsEntity> getAll() {
        return service.getAll();
    }

    @PostMapping
    public TeamSeasonStatsEntity create(@RequestBody TeamSeasonStatsEntity stats) {
        return service.save(stats);
    }

    @PutMapping("/{id}")
    public TeamSeasonStatsEntity update(
            @PathVariable String id,
            @RequestBody TeamSeasonStatsEntity stats) {

        stats.setTeam_id(id);
        return service.save(stats);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
