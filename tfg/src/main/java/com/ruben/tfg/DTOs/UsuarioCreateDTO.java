package com.ruben.tfg.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioCreateDTO {
	@NotBlank
	private String nombre;
	@NotBlank @Email
	private String email;
	@NotBlank @Size(min = 6)
	private String password;
}
