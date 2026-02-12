package com.ruben.tfg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ruben.tfg.entities.PlayerEntity;

public interface PlayerRepository extends JpaRepository<PlayerEntity, String> {

}
