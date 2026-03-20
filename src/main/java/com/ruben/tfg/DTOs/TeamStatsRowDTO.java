package com.ruben.tfg.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TeamStatsRowDTO {
	private String teamId;
	private String teamName;
	private Integer partidos;
	private Integer golesFavor;
	private Integer golesContra;
	private Integer diferenciaGoles;
	private Integer victorias;
	private Integer empates;
	private Integer derrotas;
	private Integer puntos;
	private Double posesionMedia;
	private Double tirosMedia;
	private Double tirosPuertaMedia;
	private Double paradasMedia;
	private Double tarjetasMedia;
	private Integer golesEquipo;
	private Integer asistenciasEquipo;
	private Integer disparosEquipo;
	private Integer disparosPuertaEquipo;
	private Integer amarillasEquipo;
	private Integer rojasEquipo;
	private Integer faltasCometidasEquipo;
	private Integer centrosEquipo;
	private Integer entradasGanadasEquipo;
	private Integer intercepcionesEquipo;
	private Integer autogolesEquipo;
	private Double precisionTiroMedia;
	private Double conversionPenaltiMedia;
	private Double golesPorPartido;
	private Double golesContraPorPartido;
}