package com.ruben.tfg.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "comentarios", indexes = { @Index(name = "idx_comentarios_equipo", columnList = "id_equipo"),
		@Index(name = "idx_comentarios_jugador", columnList = "id_jugador"),
		@Index(name = "idx_comentarios_partido", columnList = "id_partido"),
		@Index(name = "idx_comentarios_usuario", columnList = "id_usuario"),
		@Index(name = "idx_comentarios_padre", columnList = "comentario_padre") })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ComentarioEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// Objetivo del comentario (solo uno de los tres debería rellenarse)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_equipo", foreignKey = @ForeignKey(name = "fk_comentario_equipo"))
	private TeamEntity equipo; // teams.id (String)

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_jugador", foreignKey = @ForeignKey(name = "fk_comentario_jugador"))
	private PlayerEntity jugador; // players.id (String)

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_partido", foreignKey = @ForeignKey(name = "fk_comentario_partido"))
	private MatchEntity partido; // matches.id (Long)

	@Column(columnDefinition = "TEXT", nullable = false)
	private String comentario;

	@Column(nullable = false)
	private LocalDate fecha;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_usuario", nullable = false, foreignKey = @ForeignKey(name = "fk_comentario_usuario"))
	private UsuarioEntity usuario;

	// Comentarios anidados (self reference)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comentario_padre", foreignKey = @ForeignKey(name = "fk_comentario_padre"))
	private ComentarioEntity comentarioPadre;

	@OneToMany(mappedBy = "comentarioPadre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComentarioEntity> respuestas = new ArrayList<>();
}