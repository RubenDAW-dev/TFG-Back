package com.ruben.tfg.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ruben.tfg.entities.TeamSeasonStatsEntity;
import com.ruben.tfg.repositories.TeamSeasonStatsRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TeamSeasonStatsService {
	
	private final TeamSeasonStatsRepository repository;

    public Optional<TeamSeasonStatsEntity> getById(String id) {
        return repository.findById(id);
    }

    public List<TeamSeasonStatsEntity> getAll() {
        return repository.findAll();
    }

    public TeamSeasonStatsEntity save(TeamSeasonStatsEntity entity) {
        return repository.save(entity);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}
