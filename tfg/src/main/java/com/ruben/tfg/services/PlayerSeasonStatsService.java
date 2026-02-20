package com.ruben.tfg.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ruben.tfg.entities.PlayerSeasonStatsEntity;
import com.ruben.tfg.repositories.PlayerSeasonStatsRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PlayerSeasonStatsService {

    private final PlayerSeasonStatsRepository repo;

    public List<PlayerSeasonStatsEntity> getAll() {
        return repo.findAll();
    }

    public List<PlayerSeasonStatsEntity> getById(String playerId) {
        return repo.findByPlayerId(playerId);
    }

    public PlayerSeasonStatsEntity save(PlayerSeasonStatsEntity stats) {
        return repo.save(stats);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}