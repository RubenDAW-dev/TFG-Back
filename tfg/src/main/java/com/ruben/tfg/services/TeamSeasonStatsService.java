package com.ruben.tfg.services;

import org.springframework.stereotype.Service;

import com.ruben.tfg.repositories.TeamSeasonStatsRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TeamSeasonStatsService {
	private final TeamSeasonStatsRepository teamseasonstatsrepo;
}
