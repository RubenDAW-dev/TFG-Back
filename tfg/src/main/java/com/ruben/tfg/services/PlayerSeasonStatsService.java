package com.ruben.tfg.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ruben.tfg.DTOs.PlayerSeasonStatsDTO;
import com.ruben.tfg.DTOs.PlayerStatsTableDTO;
import com.ruben.tfg.DTOs.RankingDTO;
import com.ruben.tfg.entities.PlayerSeasonStatsEntity;
import com.ruben.tfg.repositories.PlayerRepository;
import com.ruben.tfg.repositories.PlayerSeasonStatsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerSeasonStatsService {

    private final PlayerSeasonStatsRepository repo;
    private final PlayerRepository __playerRepo__;

    public List<PlayerSeasonStatsEntity> getAll() {
        return repo.findAll();
    }

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

    // === RANKINGS (mantienen Pageable para el límite) ===
    public List<RankingDTO> topScorers(String teamId, Pageable pageable) {
        return repo.topScorers(teamId, pageable);
    }

    public List<RankingDTO> topAssists(String teamId, Pageable pageable) {
        return repo.topAssists(teamId, pageable);
    }

    public List<RankingDTO> AlltopScorers(Pageable pageable) {
        return repo.AlltopScorers(pageable);
    }

    public List<RankingDTO> AlltopAssists(Pageable pageable) {
        return repo.AlltopAssists(pageable);
    }

    // === SIN PAGINACIÓN ===
    public List<PlayerSeasonStatsDTO> aggregateByTeam(String teamId) {
        return repo.findAllByTeamIdAsDto(teamId);
    }

    public List<PlayerSeasonStatsDTO> getByTeamId(String teamId) {
        return repo.findAllByTeamIdAsDto(teamId);
    }

    public List<PlayerStatsTableDTO> getAllWithNamesSorted(String sortField, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        return switch (sortField) {
            case "playerName" -> direction == Sort.Direction.ASC
                    ? repo.findAllAsTableDtoOrderByPlayerNameAsc()
                    : repo.findAllAsTableDtoOrderByPlayerNameDesc();
            case "teamName" -> direction == Sort.Direction.ASC
                    ? repo.findAllAsTableDtoOrderByTeamNameAsc()
                    : repo.findAllAsTableDtoOrderByTeamNameDesc();
            default -> repo.findAllAsTableDto(Sort.by(direction, sortField));
        };
    }
}