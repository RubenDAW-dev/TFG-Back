package com.ruben.tfg.services;

import com.ruben.tfg.DTOs.ComentarioResponseDTO;
import com.ruben.tfg.DTOs.CrearComentarioDTO;
import com.ruben.tfg.entities.*;
import com.ruben.tfg.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ComentarioService {

    private final ComentarioRepository  comentarioRepository;
    private final UsuarioRepository     usuarioRepository;

    // ── CREAR ─────────────────────────────────────────────────────────────────

    public ComentarioResponseDTO crearComentario(CrearComentarioDTO req) {

        validarObjetivo(req);
        validarTitulo(req);

        UsuarioEntity usuario = usuarioRepository.findById(req.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario no encontrado: " + req.getUsuarioId()));

        ComentarioEntity comentario = construirEntidad(req, usuario);

        ComentarioEntity guardado = comentarioRepository.save(comentario);

        return mapToDTO(guardado);
    }
    
    private void validarObjetivo(CrearComentarioDTO req) {

        int objetivos =
                (req.getEquipoId()  != null ? 1 : 0) +
                (req.getJugadorId() != null ? 1 : 0) +
                (req.getPartidoId() != null ? 1 : 0);

        if (objetivos != 1) {
            throw new IllegalArgumentException(
                    "Debes indicar exactamente un objetivo: equipoId, jugadorId o partidoId.");
        }
    }
    private void validarTitulo(CrearComentarioDTO req) {

        if (req.getComentarioPadreId() == null) {
            if (req.getTitulo() == null || req.getTitulo().isBlank()) {
                throw new IllegalArgumentException(
                        "Los topics del foro deben tener un título.");
            }
        }
    }
    private ComentarioEntity construirEntidad(CrearComentarioDTO req, UsuarioEntity usuario) {

        ComentarioEntity c = new ComentarioEntity();

        c.setComentario(req.getComentario().trim());
        c.setFecha(LocalDate.now());
        c.setUsuario(usuario);

        if (req.getTitulo() != null && !req.getTitulo().isBlank()) {
            c.setTitulo(req.getTitulo().trim());
        }

        if (req.getEquipoId() != null) {
            TeamEntity ref = new TeamEntity();
            ref.setId(req.getEquipoId());
            c.setEquipo(ref);
        }

        if (req.getJugadorId() != null) {
            PlayerEntity ref = new PlayerEntity();
            ref.setId(req.getJugadorId());
            c.setJugador(ref);
        }

        if (req.getPartidoId() != null) {
            MatchEntity ref = new MatchEntity();
            ref.setId(req.getPartidoId());
            c.setPartido(ref);
        }

        if (req.getComentarioPadreId() != null) {
            ComentarioEntity padre = comentarioRepository
                    .findById(req.getComentarioPadreId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Comentario padre no encontrado: " + req.getComentarioPadreId()));

            c.setComentarioPadre(padre);
        }

        return c;
    }
    
    private ComentarioResponseDTO mapToDTO(ComentarioEntity creado) {

        ComentarioResponseDTO dto = new ComentarioResponseDTO();

        dto.setId(creado.getId());
        dto.setComentario(creado.getComentario());
        dto.setTitulo(creado.getTitulo());
        dto.setFecha(creado.getFecha());
        dto.setUsuarioId(creado.getUsuario().getId());
        dto.setUsuarioNombre(creado.getUsuario().getNombre());

        return dto;
    }

    // ── ACTUALIZAR ────────────────────────────────────────────────────────────

    public ComentarioEntity actualizarComentario(ComentarioEntity comentario) {
        if (!comentarioRepository.existsById(comentario.getId())) {
            throw new EntityNotFoundException("Comentario no encontrado: " + comentario.getId());
        }
        return comentarioRepository.save(comentario);
    }

    // ── ELIMINAR ──────────────────────────────────────────────────────────────

    public void eliminarComentario(Integer id) {
        if (!comentarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Comentario no encontrado: " + id);
        }
        comentarioRepository.deleteById(id);
    }

    // ── LISTAR POR CONTEXTO (para páginas de partido/equipo/jugador) ──────────

    public List<ComentarioResponseDTO> listarPorPartido(Long matchId) {
        return comentarioRepository.findByPartidoIdAndComentarioPadreIsNull(matchId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ComentarioResponseDTO> listarPorEquipo(String teamId) {
        return comentarioRepository.findByEquipoIdAndComentarioPadreIsNull(teamId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ComentarioResponseDTO> listarPorJugador(String playerId) {
        return comentarioRepository.findByJugadorIdAndComentarioPadreIsNull(playerId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── FORO GENERAL: todos los topics raíz (ordenados por fecha desc) ────────

    public List<ComentarioResponseDTO> listarTopicsForo() {
        return comentarioRepository
                .findByComentarioPadreIsNullOrderByFechaDesc()
                .stream().map(this::toDTOResumen).collect(Collectors.toList());
    }

    // ── HILO: respuestas de un comentario ─────────────────────────────────────

    public List<ComentarioResponseDTO> respuestasDe(Integer comentarioId) {
        ComentarioEntity padre = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Comentario no encontrado: " + comentarioId));
        return padre.getRespuestas()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── TOPIC INDIVIDUAL (para vista de hilo) ─────────────────────────────────

    public ComentarioResponseDTO obtenerTopic(Integer id) {
        ComentarioEntity c = comentarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Comentario no encontrado: " + id));
        return toDTO(c);
    }

    // ── MAPEO ─────────────────────────────────────────────────────────────────

    /** DTO completo con respuestas anidadas (para vista de hilo). */
    private ComentarioResponseDTO toDTO(ComentarioEntity c) {
        ComentarioResponseDTO dto = buildBase(c);
        dto.setRespuestas(
            c.getRespuestas().stream().map(this::toDTO).collect(Collectors.toList())
        );
        dto.setTotalRespuestas(c.getRespuestas().size());
        return dto;
    }

    /** DTO resumen sin respuestas anidadas (para lista del foro). */
    private ComentarioResponseDTO toDTOResumen(ComentarioEntity c) {
        ComentarioResponseDTO dto = buildBase(c);
        dto.setTotalRespuestas(c.getRespuestas().size());
        return dto;
    }

    private ComentarioResponseDTO buildBase(ComentarioEntity c) {
        ComentarioResponseDTO dto = new ComentarioResponseDTO();
        dto.setId(c.getId());
        dto.setTitulo(c.getTitulo());
        dto.setComentario(c.getComentario());
        dto.setFecha(c.getFecha());
        dto.setUsuarioId(c.getUsuario().getId());
        dto.setUsuarioNombre(c.getUsuario().getNombre());

        if (c.getComentarioPadre() != null) {
            dto.setComentarioPadreId(c.getComentarioPadre().getId());
        }
        if (c.getEquipo() != null) {
            dto.setEquipoId(c.getEquipo().getId());
            dto.setTargetNombre(c.getEquipo().getNombre()); // ajusta al getter real
        }
        if (c.getJugador() != null) {
            dto.setJugadorId(c.getJugador().getId());
            dto.setTargetNombre(c.getJugador().getNombre()); // ajusta al getter real
        }
        if (c.getPartido() != null) {
            dto.setPartidoId(c.getPartido().getId());
            // Ajusta según tu MatchEntity (homeTeam vs awayTeam, etc.)
            dto.setTargetNombre(c.getPartido().getId().toString());
        }
        return dto;
    }
}