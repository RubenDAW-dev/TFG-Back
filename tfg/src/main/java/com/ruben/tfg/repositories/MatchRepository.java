package com.ruben.tfg.repositories;

import com.ruben.tfg.entities.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MatchRepository extends JpaRepository<MatchEntity, Long> {

    // Búsquedas útiles
    List<MatchEntity> findByHomeTeamId(String homeTeamId);
    List<MatchEntity> findByAwayTeamId(String awayTeamId);
    List<MatchEntity> findByWk(Integer wk);
    List<MatchEntity> findByDate(LocalDate date);
}