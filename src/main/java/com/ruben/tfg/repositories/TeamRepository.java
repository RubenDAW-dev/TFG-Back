package com.ruben.tfg.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ruben.tfg.entities.TeamEntity;


public interface TeamRepository extends JpaRepository<TeamEntity, String> {

	List<TeamEntity> findByNombreContainingIgnoreCase(String q);

}