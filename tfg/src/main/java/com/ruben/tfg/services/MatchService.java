package com.ruben.tfg.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ruben.tfg.DTOs.FutureMatchDTO;
import com.ruben.tfg.DTOs.MatchDTO;
import com.ruben.tfg.DTOs.PastMatchDTO;
import com.ruben.tfg.DTOs.TeamFutureMatchDTO;
import com.ruben.tfg.entities.MatchEntity;
import com.ruben.tfg.repositories.MatchRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class MatchService {

	private final MatchRepository repo;
	private ModelMapper modelMapper = new ModelMapper();

	public List<MatchEntity> getAll() {
		return repo.findAll();
	}

	public MatchEntity getById(Long id) {
		MatchEntity match = repo.findById(id).orElseThrow(() -> new RuntimeException("Match not found"));
		return match;
	}

	public List<MatchEntity> getByHomeTeam(String teamId) {
		return repo.findByHomeTeamId(teamId);
	}

	public List<MatchEntity> getByAwayTeam(String teamId) {
		return repo.findByAwayTeamId(teamId);
	}

	public List<MatchEntity> getByWeek(Integer wk) {
		return repo.findByWk(wk);
	}

	public List<MatchEntity> getByDate(LocalDate date) {
		return repo.findByDate(date);
	}

	public void update(MatchDTO match) {
		MatchEntity entity = repo.findById(match.getId()).orElseThrow(() -> new RuntimeException("Match not found"));
		modelMapper.map(match, entity);
		repo.save(entity);
	}

	public List<PastMatchDTO> getLastMatches() {
		List<MatchEntity> matches = repo.findLastMatches(LocalDate.now(), PageRequest.of(0, 8)
		);

		return matches.stream().map(m -> {
			PastMatchDTO dto = new PastMatchDTO();
			dto.setHomeTeam(m.getHomeTeam().getNombre());
			dto.setAwayTeam(m.getAwayTeam().getNombre());
			dto.setScore(m.getScore());
			return dto;
		}).toList();
	}
	
	public List<FutureMatchDTO> getNextMatches() {
		List<MatchEntity> matches = repo.findNextMatches(LocalDate.now(), PageRequest.of(0, 8)
		);

		return matches.stream().map(m -> {
			FutureMatchDTO dto = new FutureMatchDTO();
			dto.setHomeTeam(m.getHomeTeam().getNombre());
			dto.setAwayTeam(m.getAwayTeam().getNombre());
			dto.setDay(m.getDay());
			dto.setTime(m.getTime());
			return dto;
		}).toList();
	}
	
	public List<TeamFutureMatchDTO> getNextMatchesByTeam(String teamId) {
	    LocalDate today = LocalDate.now();
	 
	    return repo.findAll().stream()
	        .filter(m ->
	            (m.getHomeTeam().getId().equals(teamId) || m.getAwayTeam().getId().equals(teamId))
	            && m.getScore() == null        // sin resultado = partido futuro
	            && m.getDate() != null
	            && !m.getDate().isBefore(today)
	        )
	        .sorted(Comparator.comparing(MatchEntity::getDate))
	        .limit(5)
	        .map(m -> new TeamFutureMatchDTO(
	            m.getId(),
	            m.getHomeTeam().getId(),
	            m.getHomeTeam().getNombre(),
	            m.getAwayTeam().getId(),
	            m.getAwayTeam().getNombre(),
	            m.getDay(),
	            m.getTime(),
	            m.getVenue(),
	            m.getWk(),
	            m.getDate().toString()
	        ))
	        .collect(Collectors.toList());
	}

}
