package com.ruben.tfg.services;

import org.springframework.stereotype.Service;

import com.ruben.tfg.repositories.MatchRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MatchService {
	private final MatchRepository matchrepo;
}
