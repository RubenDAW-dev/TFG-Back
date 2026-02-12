package com.ruben.tfg.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ruben.tfg.entities.PlayerEntity;
import com.ruben.tfg.repositories.PlayerRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PlayerService {

    private final PlayerRepository repo;

    public List<PlayerEntity> getAll() {
        return repo.findAll();
    }

    public Optional<PlayerEntity> getById(String id) {
        return repo.findById(id);
    }

    public PlayerEntity save(PlayerEntity player) {
        return repo.save(player);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}