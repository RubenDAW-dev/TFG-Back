package com.ruben.tfg.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioUpdateDTO {
	@NotBlank
	private String nombre;

	@NotBlank
	@Email
	private String email;
	private Integer rol;
}
