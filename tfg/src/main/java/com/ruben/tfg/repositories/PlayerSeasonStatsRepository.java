package com.ruben.tfg.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ruben.tfg.DTOs.PlayerSeasonStatsDTO;
import com.ruben.tfg.DTOs.PlayerStatsTableDTO;
import com.ruben.tfg.DTOs.RankingDTO;
import com.ruben.tfg.entities.PlayerSeasonStatsEntity;

public interface PlayerSeasonStatsRepository extends JpaRepository<PlayerSeasonStatsEntity, String> {

    List<PlayerSeasonStatsEntity> findByPlayerId(String player_id);

    // === RANKINGS (mantienen Pageable) ===
    @Query("""
                select new com.ruben.tfg.DTOs.RankingDTO(
                    pss.playerId, p.nombre, p.team.id,
                    pss.goles, pss.minutos, pss.golesPor90
                )
                from PlayerSeasonStatsEntity pss
                join pss.player p
                where (:teamId is null or p.team.id = :teamId)
                order by pss.goles desc, pss.golesPor90 desc
            """)
    List<RankingDTO> topScorers(@Param("teamId") String teamId, Pageable pageable);

    @Query("""
                select new com.ruben.tfg.DTOs.RankingDTO(
                    pss.playerId, p.nombre, p.team.id,
                    pss.asistencias, pss.minutos, pss.asistenciasPor90
                )
                from PlayerSeasonStatsEntity pss
                join pss.player p
                where (:teamId is null or p.team.id = :teamId)
                order by pss.asistencias desc, pss.asistenciasPor90 desc
            """)
    List<RankingDTO> topAssists(@Param("teamId") String teamId, Pageable pageable);

    @Query("""
                select new com.ruben.tfg.DTOs.RankingDTO(
                    pss.playerId, p.nombre, p.team.id,
                    pss.goles, pss.minutos, pss.golesPor90
                )
                from PlayerSeasonStatsEntity pss
                join pss.player p
                order by pss.goles desc, pss.golesPor90 desc
            """)
    List<RankingDTO> AlltopScorers(Pageable pageable);

    @Query("""
                select new com.ruben.tfg.DTOs.RankingDTO(
                    pss.playerId, p.nombre, p.team.id,
                    pss.asistencias, pss.minutos, pss.asistenciasPor90
                )
                from PlayerSeasonStatsEntity pss
                join pss.player p
                order by pss.asistencias desc, pss.asistenciasPor90 desc
            """)
    List<RankingDTO> AlltopAssists(Pageable pageable);

    @Query(value = """
            select new com.ruben.tfg.DTOs.PlayerStatsTableDTO(
                pss.playerId, p.nombre, p.team.nombre,
                pss.partidos, pss.minutos, pss.goles, pss.asistencias,
                pss.penaltisMarcados, pss.penaltisIntentados, pss.disparos, pss.disparosPuerta,
                pss.amarillas, pss.rojas, pss.faltasCometidas, pss.faltasRecibidas,
                pss.fueraDeJuego, pss.centros, pss.entradasGanadas, pss.intercepciones, pss.autogoles,
                pss.golesPor90, pss.asistenciasPor90, pss.disparosPor90, pss.disparosPuertaPor90,
                pss.amarillasPor90, pss.rojasPor90, pss.faltasCometidasPor90, pss.faltasRecibidasPor90,
                pss.fueraDeJuegoPor90, pss.centrosPor90, pss.entradasGanadasPor90,
                pss.intercepcionesPor90, pss.precisionTiro, pss.conversionPenalti
            )
            from PlayerSeasonStatsEntity pss
            join pss.player p
        """)
List<PlayerStatsTableDTO> findAllAsTableDto(Sort sort);

@Query(value = """
            select new com.ruben.tfg.DTOs.PlayerStatsTableDTO(
                pss.playerId, p.nombre, p.team.nombre,
                pss.partidos, pss.minutos, pss.goles, pss.asistencias,
                pss.penaltisMarcados, pss.penaltisIntentados, pss.disparos, pss.disparosPuerta,
                pss.amarillas, pss.rojas, pss.faltasCometidas, pss.faltasRecibidas,
                pss.fueraDeJuego, pss.centros, pss.entradasGanadas, pss.intercepciones, pss.autogoles,
                pss.golesPor90, pss.asistenciasPor90, pss.disparosPor90, pss.disparosPuertaPor90,
                pss.amarillasPor90, pss.rojasPor90, pss.faltasCometidasPor90, pss.faltasRecibidasPor90,
                pss.fueraDeJuegoPor90, pss.centrosPor90, pss.entradasGanadasPor90,
                pss.intercepcionesPor90, pss.precisionTiro, pss.conversionPenalti
            )
            from PlayerSeasonStatsEntity pss
            join pss.player p
            order by p.nombre asc
        """)
List<PlayerStatsTableDTO> findAllAsTableDtoOrderByPlayerNameAsc();

@Query(value = """
            select new com.ruben.tfg.DTOs.PlayerStatsTableDTO(
                pss.playerId, p.nombre, p.team.nombre,
                pss.partidos, pss.minutos, pss.goles, pss.asistencias,
                pss.penaltisMarcados, pss.penaltisIntentados, pss.disparos, pss.disparosPuerta,
                pss.amarillas, pss.rojas, pss.faltasCometidas, pss.faltasRecibidas,
                pss.fueraDeJuego, pss.centros, pss.entradasGanadas, pss.intercepciones, pss.autogoles,
                pss.golesPor90, pss.asistenciasPor90, pss.disparosPor90, pss.disparosPuertaPor90,
                pss.amarillasPor90, pss.rojasPor90, pss.faltasCometidasPor90, pss.faltasRecibidasPor90,
                pss.fueraDeJuegoPor90, pss.centrosPor90, pss.entradasGanadasPor90,
                pss.intercepcionesPor90, pss.precisionTiro, pss.conversionPenalti
            )
            from PlayerSeasonStatsEntity pss
            join pss.player p
            order by p.nombre desc
        """)
List<PlayerStatsTableDTO> findAllAsTableDtoOrderByPlayerNameDesc();

@Query(value = """
            select new com.ruben.tfg.DTOs.PlayerStatsTableDTO(
                pss.playerId, p.nombre, p.team.nombre,
                pss.partidos, pss.minutos, pss.goles, pss.asistencias,
                pss.penaltisMarcados, pss.penaltisIntentados, pss.disparos, pss.disparosPuerta,
                pss.amarillas, pss.rojas, pss.faltasCometidas, pss.faltasRecibidas,
                pss.fueraDeJuego, pss.centros, pss.entradasGanadas, pss.intercepciones, pss.autogoles,
                pss.golesPor90, pss.asistenciasPor90, pss.disparosPor90, pss.disparosPuertaPor90,
                pss.amarillasPor90, pss.rojasPor90, pss.faltasCometidasPor90, pss.faltasRecibidasPor90,
                pss.fueraDeJuegoPor90, pss.centrosPor90, pss.entradasGanadasPor90,
                pss.intercepcionesPor90, pss.precisionTiro, pss.conversionPenalti
            )
            from PlayerSeasonStatsEntity pss
            join pss.player p
            order by p.team.nombre asc
        """)
List<PlayerStatsTableDTO> findAllAsTableDtoOrderByTeamNameAsc();

@Query(value = """
            select new com.ruben.tfg.DTOs.PlayerStatsTableDTO(
                pss.playerId, p.nombre, p.team.nombre,
                pss.partidos, pss.minutos, pss.goles, pss.asistencias,
                pss.penaltisMarcados, pss.penaltisIntentados, pss.disparos, pss.disparosPuerta,
                pss.amarillas, pss.rojas, pss.faltasCometidas, pss.faltasRecibidas,
                pss.fueraDeJuego, pss.centros, pss.entradasGanadas, pss.intercepciones, pss.autogoles,
                pss.golesPor90, pss.asistenciasPor90, pss.disparosPor90, pss.disparosPuertaPor90,
                pss.amarillasPor90, pss.rojasPor90, pss.faltasCometidasPor90, pss.faltasRecibidasPor90,
                pss.fueraDeJuegoPor90, pss.centrosPor90, pss.entradasGanadasPor90,
                pss.intercepcionesPor90, pss.precisionTiro, pss.conversionPenalti
            )
            from PlayerSeasonStatsEntity pss
            join pss.player p
            order by p.team.nombre desc
        """)
List<PlayerStatsTableDTO> findAllAsTableDtoOrderByTeamNameDesc();
@Query("""
        select new com.ruben.tfg.DTOs.PlayerStatsTableDTO(
            pss.playerId, p.nombre, p.team.nombre,
            pss.partidos, pss.minutos, pss.goles, pss.asistencias,
            pss.penaltisMarcados, pss.penaltisIntentados, pss.disparos, pss.disparosPuerta,
            pss.amarillas, pss.rojas, pss.faltasCometidas, pss.faltasRecibidas,
            pss.fueraDeJuego, pss.centros, pss.entradasGanadas, pss.intercepciones, pss.autogoles,
            pss.golesPor90, pss.asistenciasPor90, pss.disparosPor90, pss.disparosPuertaPor90,
            pss.amarillasPor90, pss.rojasPor90, pss.faltasCometidasPor90, pss.faltasRecibidasPor90,
            pss.fueraDeJuegoPor90, pss.centrosPor90, pss.entradasGanadasPor90,
            pss.intercepcionesPor90, pss.precisionTiro, pss.conversionPenalti
        )
        from PlayerSeasonStatsEntity pss
        join pss.player p
        where p.team.id = :teamId
    """)
List<PlayerStatsTableDTO> findAllByTeamIdAsDto(@Param("teamId") String teamId);
   }