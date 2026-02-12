package com.ruben.tfg.services;

import org.springframework.stereotype.Service;

import com.ruben.tfg.repositories.TeamMatchStatsRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TeamMatchStatsService {
	private final TeamMatchStatsRepository teamstatsrepo;
}
