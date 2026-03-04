package com.ruben.tfg.services;

import com.ruben.tfg.DTOs.PlayerSeasonStatsDTO;
import com.ruben.tfg.DTOs.RankingDTO;
import com.ruben.tfg.entities.PlayerSeasonStatsEntity;
import com.ruben.tfg.repositories.PlayerSeasonStatsRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerSeasonStatsService {

    private final PlayerSeasonStatsRepository repo;

    // ============================================================
    // CRUD
    // ============================================================

    public List<PlayerSeasonStatsEntity> getAll() {
        return repo.findAll();
    }

    /**
     * Como tu controller responde con List, y esta tabla tiene 1 registro por jugador,
     * convertimos Optional -> List automáticamente.
     */
    public List<PlayerSeasonStatsEntity> getById(String playerId) {
        Optional<PlayerSeasonStatsEntity> opt = repo.findById(playerId);
        return opt.<List<PlayerSeasonStatsEntity>>map(List::of)
                  .orElse(Collections.emptyList());
    }

    public PlayerSeasonStatsEntity save(PlayerSeasonStatsEntity stats) {
        return repo.save(stats);
    }

    public void delete(String playerId) {
        repo.deleteById(playerId);
    }

    // ============================================================
    // RANKINGS (Top Scorers / Top Assists)
    // ============================================================

    public List<RankingDTO> topScorers(String teamId, Pageable pageable) {
        return repo.topScorers(teamId, pageable);
    }

    public List<RankingDTO> topAssists(String teamId, Pageable pageable) {
        return repo.topAssists(teamId, pageable);
    }

    // ============================================================
    // TABLA AGREGADA POR EQUIPO
    // ============================================================

    /** Paginado (para tabla en Angular) */
    public Page<PlayerSeasonStatsDTO> aggregateByTeam(String teamId, Pageable pageable) {
        return repo.findAllByTeamIdAsDtoPaged(teamId, pageable);
    }

    /** Versión sin paginar (si quieres usarla en algún otro sitio) */
    public List<PlayerSeasonStatsDTO> getByTeamId(String teamId) {
        return repo.findAllByTeamIdAsDto(teamId);
    }

	public List<RankingDTO> AlltopScorers(Pageable pageable) {
		return repo.AlltopScorers(pageable);
	}

	public List<RankingDTO> AlltopAssists( Pageable pageable) {
		return repo.AlltopAssists(pageable);
	}
}