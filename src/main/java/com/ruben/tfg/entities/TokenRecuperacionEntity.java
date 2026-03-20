package com.ruben.tfg.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tokens_recuperacion", indexes = { @Index(name = "idx_tokens_usuario", columnList = "id_usuario"),
		@Index(name = "idx_tokens_token", columnList = "token", unique = true) })
public class TokenRecuperacionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_usuario", nullable = false, foreignKey = @ForeignKey(name = "fk_token_usuario"))
	private UsuarioEntity usuario;

	@Column(nullable = false, unique = true, length = 255)
	private String token;

	@Column(name = "fecha_expiracion", nullable = false)
	private LocalDateTime fechaExpiracion;
}