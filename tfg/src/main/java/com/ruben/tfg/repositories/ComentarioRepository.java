package com.ruben.tfg.repositories;

import com.ruben.tfg.entities.ComentarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<ComentarioEntity, Integer> {
    List<ComentarioEntity> findByPartido_IdOrderByFechaAsc(Long matchId);
    List<ComentarioEntity> findByEquipo_IdOrderByFechaAsc(String teamId);
    List<ComentarioEntity> findByJugador_IdOrderByFechaAsc(String playerId);
    List<ComentarioEntity> findByComentarioPadre_IdOrderByFechaAsc(Integer comentarioPadreId);
}