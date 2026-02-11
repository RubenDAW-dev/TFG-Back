package com.ruben.tfg.repositories;

import com.ruben.tfg.entities.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<MatchEntity, Long> {
}
