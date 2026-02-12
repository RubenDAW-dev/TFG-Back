package com.ruben.tfg.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ruben.tfg.entities.TeamEntity;
import com.ruben.tfg.repositories.TeamRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TeamService {

    private final TeamRepository repo;

    public List<TeamEntity> getAll() {
        return repo.findAll();
    }

    public Optional<TeamEntity> getById(String id) {
        return repo.findById(id);
    }

    public TeamEntity save(TeamEntity team) {
        return repo.save(team);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}