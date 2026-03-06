package com.ruben.tfg.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
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
    private final PlayerRepository playerRepo;

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

    public List<RankingDTO> topScorers(String teamId, Pageable pageable) {
        return repo.topScorers(teamId, pageable);
    }

    public List<RankingDTO> topAssists(String teamId, Pageable pageable) {
        return repo.topAssists(teamId, pageable);
    }

    public Page<PlayerSeasonStatsDTO> aggregateByTeam(String teamId, Pageable pageable) {
        return repo.findAllByTeamIdAsDtoPaged(teamId, pageable);
    }

    public List<PlayerSeasonStatsDTO> getByTeamId(String teamId) {
        return repo.findAllByTeamIdAsDto(teamId);
    }

    public List<RankingDTO> AlltopScorers(Pageable pageable) {
        return repo.AlltopScorers(pageable);
    }

    public List<RankingDTO> AlltopAssists(Pageable pageable) {
        return repo.AlltopAssists(pageable);
    }

    public Page<PlayerStatsTableDTO> getAllWithNamesPaged(int page, int size, String sortField, String sortDir) {
        Sort.Direction direction = sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size);

        return switch (sortField) {
            case "playerName" -> direction == Sort.Direction.ASC
                    ? repo.findAllAsTableDtoOrderByPlayerNameAsc(pageable)
                    : repo.findAllAsTableDtoOrderByPlayerNameDesc(pageable);
            case "teamName" -> direction == Sort.Direction.ASC
                    ? repo.findAllAsTableDtoOrderByTeamNameAsc(pageable)
                    : repo.findAllAsTableDtoOrderByTeamNameDesc(pageable);
            default -> repo.findAllAsTableDto(PageRequest.of(page, size, Sort.by(direction, sortField)));
        };
    }
}