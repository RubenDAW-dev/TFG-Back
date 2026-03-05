package com.ruben.tfg.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioPasswordChangeDTO {
	@NotBlank
	private String newPassword;
}
