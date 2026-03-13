package com.ruben.tfg.repositories;

import com.ruben.tfg.entities.ComentarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<ComentarioEntity, Integer> {

    List<ComentarioEntity> findByPartidoIdAndComentarioPadreIsNull(Long partidoId);
    List<ComentarioEntity> findByEquipoIdAndComentarioPadreIsNull(String equipoId);
    List<ComentarioEntity> findByJugadorIdAndComentarioPadreIsNull(String jugadorId);

    List<ComentarioEntity> findByComentarioPadreIsNullOrderByFechaDesc();

    List<ComentarioEntity> findByUsuarioId(Integer usuarioId);
}