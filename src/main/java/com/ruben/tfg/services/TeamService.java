package com.ruben.tfg.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ruben.tfg.DTOs.SearchItemDTO;
import com.ruben.tfg.DTOs.UpdateTeamDTO;
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

	public TeamEntity update(String id, UpdateTeamDTO dto) {

		TeamEntity entity = repo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipo no encontrado"));

		if (dto.getNombre() != null)
			entity.setNombre(dto.getNombre());
		if (dto.getCiudad() != null)
			entity.setCiudad(dto.getCiudad());
		if (dto.getEstadio() != null)
			entity.setEstadio(dto.getEstadio());
		if (dto.getCapacidad() != null)
			entity.setCapacidad(dto.getCapacidad());
		if (dto.getEscudo() != null)
			entity.setEscudo(dto.getEscudo());

		return repo.save(entity);
	}

	public List<SearchItemDTO> search(String q) {
		List<TeamEntity> lista = (q == null || q.isBlank()) ? repo.findAll() : repo.findByNombreContainingIgnoreCase(q);
		return lista.stream().map(t -> new SearchItemDTO(t.getId(), t.getNombre())).collect(Collectors.toList());
	}
}