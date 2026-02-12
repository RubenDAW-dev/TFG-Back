package com.ruben.tfg.services;

import org.springframework.stereotype.Service;

import com.ruben.tfg.repositories.PlayerMatchStatsRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PlayerMatchStatsRepo {
	private final PlayerMatchStatsRepository playerstatsrepo;
}
