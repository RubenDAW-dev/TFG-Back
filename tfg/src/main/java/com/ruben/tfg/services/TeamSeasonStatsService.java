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
    public List<TeamStatsRowDTO> statsTable() {
        List<TeamSeasonStatsEntity> stats = repo.findAll();
        List<TeamPlayerAggDTO> playerAgg = playerStatsRepo.aggregateByTeam();

        Map<String, TeamPlayerAggDTO> aggMap = playerAgg.stream()
            .collect(Collectors.toMap(TeamPlayerAggDTO::teamId, a -> a));

        return stats.stream().map(ts -> {
            TeamStatsRowDTO dto = new TeamStatsRowDTO();
            TeamPlayerAggDTO agg = aggMap.get(ts.getTeamId());

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

            if (agg != null) {
                dto.setGolesEquipo(agg.goles() != null ? agg.goles().intValue() : 0);
                dto.setAsistenciasEquipo(agg.asistencias() != null ? agg.asistencias().intValue() : 0);
                dto.setDisparosEquipo(agg.disparos() != null ? agg.disparos().intValue() : 0);
                dto.setDisparosPuertaEquipo(agg.disparosPuerta() != null ? agg.disparosPuerta().intValue() : 0);
                dto.setAmarillasEquipo(agg.amarillas() != null ? agg.amarillas().intValue() : 0);
                dto.setRojasEquipo(agg.rojas() != null ? agg.rojas().intValue() : 0);
                dto.setFaltasCometidasEquipo(agg.faltasCometidas() != null ? agg.faltasCometidas().intValue() : 0);
                dto.setCentrosEquipo(agg.centros() != null ? agg.centros().intValue() : 0);
                dto.setEntradasGanadasEquipo(agg.entradasGanadas() != null ? agg.entradasGanadas().intValue() : 0);
                dto.setIntercepcionesEquipo(agg.intercepciones() != null ? agg.intercepciones().intValue() : 0);
                dto.setAutogolesEquipo(agg.autogoles() != null ? agg.autogoles().intValue() : 0);
                dto.setPrecisionTiroMedia(agg.precisionTiroMedia() != null ? agg.precisionTiroMedia() : 0.0);
                dto.setConversionPenaltiMedia(agg.conversionPenaltiMedia() != null ? agg.conversionPenaltiMedia() : 0.0);
            } else {
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
            }

            double golesPorPartido = ts.getPartidos() != null && ts.getPartidos() > 0
                ? (double) dto.getGolesEquipo() / ts.getPartidos()
                : 0.0;
            dto.setGolesPorPartido(golesPorPartido);

            double golesContraPorPartido = ts.getPartidos() != null && ts.getPartidos() > 0
                ? (double) ts.getGoles_contra() / ts.getPartidos()
                : 0.0;
            dto.setGolesContraPorPartido(golesContraPorPartido);

            return dto;
        }).toList();
    }
}