package com.ruben.tfg.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdatePlayerDTO {

	private String nombre;
	private String posicion;
	private Integer edad;
	private String nacionalidad;
	private String imageUrl;
	private String teamId;

}
