package com.ruben.tfg.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ruben.tfg.entities.TokenRecuperacionEntity;
import com.ruben.tfg.entities.UsuarioEntity;

public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacionEntity, Integer> {
    Optional<TokenRecuperacionEntity> findByToken(String token);
    void deleteByUsuario_Id(Integer usuarioId);
    void deleteByFechaExpiracionBefore(LocalDateTime momento);
	void deleteByUsuario(UsuarioEntity user);
}
