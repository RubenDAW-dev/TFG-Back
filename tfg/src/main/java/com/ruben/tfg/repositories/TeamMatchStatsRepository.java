package com.ruben.tfg.repositories;

import com.ruben.tfg.entities.TeamMatchStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMatchStatsRepository extends JpaRepository<TeamMatchStatsEntity, Long> {

    List<TeamMatchStatsEntity> findByMatchId(String match_id);

    List<TeamMatchStatsEntity> findByTeamId(String team_id);

    List<TeamMatchStatsEntity> findBySide(String side);

    List<TeamMatchStatsEntity> findByMatchIdAndSide(String match_id, String side);
}