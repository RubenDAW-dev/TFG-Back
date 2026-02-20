package com.ruben.tfg.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.ruben.tfg.DTOs.MatchDTO;
import com.ruben.tfg.entities.MatchEntity;
import com.ruben.tfg.repositories.MatchRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class MatchService {

    private final MatchRepository repo;
    private ModelMapper modelMapper = new ModelMapper();

    public List<MatchEntity> getAll() {
        return repo.findAll();
    }

    public MatchEntity getById(Long id) {
        MatchEntity match = repo.findById(id).orElseThrow(() -> new RuntimeException("Match not found"));
        return match;
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


	public void update(MatchDTO match) {
		MatchEntity entity = repo.findById(match.getId()).orElseThrow(() -> new RuntimeException("Match not found"));
		modelMapper.map(match, entity);
		repo.save(entity);
	}
}
