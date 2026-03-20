package com.ruben.tfg.services;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ruben.tfg.DTOs.TeamPlayerAggDTO;
import com.ruben.tfg.DTOs.TeamRadarDTO;
import com.ruben.tfg.DTOs.TeamStatsRowDTO;
import com.ruben.tfg.DTOs.TeamSummaryDTO;
import com.ruben.tfg.DTOs.TeamTableRowDTO;
import com.ruben.tfg.entities.TeamSeasonStatsEntity;
import com.ruben.tfg.repositories.PlayerSeasonStatsRepository;
import com.ruben.tfg.repositories.TeamSeasonStatsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamSeasonStatsService {

    private final TeamSeasonStatsRepository repo;
    private final PlayerSeasonStatsRepository playerStatsRepo;

    // ============================================================
    // CRUD BÁSICO
    // ============================================================

    public List<TeamSeasonStatsEntity> getAll() {
        return repo.findAll();
    }

    public TeamStatsRowDTO getStatsByTeamId(String teamId) {
        TeamSeasonStatsEntity ts = repo.findById(teamId)
                .orElseThrow(() -> new RuntimeException("No existe equipo con ID: " + teamId));

        Map<String, TeamPlayerAggDTO> aggMap = buildAggMap();
        TeamPlayerAggDTO agg = aggMap.get(teamId);

        return buildRow(ts, agg);
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

    public List<TeamStatsRowDTO> statsTable() {
        List<TeamSeasonStatsEntity> stats = repo.findAll();
        Map<String, TeamPlayerAggDTO> aggMap = buildAggMap();

        return stats.stream()
                .map(ts -> buildRow(ts, aggMap.get(ts.getTeamId())))
                .toList();
    }

    private Map<String, TeamPlayerAggDTO> buildAggMap() {
        return playerStatsRepo.aggregateByTeam()
                .stream()
                .collect(Collectors.toMap(TeamPlayerAggDTO::teamId, a -> a));
    }

    private TeamStatsRowDTO buildRow(TeamSeasonStatsEntity ts, TeamPlayerAggDTO agg) {
        TeamStatsRowDTO dto = new TeamStatsRowDTO();

        fillBaseStats(dto, ts);
        fillAggregatedStats(dto, agg);
        calculateDerivedMetrics(dto, ts);

        return dto;
    }

    private void fillBaseStats(TeamStatsRowDTO dto, TeamSeasonStatsEntity ts) {
        dto.setTeamId(ts.getTeamId());
        dto.setTeamName(ts.getTeam().getNombre());
        dto.setPartidos(ts.getPartidos());
        dto.setGolesFavor(ts.getGoles_favor());
        dto.setGolesContra(ts.getGoles_contra());
        dto.setDiferenciaGoles(ts.getGoles_favor() - ts.getGoles_contra());
        dto.setVictorias(ts.getVictorias());
        dto.setEmpates(ts.getEmpates());
        dto.setDerrotas(ts.getDerrotas());
        dto.setPuntos(ts.getPuntos());
        dto.setPosesionMedia(ts.getPosesion_media());
        dto.setTirosMedia(ts.getTiros_media());
        dto.setTirosPuertaMedia(ts.getTiros_puerta_media());
        dto.setParadasMedia(ts.getParadas_media());
        dto.setTarjetasMedia(ts.getTarjetas_media());
    }

    private void fillAggregatedStats(TeamStatsRowDTO dto, TeamPlayerAggDTO agg) {

        if (agg == null) {
            dto.setGolesEquipo(0);
            dto.setAsistenciasEquipo(0);
            dto.setDisparosEquipo(0);
            dto.setDisparosPuertaEquipo(0);
            dto.setAmarillasEquipo(0);
            dto.setRojasEquipo(0);
            dto.setFaltasCometidasEquipo(0);
            dto.setCentrosEquipo(0);
            dto.setEntradasGanadasEquipo(0);
            dto.setIntercepcionesEquipo(0);
            dto.setAutogolesEquipo(0);
            dto.setPrecisionTiroMedia(0.0);
            dto.setConversionPenaltiMedia(0.0);
            return;
        }

        dto.setGolesEquipo(nzInt(agg.goles()));
        dto.setAsistenciasEquipo(nzInt(agg.asistencias()));
        dto.setDisparosEquipo(nzInt(agg.disparos()));
        dto.setDisparosPuertaEquipo(nzInt(agg.disparosPuerta()));
        dto.setAmarillasEquipo(nzInt(agg.amarillas()));
        dto.setRojasEquipo(nzInt(agg.rojas()));
        dto.setFaltasCometidasEquipo(nzInt(agg.faltasCometidas()));
        dto.setCentrosEquipo(nzInt(agg.centros()));
        dto.setEntradasGanadasEquipo(nzInt(agg.entradasGanadas()));
        dto.setIntercepcionesEquipo(nzInt(agg.intercepciones()));
        dto.setAutogolesEquipo(nzInt(agg.autogoles()));

        dto.setPrecisionTiroMedia(
                agg.precisionTiroMedia() != null ? agg.precisionTiroMedia() : 0.0
        );

        dto.setConversionPenaltiMedia(
                agg.conversionPenaltiMedia() != null ? agg.conversionPenaltiMedia() : 0.0
        );
    }

    private int nzInt(Number n) {
        return n == null ? 0 : n.intValue();
    }

    private void calculateDerivedMetrics(TeamStatsRowDTO dto, TeamSeasonStatsEntity ts) {

        int partidos = ts.getPartidos() != null ? ts.getPartidos() : 0;

        double golesPorPartido =
                partidos > 0 ? (double) dto.getGolesEquipo() / partidos : 0.0;

        double golesContraPorPartido =
                partidos > 0 ? (double) ts.getGoles_contra() / partidos : 0.0;

        dto.setGolesPorPartido(golesPorPartido);
        dto.setGolesContraPorPartido(golesContraPorPartido);
    }

}