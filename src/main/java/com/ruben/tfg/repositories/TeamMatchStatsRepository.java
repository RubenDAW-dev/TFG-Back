package com.ruben.tfg.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ruben.tfg.entities.TeamMatchStatsEntity;

@Repository
public interface TeamMatchStatsRepository extends JpaRepository<TeamMatchStatsEntity, Long> {

    List<TeamMatchStatsEntity> findByMatch_Id(Long matchId);

    List<TeamMatchStatsEntity> findByMatch_IdAndSide(Long matchId, String side);

    List<TeamMatchStatsEntity> findByTeam_Id(String teamId);

	Optional<TeamMatchStatsEntity> findByMatch_IdAndTeam_Id(Long matchId, String teamId);
}