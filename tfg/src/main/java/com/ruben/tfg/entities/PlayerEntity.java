package com.ruben.tfg.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "players")

public class PlayerEntity {

	@Id
	private String id;

	private String nombre;
	private String posicion;
	private Integer edad;
	private String nacionalidad;
	
	
    @ManyToOne
    @JoinColumn(name = "team_id")
    private TeamEntity team;

}
