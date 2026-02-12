package com.ruben.tfg.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ruben.tfg.entities.MatchEntity;
import com.ruben.tfg.repositories.MatchRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class MatchService {

    private final MatchRepository repo;

    public List<MatchEntity> getAll() {
        return repo.findAll();
    }

    public Optional<MatchEntity> getById(Long id) {
        return repo.findById(id);
    }

    public List<MatchEntity> getByHomeTeam(String teamId) {
        return repo.findByHomeTeamId(teamId);
    }

    public List<MatchEntity> getByAwayTeam(String teamId) {
        return repo.findByAwayTeamId(teamId);
    }

    public List<MatchEntity> getByWeek(Integer wk) {
        return repo.findByWk(wk);
    }

    public List<MatchEntity> getByDate(LocalDate date) {
        return repo.findByDate(date);
    }

    public MatchEntity save(MatchEntity match) {
        return repo.save(match);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
