package com.ruben.tfg.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ruben.tfg.entities.PlayerMatchStatsEntity;
import com.ruben.tfg.repositories.PlayerMatchStatsRepository;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class PlayerMatchStatsService {

    private final PlayerMatchStatsRepository repo;

    public List<PlayerMatchStatsEntity> getAll() {
        return repo.findAll();
    }

    public Optional<PlayerMatchStatsEntity> getById(Long id) {
        return repo.findById(id);
    }

    public List<PlayerMatchStatsEntity> getByMatch(Long matchId) {
        return repo.findByMatchId(matchId);
    }

    public List<PlayerMatchStatsEntity> getByPlayer(String playerId) {
        return repo.findByPlayerId(playerId);
    }

    public PlayerMatchStatsEntity save(PlayerMatchStatsEntity stats) {
        return repo.save(stats);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
