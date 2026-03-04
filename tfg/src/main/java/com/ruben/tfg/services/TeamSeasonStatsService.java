package com.ruben.tfg.services;

import com.ruben.tfg.DTOs.TeamSummaryDTO;
import com.ruben.tfg.DTOs.TeamRadarDTO;
import com.ruben.tfg.DTOs.TeamTableRowDTO;
import com.ruben.tfg.entities.TeamSeasonStatsEntity;
import com.ruben.tfg.repositories.TeamSeasonStatsRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamSeasonStatsService {

    private final TeamSeasonStatsRepository repo;

    // ============================================================
    // CRUD BÁSICO
    // ============================================================

    public List<TeamSeasonStatsEntity> getAll() {
        return repo.findAll();
    }

    public Optional<TeamSeasonStatsEntity> getById(String id) {
        return repo.findById(id);
    }

    public TeamSeasonStatsEntity save(TeamSeasonStatsEntity e) {
        return repo.save(e);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }

    // ============================================================
    // MÉTODOS PARA DASHBOARD / FRONT
    // ============================================================

    /** Tarjeta resumen del equipo */
    public Optional<TeamSummaryDTO> summary(String teamId) {
        return repo.findById(teamId).map(ts -> new TeamSummaryDTO(
                ts.getTeamId(),
                ts.getTeam().getNombre(),
                ts.getPartidos(),
                ts.getGoles_favor(),
                ts.getGoles_contra(),
                ts.getVictorias(),
                ts.getEmpates(),
                ts.getDerrotas(),
                ts.getPuntos()
        ));
    }

    /** Radar de métricas medias */
    public Optional<TeamRadarDTO> radar(String teamId) {
        return repo.findById(teamId).map(ts -> new TeamRadarDTO(
                ts.getPosesion_media(),
                ts.getTiros_media(),
                ts.getTiros_puerta_media(),
                ts.getParadas_media(),
                ts.getTarjetas_media()
        ));
    }

    /** Clasificación completa ordenada */
    public List<TeamTableRowDTO> table() {
        List<TeamSeasonStatsEntity> lista = repo.findAll();

        return lista.stream()
                .map(ts -> new TeamTableRowDTO(
                        ts.getTeamId(),
                        ts.getTeam().getNombre(),
                        ts.getPartidos(),
                        ts.getGoles_favor(),
                        ts.getGoles_contra(),
                        ts.getGoles_favor() - ts.getGoles_contra(),
                        ts.getVictorias(),
                        ts.getEmpates(),
                        ts.getDerrotas(),
                        ts.getPuntos()
                ))
                .sorted(Comparator
                        .comparing(TeamTableRowDTO::puntos).reversed()
                        .thenComparing(TeamTableRowDTO::diferenciaGoles).reversed()
                        .thenComparing(TeamTableRowDTO::golesFavor).reversed()
                        .thenComparing(TeamTableRowDTO::victorias).reversed()
                )
                .toList();
    }
}