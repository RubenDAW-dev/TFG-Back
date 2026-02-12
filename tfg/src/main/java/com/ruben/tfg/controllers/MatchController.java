package com.ruben.tfg.controllers;

import com.ruben.tfg.entities.MatchEntity;
import com.ruben.tfg.services.MatchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService service;

    public MatchController(MatchService service) {
        this.service = service;
    }

    @GetMapping
    public List<MatchEntity> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MatchEntity getById(@PathVariable Long id) {
        return service.getById(id).orElse(null);
    }

    @PostMapping
    public MatchEntity create(@RequestBody MatchEntity match) {
        return service.save(match);
    }

    @PutMapping("/{id}")
    public MatchEntity update(@PathVariable Long id, @RequestBody MatchEntity match) {
        match.setId(id);
        return service.save(match);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/week/{wk}")
    public List<MatchEntity> getByWeek(@PathVariable Integer wk) {
        return service.getByWeek(wk);
    }

    @GetMapping("/home/{teamId}")
    public List<MatchEntity> getByHomeTeam(@PathVariable String teamId) {
        return service.getByHomeTeam(teamId);
    }

    @GetMapping("/away/{teamId}")
    public List<MatchEntity> getByAwayTeam(@PathVariable String teamId) {
        return service.getByAwayTeam(teamId);
    }

    @GetMapping("/date/{date}")
    public List<MatchEntity> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.getByDate(date);
    }
}