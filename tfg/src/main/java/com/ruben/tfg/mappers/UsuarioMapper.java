package com.ruben.tfg.mappers;

import com.ruben.tfg.DTOs.UsuarioDTO;
import com.ruben.tfg.entities.UsuarioEntity;

public class UsuarioMapper {
	public static UsuarioDTO toDTO(UsuarioEntity usuario) {
		if (usuario == null) {
			return null;
		}
		UsuarioDTO dto = new UsuarioDTO();
		dto.setId(usuario.getId());
		dto.setNombre(usuario.getNombre());
		dto.setEmail(usuario.getEmail());
		dto.setRol(usuario.getRol());
		return dto;
	}

	public static UsuarioEntity toEntity(UsuarioDTO dto) {
		if (dto == null) {
			return null;
		}
		UsuarioEntity usuario = new UsuarioEntity();
		usuario.setId(dto.getId());
		usuario.setNombre(dto.getNombre());
		usuario.setEmail(dto.getEmail());
		usuario.setRol(dto.getRol());
		return usuario;
	}
}
