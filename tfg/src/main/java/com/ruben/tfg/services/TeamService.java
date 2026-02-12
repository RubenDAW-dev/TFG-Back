package com.ruben.tfg.services;

import org.springframework.stereotype.Service;

import com.ruben.tfg.repositories.TeamRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TeamService {

    private final TeamRepository teamrepo;

}