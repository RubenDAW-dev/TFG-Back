package com.ruben.tfg.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ruben.tfg.DTOs.CrearComentarioDTO;
import com.ruben.tfg.entities.ComentarioEntity;
import com.ruben.tfg.entities.MatchEntity;
import com.ruben.tfg.entities.PlayerEntity;
import com.ruben.tfg.entities.TeamEntity;
import com.ruben.tfg.entities.UsuarioEntity;
import com.ruben.tfg.repositories.ComentarioRepository;
import com.ruben.tfg.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComentarioService {

	private final ComentarioRepository comentarioRepository;
	private final UsuarioRepository usuarioRepository;

	public ComentarioEntity crearComentario(CrearComentarioDTO req) {
		// Validar “exactamente un objetivo”
		int objetivos = (req.getEquipoId() != null ? 1 : 0) + (req.getJugadorId() != null ? 1 : 0)
				+ (req.getPartidoId() != null ? 1 : 0);
		if (objetivos != 1) {
			throw new IllegalArgumentException(
					"Debes indicar exactamente un objetivo: equipoId, jugadorId o partidoId");
		}
		if (req.getComentario() == null || req.getComentario().isBlank()) {
			throw new IllegalArgumentException("El comentario no puede estar vacío");
		}

		UsuarioEntity usuario = usuarioRepository.findById(req.getUsuarioId())
				.orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + req.getUsuarioId()));

		ComentarioEntity c = new ComentarioEntity();
		c.setComentario(req.getComentario().trim());
		c.setFecha(LocalDate.now());
		c.setUsuario(usuario);

		// Asociaciones por ID sin cargar todo (getReference)
		if (req.getEquipoId() != null) {
			TeamEntity equipoRef = new TeamEntity();
			equipoRef.setId(req.getEquipoId());
			c.setEquipo(equipoRef);
		}
		if (req.getJugadorId() != null) {
			PlayerEntity jugadorRef = new PlayerEntity();
			jugadorRef.setId(req.getJugadorId());
			c.setJugador(jugadorRef);
		}
		if (req.getPartidoId() != null) {
			MatchEntity partidoRef = new MatchEntity();
			partidoRef.setId(req.getPartidoId());
			c.setPartido(partidoRef);
		}

		if (req.getComentarioPadreId() != null) {
			ComentarioEntity padre = comentarioRepository.findById(req.getComentarioPadreId()).orElseThrow(
					() -> new EntityNotFoundException("Comentario padre no encontrado: " + req.getComentarioPadreId()));
			c.setComentarioPadre(padre);
		}
		return comentarioRepository.save(c);
	}

	public List<ComentarioEntity> listarPorPartido(Long matchId) {
		return comentarioRepository.findByPartido_IdOrderByFechaAsc(matchId);
	}

	public List<ComentarioEntity> listarPorEquipo(String teamId) {
		return comentarioRepository.findByEquipo_IdOrderByFechaAsc(teamId);
	}

	public List<ComentarioEntity> listarPorJugador(String playerId) {
		return comentarioRepository.findByJugador_IdOrderByFechaAsc(playerId);
	}

	public List<ComentarioEntity> respuestasDe(Integer comentarioPadreId) {
		return comentarioRepository.findByComentarioPadre_IdOrderByFechaAsc(comentarioPadreId);
	}

	public ComentarioEntity actualizarComentario(ComentarioEntity comentario) {
		if (comentario.getId() == null) {
			throw new IllegalArgumentException("El ID del comentario es requerido para actualizar");
		}
		ComentarioEntity existente = comentarioRepository.findById(comentario.getId())
				.orElseThrow(() -> new EntityNotFoundException("Comentario no encontrado: " + comentario.getId()));
		existente.setComentario(comentario.getComentario());
		return comentarioRepository.save(existente);
	}

	public void eliminarComentario(Integer comentarioId) {
		if (!comentarioRepository.existsById(comentarioId)) {
			throw new EntityNotFoundException("Comentario no encontrado: " + comentarioId);
		}
		comentarioRepository.deleteById(comentarioId);
	}
}