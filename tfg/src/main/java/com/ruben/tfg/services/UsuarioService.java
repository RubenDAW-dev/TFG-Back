package com.ruben.tfg.services;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruben.tfg.DTOs.UsuarioDTO;
import com.ruben.tfg.DTOs.UsuarioCreateDTO;
import com.ruben.tfg.DTOs.UsuarioPasswordChangeDTO;
import com.ruben.tfg.DTOs.UsuarioUpdateDTO;
import com.ruben.tfg.entities.UsuarioEntity;
import com.ruben.tfg.mappers.UsuarioMapper;
import com.ruben.tfg.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

	private final UsuarioRepository repo;
	private final PasswordEncoder passwordEncoder;

	public Page<UsuarioDTO> list(int page, int size, String sort, String dir, String qEmail) {
		Sort s = Sort.by((dir == null || dir.equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC,
				(sort == null || sort.isBlank()) ? "id" : sort);
		Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), s);

		Page<UsuarioEntity> res;
		if (qEmail != null && !qEmail.isBlank()) {
			res = repo.findAll(pageable)
					.map(e -> e);
			res = new PageImpl<>(res.getContent().stream()
					.filter(u -> u.getEmail() != null && u.getEmail().toLowerCase().contains(qEmail.toLowerCase()))
					.toList(), pageable, res.getTotalElements()
			);
		} else {
			res = repo.findAll(pageable);
		}

		return res.map(UsuarioMapper::toDTO);
	}

	public Optional<UsuarioDTO> get(Integer id) {
		return repo.findById(id).map(UsuarioMapper::toDTO);
	}

	@Transactional
	public UsuarioDTO create(UsuarioCreateDTO dto) {
		String email = dto.getEmail().trim().toLowerCase();
		if (repo.existsByEmail(email)) {
			throw new DataIntegrityViolationException("Email ya existe");
		}
		UsuarioEntity u = new UsuarioEntity();
		u.setNombre(dto.getNombre().trim());
		u.setEmail(email);
		u.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
		u.setRol(0); // rol por defecto
		UsuarioEntity saved = repo.save(u);
		return UsuarioMapper.toDTO(saved);
	}

	@Transactional
	public Optional<UsuarioDTO> update(Integer id, UsuarioUpdateDTO dto) {
		return repo.findById(id).map(u -> {
			String email = dto.getEmail().trim().toLowerCase();
			// si cambia email, valida unicidad
			if (!u.getEmail().equalsIgnoreCase(email) && repo.existsByEmail(email)) {
				throw new DataIntegrityViolationException("Email ya existe");
			}
			u.setNombre(dto.getNombre().trim());
			u.setEmail(email);
			u.setRol(dto.getRol());
			repo.save(u);
			return UsuarioMapper.toDTO(u);
		});
	}

	@Transactional
	public boolean delete(Integer id) {
		if (!repo.existsById(id))
			return false;
		repo.deleteById(id);
		return true;
	}

	@Transactional
	public boolean changePassword(Integer id, UsuarioPasswordChangeDTO dto) {
		return repo.findById(id).map(u -> {
			u.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
			repo.save(u);
			return true;
		}).orElse(false);
	}
}