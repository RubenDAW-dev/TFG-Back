package com.ruben.tfg.services;

import org.springframework.stereotype.Service;
import com.ruben.tfg.repositories.PlayerRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PlayerService {

	private final PlayerRepository playerrepo;
}
