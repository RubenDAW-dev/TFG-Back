package com.ruben.tfg.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ruben.tfg.entities.PlayerEntity;

public interface PlayerRepository extends JpaRepository<PlayerEntity, String> {

	List<PlayerEntity> findByNombreContainingIgnoreCase(String nombre);


}
