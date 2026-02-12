package com.ruben.tfg.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ruben.tfg.entities.TeamMatchStatsEntity;
import com.ruben.tfg.repositories.TeamMatchStatsRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TeamMatchStatsService {

    private final TeamMatchStatsRepository repo;

    public List<TeamMatchStatsEntity> getAll() {
        return repo.findAll();
    }

    public Optional<TeamMatchStatsEntity> getById(Long id) {
        return repo.findById(id);
    }

    public List<TeamMatchStatsEntity> getByMatch(String matchId) {
        return repo.findByMatchId(matchId);
    }

    public List<TeamMatchStatsEntity> getByTeam(String teamId) {
        return repo.findByTeamId(teamId);
    }

    public List<TeamMatchStatsEntity> getBySide(String side) {
        return repo.findBySide(side);
    }

    public List<TeamMatchStatsEntity> getByMatchAndSide(String matchId, String side) {
        return repo.findByMatchIdAndSide(matchId, side);
    }

    public TeamMatchStatsEntity save(TeamMatchStatsEntity stats) {
        return repo.save(stats);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}