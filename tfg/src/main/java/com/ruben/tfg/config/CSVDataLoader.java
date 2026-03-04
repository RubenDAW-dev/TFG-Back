package com.ruben.tfg.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;
import com.ruben.tfg.entities.MatchEntity;
import com.ruben.tfg.entities.PlayerEntity;
import com.ruben.tfg.entities.PlayerMatchStatsEntity;
import com.ruben.tfg.entities.PlayerSeasonStatsEntity;
import com.ruben.tfg.entities.TeamEntity;
import com.ruben.tfg.entities.TeamMatchStatsEntity;
import com.ruben.tfg.entities.TeamSeasonStatsEntity;
import com.ruben.tfg.repositories.MatchRepository;
import com.ruben.tfg.repositories.PlayerMatchStatsRepository;
import com.ruben.tfg.repositories.PlayerRepository;
import com.ruben.tfg.repositories.PlayerSeasonStatsRepository;
import com.ruben.tfg.repositories.TeamMatchStatsRepository;
import com.ruben.tfg.repositories.TeamRepository;
import com.ruben.tfg.repositories.TeamSeasonStatsRepository;

import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CSVDataLoader implements CommandLineRunner {

	private final TeamRepository teamRepo;
	private final PlayerRepository playerRepo;
	private final MatchRepository matchRepo;
	private final TeamMatchStatsRepository teamStatsRepo;
	private final PlayerMatchStatsRepository playerStatsRepo;
	private final PlayerSeasonStatsRepository playerSeasonStatsRepo;
	private final TeamSeasonStatsRepository teamSeasonStatsRepo;

	@PersistenceContext
	private jakarta.persistence.EntityManager entityManager;
	private final org.springframework.transaction.support.TransactionTemplate transactionTemplate;

	@Override
	public void run(String... args) throws Exception {
		loadTeams();
		loadPlayers();
		loadMatches();
		loadTeamMatchStats();
		loadPlayerMatchStats();
		updateCardsFromPlayerStats();
		loadPlayerSeasonStats();
		loadTeamSeasonStats();
	}

	// ---------------------------- EQUIPOS ----------------------------
	private void loadTeams() throws Exception {
		BufferedReader br = getReader("csv/equipos_final_ids.csv");
		br.readLine(); // saltar cabecera

		String line;
		while ((line = br.readLine()) != null) {
			String[] d = line.split(",");

			String teamId = d[4]; // TEAM-XXXX

			TeamEntity t = teamRepo.findById(teamId).orElseGet(TeamEntity::new);
			t.setId(teamId);
			t.setNombre(d[0]);
			t.setEstadio(d[1]);
			t.setCiudad(d[2]);
			t.setCapacidad(parseIntOrNull(d[3]));

			teamRepo.save(t);
		}
	}

	// ---------------------------- JUGADORES ----------------------------
	private void loadPlayers() throws Exception {
		CSVReader reader = new CSVReader(
				new InputStreamReader(new ClassPathResource("csv/jugadores_laliga_ids_FINAL.csv").getInputStream()));

		reader.readNext(); // cabecera

		// Orden de columnas: player_id, team_id, Player, Nation, Pos, Squad, Age, ...
		String[] d;
		while ((d = reader.readNext()) != null) {
			String playerId = d[0];
			String teamId = d[1];

			PlayerEntity p = playerRepo.findById(playerId).orElseGet(PlayerEntity::new);
			p.setId(playerId);
			p.setNombre(d[2]); // Player
			p.setNacionalidad(d[3]); // Nation
			p.setPosicion(d[4]); // Pos (maneja "MF,FW" correctamente con CSVReader)
			p.setEdad(parseAge(d[6])); // Age
			p.setTeam(teamRepo.findById(teamId).orElse(null));

			playerRepo.save(p);
		}
	}

	private Integer parseAge(String age) {
		try {
			if (age == null || age.isEmpty())
				return null;
			return Integer.parseInt(age.split("-")[0]);
		} catch (Exception e) {
			return null;
		}
	}

	// ---------------------------- PARTIDOS ----------------------------
	private void loadMatches() throws Exception {
		BufferedReader br = getReader("csv/PARTIDOS_FINAL.CSV");
		br.readLine(); // cabecera

		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String line;
		while ((line = br.readLine()) != null) {
			String[] d = splitCSV(line);

			Long matchId = Long.parseLong(d[0]);

			MatchEntity m = matchRepo.findById(matchId).orElseGet(MatchEntity::new);
			m.setId(matchId);

			m.setHomeTeam(teamRepo.findById(d[1]).orElse(null));
			m.setAwayTeam(teamRepo.findById(d[2]).orElse(null));

			m.setWk(parseIntOrNull(d[3]));
			m.setDay(d[4]);
			m.setDate(parseDateOrNull(d[5], df));
			m.setTime(emptyToNull(d[6]));
			m.setScore(emptyToNull(d[7]));

			m.setAttendance(parseIntOrNull(safe(d[8]).replace(".0", "")));

			m.setVenue(emptyToNull(d[9]));
			m.setReferee(emptyToNull(d[10]));

			matchRepo.save(m);
		}
	}

	// ---------------------------- TEAM MATCH STATS ----------------------------
	private void loadTeamMatchStats() throws Exception {
		BufferedReader br = getReader("csv/TEAM_MATCH_STATS_FINAL.csv");
		br.readLine(); // cabecera

		String line;
		while ((line = br.readLine()) != null) {
			String[] d = line.split(",");

			Long matchId = Long.parseLong(d[0]);
			String teamId = d[1];

			MatchEntity match = matchRepo.findById(matchId).orElse(null);
			TeamEntity team = teamRepo.findById(teamId).orElse(null);

			if (match == null || team == null) {
				System.out.println("⚠ No se puede cargar TeamMatchStats: match=" + matchId + ", team=" + teamId);
				continue;
			}

			Optional<TeamMatchStatsEntity> opt = teamStatsRepo.findByMatch_IdAndTeam_Id(matchId, teamId);
			TeamMatchStatsEntity s = opt.orElseGet(TeamMatchStatsEntity::new);

			s.setMatch(match);
			s.setTeam(team);
			s.setSide(d[2]);
			s.setPossession(parseIntOrNull(d[3]));
			s.setShots_on_target(parseIntOrNull(d[4]));
			s.setShots_total(parseIntOrNull(d[5]));
			s.setSaves(parseIntOrNull(d[6]));
			s.setCards(parseIntOrNull(d[7]));

			teamStatsRepo.save(s);
		}
	}

	// ---------------------------- PLAYER MATCH STATS ----------------------------
	private void loadPlayerMatchStats() throws Exception {
		CSVReader reader = new CSVReader(
				new InputStreamReader(new ClassPathResource("csv/PLAYER_STATS_FINAL.CSV").getInputStream()));

		String[] d = reader.readNext(); // cabecera
		int row = 1;

		while ((d = reader.readNext()) != null) {
			row++;

			if (d.length != 22) {
				System.out.println("❌ Línea " + row + " inválida, columnas=" + d.length);
				System.out.println(Arrays.toString(d));
				continue;
			}

			Long matchId = Long.parseLong(d[0]);
			String playerId = d[1];

			MatchEntity match = matchRepo.findById(matchId).orElse(null);
			if (match == null) {
				System.out.println("⚠ match_id no encontrado: " + matchId);
				continue;
			}

			PlayerEntity player = playerRepo.findById(playerId).orElse(null);
			if (player == null) {
				System.out.println("⚠ player_id no encontrado: " + playerId);
				continue;
			}

			Optional<PlayerMatchStatsEntity> opt = playerStatsRepo.findByMatch_IdAndPlayer_Id(matchId, playerId);
			PlayerMatchStatsEntity s = opt.orElseGet(PlayerMatchStatsEntity::new);

			s.setMatch(match);
			s.setPlayer(player);

			s.setNumber(parseIntOrNull(safe(d[2]).replace(".0", "")));
			s.setNation(d[3]);
			s.setPos(d[4]);
			s.setAge(safe(d[5]).length() >= 2 ? safe(d[5]).substring(0, 2) : safe(d[5]));
			s.setMinutes(parseIntOrNull(d[6]));
			s.setGls(parseIntOrNull(d[7]));
			s.setAst(parseIntOrNull(d[8]));
			s.setPk(parseIntOrNull(d[9]));
			s.setPkAtt(parseIntOrNull(d[10]));
			s.setShots(parseIntOrNull(d[11]));
			s.setShotsOnTarget(parseIntOrNull(d[12]));
			s.setYellowCards(parseIntOrNull(d[13]));
			s.setRedCards(parseIntOrNull(d[14]));
			s.setFoulsCommitted(parseIntOrNull(d[15]));
			s.setFoulsDrawn(parseIntOrNull(d[16]));
			s.setOffsides(parseIntOrNull(d[17]));
			s.setCrosses(parseIntOrNull(d[18]));
			s.setTacklesWon(parseIntOrNull(d[19]));
			s.setInterceptions(parseIntOrNull(d[20]));
			s.setOwnGoals(parseIntOrNull(d[21]));

			playerStatsRepo.save(s);
		}
	}

	// ---------------------------- CARDS UPDATE ----------------------------
	private void updateCardsFromPlayerStats() {
		List<TeamMatchStatsEntity> allTeamStats = teamStatsRepo.findAll();

		for (TeamMatchStatsEntity teamStat : allTeamStats) {
			Long matchId = teamStat.getMatch().getId();
			String teamId = teamStat.getTeam().getId();

			List<PlayerMatchStatsEntity> playerStats = playerStatsRepo.findByMatch_Id(matchId);

			int totalCards = playerStats.stream()
					.filter(ps -> ps.getPlayer().getTeam() != null && ps.getPlayer().getTeam().getId().equals(teamId))
					.mapToInt(ps -> {
						int yellow = ps.getYellowCards() != null ? ps.getYellowCards() : 0;
						int red = ps.getRedCards() != null ? ps.getRedCards() : 0;
						return yellow + red;
					}).sum();

			teamStat.setCards(totalCards);
			teamStatsRepo.save(teamStat);
		}

		System.out.println("✔ Tarjetas actualizadas en TeamMatchStats");
	}

	// ---------------------------- PLAYER SEASON STATS ----------------------------
	private void loadPlayerSeasonStats() {
	    List<PlayerMatchStatsEntity> allStats = playerStatsRepo.findAll();

	    Map<String, List<PlayerMatchStatsEntity>> porJugador = allStats.stream()
	        .filter(ps -> ps.getPlayer() != null)
	        .collect(Collectors.groupingBy(ps -> ps.getPlayer().getId()));

	    int count = 0;
	    for (Map.Entry<String, List<PlayerMatchStatsEntity>> entry : porJugador.entrySet()) {
	        String playerId = entry.getKey();
	        List<PlayerMatchStatsEntity> stats = entry.getValue();

	        transactionTemplate.execute(status -> {
	            playerSeasonStatsRepo.deleteById(playerId);

	            PlayerEntity player = entityManager.find(PlayerEntity.class, playerId);
	            if (player == null) return null;

	            int partidos           = stats.size();
	            int minutos            = sum(stats, PlayerMatchStatsEntity::getMinutes);
	            int goles              = sum(stats, PlayerMatchStatsEntity::getGls);
	            int asistencias        = sum(stats, PlayerMatchStatsEntity::getAst);
	            int penaltisMarcados   = sum(stats, PlayerMatchStatsEntity::getPk);
	            int penaltisIntentados = sum(stats, PlayerMatchStatsEntity::getPkAtt);
	            int disparos           = sum(stats, PlayerMatchStatsEntity::getShots);
	            int disparosPuerta     = sum(stats, PlayerMatchStatsEntity::getShotsOnTarget);
	            int amarillas          = sum(stats, PlayerMatchStatsEntity::getYellowCards);
	            int rojas              = sum(stats, PlayerMatchStatsEntity::getRedCards);
	            int faltasCometidas    = sum(stats, PlayerMatchStatsEntity::getFoulsCommitted);
	            int faltasRecibidas    = sum(stats, PlayerMatchStatsEntity::getFoulsDrawn);
	            int fueraDeJuego       = sum(stats, PlayerMatchStatsEntity::getOffsides);
	            int centros            = sum(stats, PlayerMatchStatsEntity::getCrosses);
	            int entradasGanadas    = sum(stats, PlayerMatchStatsEntity::getTacklesWon);
	            int intercepciones     = sum(stats, PlayerMatchStatsEntity::getInterceptions);
	            int autogoles          = sum(stats, PlayerMatchStatsEntity::getOwnGoals);

	            double golesPor90              = per90(goles, minutos);
	            double asistenciasPor90        = per90(asistencias, minutos);
	            double disparosPor90           = per90(disparos, minutos);
	            double disparosPuertaPor90     = per90(disparosPuerta, minutos);
	            double amarillasPor90          = per90(amarillas, minutos);
	            double rojasPor90              = per90(rojas, minutos);
	            double faltasCometidasPor90    = per90(faltasCometidas, minutos);
	            double faltasRecibidasPor90    = per90(faltasRecibidas, minutos);
	            double fueraDeJuegoPor90       = per90(fueraDeJuego, minutos);
	            double centrosPor90            = per90(centros, minutos);
	            double entradasGanadasPor90    = per90(entradasGanadas, minutos);
	            double intercepcionesPor90     = per90(intercepciones, minutos);

	            double precisionTiro    = safeRatio(disparosPuerta, disparos);          // [0..1]
	            double conversionPenalti = safeRatio(penaltisMarcados, penaltisIntentados); // [0..1]

	            PlayerSeasonStatsEntity s = new PlayerSeasonStatsEntity();
	            s.setPlayerId(playerId);
	            s.setPlayer(player);

	            s.setPartidos(partidos);
	            s.setMinutos(minutos);
	            s.setGoles(goles);
	            s.setAsistencias(asistencias);
	            s.setPenaltisMarcados(penaltisMarcados);
	            s.setPenaltisIntentados(penaltisIntentados);
	            s.setDisparos(disparos);
	            s.setDisparosPuerta(disparosPuerta);
	            s.setAmarillas(amarillas);
	            s.setRojas(rojas);
	            s.setFaltasCometidas(faltasCometidas);
	            s.setFaltasRecibidas(faltasRecibidas);
	            s.setFueraDeJuego(fueraDeJuego);
	            s.setCentros(centros);
	            s.setEntradasGanadas(entradasGanadas);
	            s.setIntercepciones(intercepciones);
	            s.setAutogoles(autogoles);

	            s.setGolesPor90(golesPor90);
	            s.setAsistenciasPor90(asistenciasPor90);
	            s.setDisparosPor90(disparosPor90);
	            s.setDisparosPuertaPor90(disparosPuertaPor90);
	            s.setAmarillasPor90(amarillasPor90);
	            s.setRojasPor90(rojasPor90);
	            s.setFaltasCometidasPor90(faltasCometidasPor90);
	            s.setFaltasRecibidasPor90(faltasRecibidasPor90);
	            s.setFueraDeJuegoPor90(fueraDeJuegoPor90);
	            s.setCentrosPor90(centrosPor90);
	            s.setEntradasGanadasPor90(entradasGanadasPor90);
	            s.setIntercepcionesPor90(intercepcionesPor90);

	            s.setPrecisionTiro(precisionTiro);
	            s.setConversionPenalti(conversionPenalti);

	            entityManager.persist(s);
	            return null;
	        });

	        count++;
	    }

	    System.out.println("✔ PlayerSeasonStats calculadas: " + count + " jugadores");
	}

	// ---------------------------- TEAM SEASON STATS ----------------------------
	private void loadTeamSeasonStats() {
		List<TeamMatchStatsEntity> allStats = teamStatsRepo.findAll();

		Map<String, List<TeamMatchStatsEntity>> porEquipo = allStats.stream().filter(ts -> ts.getTeam() != null)
				.collect(Collectors.groupingBy(ts -> ts.getTeam().getId()));

		for (Map.Entry<String, List<TeamMatchStatsEntity>> entry : porEquipo.entrySet()) {
			String teamId = entry.getKey();
			List<TeamMatchStatsEntity> stats = entry.getValue();

			transactionTemplate.execute(status -> {
				teamSeasonStatsRepo.deleteById(teamId);

				TeamEntity team = entityManager.find(TeamEntity.class, teamId);
				if (team == null)
					return null;

				TeamSeasonStatsEntity s = new TeamSeasonStatsEntity();
				s.setTeamId(teamId);
				s.setTeam(team);

				s.setPartidos(stats.size());

				int golesFavor = 0, golesContra = 0, victorias = 0, empates = 0, derrotas = 0;
				for (TeamMatchStatsEntity ts : stats) {
					String rawScore = ts.getMatch().getScore();

					if (rawScore != null) {

						// Normalizamos guiones (1–0, 1 — 0, 1 − 0, etc.)
						String score = rawScore.trim().replace("–", "-").replace("—", "-").replace("−", "-")
								.replaceAll("\\s+", ""); // quitar espacios

						if (score.matches("\\d+-\\d+")) {
							String[] partes = score.split("-");
							int golesHome = Integer.parseInt(partes[0]);
							int golesAway = Integer.parseInt(partes[1]);

							int gF = "HOME".equals(ts.getSide()) ? golesHome : golesAway;
							int gC = "HOME".equals(ts.getSide()) ? golesAway : golesHome;

							golesFavor += gF;
							golesContra += gC;

							if (gF > gC)
								victorias++;
							else if (gF == gC)
								empates++;
							else
								derrotas++;
						}
					}
				}

				s.setGoles_favor(golesFavor);
				s.setGoles_contra(golesContra);
				s.setVictorias(victorias);
				s.setEmpates(empates);
				s.setDerrotas(derrotas);
				s.setPuntos(victorias * 3 + empates);

				s.setPosesion_media(
						avg(stats, ts -> ts.getPossession() != null ? ts.getPossession().doubleValue() : null));
				s.setTiros_media(
						avg(stats, ts -> ts.getShots_total() != null ? ts.getShots_total().doubleValue() : null));
				s.setTiros_puerta_media(avg(stats,
						ts -> ts.getShots_on_target() != null ? ts.getShots_on_target().doubleValue() : null));
				s.setParadas_media(avg(stats, ts -> ts.getSaves() != null ? ts.getSaves().doubleValue() : null));
				s.setTarjetas_media(avg(stats, ts -> ts.getCards() != null ? ts.getCards().doubleValue() : null));

				entityManager.persist(s);
				return null;
			});
		}

		System.out.println("✔ TeamSeasonStats calculadas: " + porEquipo.size() + " equipos");
	}



	private Double avg(List<TeamMatchStatsEntity> list,
			java.util.function.Function<TeamMatchStatsEntity, Double> getter) {
		return round2(list.stream().map(getter).filter(v -> v != null).mapToDouble(Double::doubleValue).average()
				.orElse(0.0));
	}


	// ---------------------------- HELPERS ----------------------------
	private BufferedReader getReader(String path) throws Exception {
		return new BufferedReader(new InputStreamReader(new ClassPathResource(path).getInputStream()));
	}

	private Integer parseIntOrNull(String s) {
		try {
			s = safe(s).trim();
			if (s.isEmpty())
				return null;
			return Integer.parseInt(s);
		} catch (Exception e) {
			return null;
		}
	}

	private String[] splitCSV(String line) {
		return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
	}

	private String emptyToNull(String s) {
		s = safe(s).trim();
		return s.isEmpty() ? null : s;
	}

	private String safe(String s) {
		return (s == null) ? "" : s;
	}

	private LocalDate parseDateOrNull(String s, DateTimeFormatter df) {
		try {
			s = safe(s).trim();
			if (s.isEmpty())
				return null;
			return LocalDate.parse(s, df);
		} catch (Exception e) {
			return null;
		}
	}
	private int sum(List<PlayerMatchStatsEntity> stats, java.util.function.Function<PlayerMatchStatsEntity, Integer> getter) {
	    int total = 0;
	    for (PlayerMatchStatsEntity s : stats) {
	        Integer v = getter.apply(s);
	        if (v != null) total += v;
	    }
	    return total;
	}

	private double per90(int total, int minutos) {
	    if (minutos <= 0) return 0.0;
	    double p90 = minutos / 90.0;
	    return round2(total / p90);
	}

	private double safeRatio(int numerador, int denominador) {
	    if (denominador <= 0) return 0.0;
	    return round4(numerador / (double) denominador);
	}

	private double round2(double v) {
	    return Math.round(v * 100.0) / 100.0;
	}

	private double round4(double v) {
	    return Math.round(v * 10000.0) / 10000.0;
	}
}