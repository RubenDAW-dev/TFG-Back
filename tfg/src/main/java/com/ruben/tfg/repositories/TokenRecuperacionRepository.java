package com.ruben.tfg.repositories;

import com.ruben.tfg.entities.TokenRecuperacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacionEntity, Integer> {
    Optional<TokenRecuperacionEntity> findByToken(String token);
    void deleteByUsuario_Id(Integer usuarioId);
}
