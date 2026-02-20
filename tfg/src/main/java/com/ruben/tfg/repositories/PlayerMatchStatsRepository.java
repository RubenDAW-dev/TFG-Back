package com.ruben.tfg.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ruben.tfg.entities.PlayerMatchStatsEntity;

public interface PlayerMatchStatsRepository extends JpaRepository<PlayerMatchStatsEntity, Long> {

    List<PlayerMatchStatsEntity> findByMatchId(Long matchId);

    List<PlayerMatchStatsEntity> findByPlayerId(String playerId);

	Optional<PlayerMatchStatsEntity> findByMatch_IdAndPlayer_Id(Long matchId, String playerId);

	List<PlayerMatchStatsEntity> findByMatch_Id(Long matchId);

}
