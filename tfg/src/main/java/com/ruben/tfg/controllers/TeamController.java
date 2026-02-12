package com.ruben.tfg.controllers;

import com.ruben.tfg.entities.TeamEntity;
import com.ruben.tfg.services.TeamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService service;

    public TeamController(TeamService service) {
        this.service = service;
    }

    @GetMapping
    public List<TeamEntity> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TeamEntity getById(@PathVariable String id) {
        return service.getById(id).orElse(null);
    }

    @PostMapping
    public TeamEntity create(@RequestBody TeamEntity team) {
        return service.save(team);
    }

    @PutMapping("/{id}")
    public TeamEntity update(
            @PathVariable String id,
            @RequestBody TeamEntity team) {

        team.setId(id);
        return service.save(team);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}