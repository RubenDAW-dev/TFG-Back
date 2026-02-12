package com.ruben.tfg.repositories;

import com.ruben.tfg.entities.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TeamRepository extends JpaRepository<TeamEntity, String> {

}