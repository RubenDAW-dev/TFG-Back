package com.ruben.tfg.services;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.ruben.tfg.DTOs.PlayerDTO;
import com.ruben.tfg.entities.PlayerEntity;
import com.ruben.tfg.repositories.PlayerRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PlayerService {

    private final PlayerRepository repo;
    private ModelMapper mapper;

    public List<PlayerEntity> getAll() {
        return repo.findAll();
    }

    public PlayerEntity getById(String id) {
        PlayerEntity player = repo.findById(id).orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
        return player; 
    }

    public void delete(String id) {
    	PlayerEntity player = repo.findById(id).orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
        repo.delete(player);
    }

	public PlayerEntity crear(PlayerDTO player) {
		PlayerEntity entity = mapper.map(player, PlayerEntity.class);
		return repo.save(entity);
	}

	public PlayerEntity update(PlayerEntity player) {
		Optional<PlayerEntity> existingPlayerOpt = repo.findById(player.getId());
		if (existingPlayerOpt.isPresent()) {
			PlayerEntity existingPlayer = existingPlayerOpt.get();
			existingPlayer.setNombre(player.getNombre());
			existingPlayer.setPosicion(player.getPosicion());
			existingPlayer.setEdad(player.getEdad());
			existingPlayer.setNacionalidad(player.getNacionalidad());
			return repo.save(existingPlayer);
		} else {
			return null; // O lanzar una excepción si prefieres
		}
	}
}