package com.ruben.tfg.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ruben.tfg.entities.PlayerMatchStatsEntity;

public interface PlayerMatchStatsRepository extends JpaRepository<PlayerMatchStatsEntity, Long> {

    List<PlayerMatchStatsEntity> findByMatchId(Long matchId);

    List<PlayerMatchStatsEntity> findByPlayerId(String playerId);

}
