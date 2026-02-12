package com.ruben.tfg.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "teams")
public class TeamEntity {

    @Id
    private String id;

    private String nombre;
    private String estadio;
    private String ciudad;
    private Integer capacidad;
}