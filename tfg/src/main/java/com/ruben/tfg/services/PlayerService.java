package com.ruben.tfg.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ruben.tfg.DTOs.PlayerDTO;
import com.ruben.tfg.DTOs.SearchItemDTO;
import com.ruben.tfg.DTOs.UpdatePlayerDTO;
import com.ruben.tfg.entities.PlayerEntity;
import com.ruben.tfg.entities.TeamEntity;
import com.ruben.tfg.repositories.PlayerRepository;
import com.ruben.tfg.repositories.TeamRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PlayerService {

	private final PlayerRepository repo;
	private ModelMapper mapper;
	private final TeamRepository teamrepo;

	public List<PlayerEntity> getAll() {
		return repo.findAll();
	}

	public PlayerEntity getById(String id) {
		PlayerEntity player = repo.findById(id)
				.orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
		return player;
	}

	public void delete(String id) {
		PlayerEntity player = repo.findById(id)
				.orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
		repo.delete(player);
	}

	public PlayerEntity crear(PlayerDTO player) {
		PlayerEntity entity = mapper.map(player, PlayerEntity.class);
		return repo.save(entity);
	}

	public PlayerEntity update(String id, UpdatePlayerDTO dto) {
		PlayerEntity entity = repo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jugador no encontrado"));

		if (dto.getNombre() != null)
			entity.setNombre(dto.getNombre());
		if (dto.getPosicion() != null)
			entity.setPosicion(dto.getPosicion());
		if (dto.getEdad() != null)
			entity.setEdad(dto.getEdad());
		if (dto.getNacionalidad() != null)
			entity.setNacionalidad(dto.getNacionalidad());
		if (dto.getImageUrl() != null)
			entity.setImageUrl(dto.getImageUrl());

		if (dto.getTeamId() != null) {
			if (dto.getTeamId().isBlank()) {
				entity.setTeam(null); // quitar equipo
			} else {
				TeamEntity team = teamrepo.findById(dto.getTeamId())
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipo no encontrado"));
				entity.setTeam(team);
			}
		}

		return repo.save(entity);
	}

	public List<SearchItemDTO> search(String q) {
		List<PlayerEntity> lista = (q == null || q.isBlank()) ? repo.findAll()
				: repo.findByNombreContainingIgnoreCase(q);
		return lista.stream().map(p -> new SearchItemDTO(p.getId(), p.getNombre())).collect(Collectors.toList());
	}
}