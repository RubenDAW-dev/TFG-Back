package com.ruben.tfg.repositories;

import com.ruben.tfg.entities.PlayerSeasonStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerSeasonStatsRepository extends JpaRepository<PlayerSeasonStatsEntity, String> {

    List<PlayerSeasonStatsEntity> findByPlayerId(String player_id);

    List<PlayerSeasonStatsEntity> findBySeason(String season);

    List<PlayerSeasonStatsEntity> findByPlayerIdAndSeason(String player_id, String season);
}