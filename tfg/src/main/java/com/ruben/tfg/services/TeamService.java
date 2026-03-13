package com.ruben.tfg.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ruben.tfg.DTOs.SearchItemDTO;
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

    public TeamEntity getById(String id) {
        TeamEntity team = repo.findById(id).orElseThrow(() -> new RuntimeException("Team not found"));
        return team;
    }

    public void delete(String id) {
        TeamEntity team = repo.findById(id).orElseThrow(() -> new RuntimeException("Team not found"));
        repo.delete(team);
    }

	public TeamEntity update(TeamEntity team) {
		TeamEntity existingTeam = repo.findById(team.getId()).orElseThrow(() -> new RuntimeException("Team not found"));
		return repo.save(team);
	}

	public List<SearchItemDTO> search(String q) {
	    List<TeamEntity> lista = (q == null || q.isBlank())
	        ? repo.findAll()
	        : repo.findByNombreContainingIgnoreCase(q);
	    return lista.stream()
	        .map(t -> new SearchItemDTO(t.getId(), t.getNombre()))
	        .collect(Collectors.toList());
	}
}