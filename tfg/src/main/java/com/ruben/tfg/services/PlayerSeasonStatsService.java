package com.ruben.tfg.services;

import org.springframework.stereotype.Service;

import com.ruben.tfg.repositories.PlayerSeasonStatsRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PlayerSeasonStatsService {
	private final PlayerSeasonStatsRepository playerseasonstatsrepo;
}
