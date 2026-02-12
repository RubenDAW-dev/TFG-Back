package com.ruben.tfg.controllers;

import com.ruben.tfg.entities.PlayerEntity;
import com.ruben.tfg.services.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService service;

    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @GetMapping
    public List<PlayerEntity> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public PlayerEntity getById(@PathVariable String id) {
        return service.getById(id).orElse(null);
    }

    @PostMapping
    public PlayerEntity create(@RequestBody PlayerEntity player) {
        return service.save(player);
    }

    @PutMapping("/{id}")
    public PlayerEntity update(
            @PathVariable String id,
            @RequestBody PlayerEntity player
    ) {
        player.setId(id);
        return service.save(player);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}