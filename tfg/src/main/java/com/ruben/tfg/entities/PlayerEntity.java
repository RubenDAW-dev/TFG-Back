package com.ruben.tfg.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private Integer dorsal;
    private Integer edad;
    private String nacionalidad;

    
}
