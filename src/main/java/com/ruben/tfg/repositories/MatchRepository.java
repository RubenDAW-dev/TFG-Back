package com.ruben.tfg.repositories;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ruben.tfg.entities.MatchEntity;

public interface MatchRepository extends JpaRepository<MatchEntity, Long> {


    List<MatchEntity> findByDateBeforeOrderByDateDesc(LocalDate date, Pageable pageable);

	
    // Búsquedas útiles
    List<MatchEntity> findByHomeTeamId(String homeTeamId);
    List<MatchEntity> findByAwayTeamId(String awayTeamId);
    List<MatchEntity> findByWk(Integer wk);
    List<MatchEntity> findByDate(LocalDate date);


    @Query("SELECT m FROM MatchEntity m WHERE m.date < :today ORDER BY m.date DESC")
    List<MatchEntity> findLastMatches(@Param("today") LocalDate today, Pageable pageable);

    @Query("SELECT m FROM MatchEntity m WHERE m.date >= :today ORDER BY m.date ASC")
    List<MatchEntity> findNextMatches(@Param("today") LocalDate today, Pageable pageable);


    List<MatchEntity> findByHomeTeamNombreContainingIgnoreCaseOrAwayTeamNombreContainingIgnoreCase(
    	    String homeNombre, String awayNombre);


	List<MatchEntity> findByHomeTeam_IdAndAwayTeam_IdAndWk(String homeTeamId, String awayTeamId, Integer wk);
    
}