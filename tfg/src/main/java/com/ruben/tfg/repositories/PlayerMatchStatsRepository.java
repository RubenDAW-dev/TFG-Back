package com.ruben.tfg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ruben.tfg.entities.PlayerMatchStatsEntity;

public interface PlayerMatchStatsRepository extends JpaRepository<PlayerMatchStatsEntity, Long> {

}
