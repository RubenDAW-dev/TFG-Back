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

    private void loadTeams() throws Exception {
        BufferedReader br = getReader("csv/equipos_final_ids.csv");
        br.readLine();

        String line;
        while ((line = br.readLine()) != null) {
            String[] d = line.split(",");

            String teamId = d[4];

            TeamEntity t = teamRepo.findById(teamId).orElseGet(TeamEntity::new);
            t.setId(teamId);
            t.setNombre(d[0]);
            t.setEstadio(d[1]);
            t.setCiudad(d[2]);
            t.setCapacidad(parseIntFlex(d[3]));

            teamRepo.save(t);
        }
    }

    private void loadPlayers() throws Exception {
        CSVReader reader = new CSVReader(
                new InputStreamReader(new ClassPathResource("csv/jugadores_laliga_ids_FINAL.csv").getInputStream()));

        reader.readNext();
        String[] d;

        while ((d = reader.readNext()) != null) {
            String playerId = d[0];
            String teamId = d[1];

            PlayerEntity p = playerRepo.findById(playerId).orElseGet(PlayerEntity::new);
            p.setId(playerId);
            p.setNombre(d[2]);
            p.setNacionalidad(d[3]);
            p.setPosicion(d[4]);
            p.setEdad(parseAge(d[6]));
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

    private void loadMatches() throws Exception {
        BufferedReader br = getReader("csv/PARTIDOS_FINAL.CSV");
        br.readLine();

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String line;
        while ((line = br.readLine()) != null) {
            String[] d = splitCSV(line);

            Long matchId = Long.parseLong(d[0]);

            MatchEntity m = matchRepo.findById(matchId).orElseGet(MatchEntity::new);
            m.setId(matchId);

            m.setHomeTeam(teamRepo.findById(d[1]).orElse(null));
            m.setAwayTeam(teamRepo.findById(d[2]).orElse(null));

            m.setWk(parseIntFlex(d[3]));
            m.setDay(d[4]);
            m.setDate(parseDateOrNull(d[5], df));
            m.setTime(emptyToNull(d[6]));
            m.setScore(emptyToNull(d[7]));
            m.setAttendance(parseIntFlex(safe(d[8]).replace(".0", "")));
            m.setVenue(emptyToNull(d[9]));
            m.setReferee(emptyToNull(d[10]));

            matchRepo.save(m);
        }
    }

    private void loadTeamMatchStats() throws Exception {
        BufferedReader br = getReader("csv/TEAM_MATCH_STATS_FINAL.csv");
        br.readLine();

        String line;
        while ((line = br.readLine()) != null) {
            String[] d = line.split(",");

            Long matchId = Long.parseLong(d[0]);
            String teamId = d[1];

            MatchEntity match = matchRepo.findById(matchId).orElse(null);
            TeamEntity team = teamRepo.findById(teamId).orElse(null);

            if (match == null || team == null)
                continue;

            Optional<TeamMatchStatsEntity> opt = teamStatsRepo.findByMatch_IdAndTeam_Id(matchId, teamId);
            TeamMatchStatsEntity s = opt.orElseGet(TeamMatchStatsEntity::new);

            s.setMatch(match);
            s.setTeam(team);
            s.setSide(d[2]);
            s.setPossession(parseIntFlex(d[3]));
            s.setShots_on_target(parseIntFlex(d[4]));
            s.setShots_total(parseIntFlex(d[5]));
            s.setSaves(parseIntFlex(d[6]));
            s.setCards(parseIntFlex(d[7]));

            teamStatsRepo.save(s);
        }
    }

    private void loadPlayerMatchStats() throws Exception {
        CSVReader reader = new CSVReader(
                new InputStreamReader(new ClassPathResource("csv/PLAYER_STATS_FINAL.CSV").getInputStream()));

        reader.readNext();
        String[] d;
        int row = 1;

        while ((d = reader.readNext()) != null) {
            row++;

            if (d.length != 22)
                continue;

            Long matchId = Long.parseLong(d[0]);
            String playerId = d[1];

            MatchEntity match = matchRepo.findById(matchId).orElse(null);
            if (match == null)
                continue;

            PlayerEntity player = playerRepo.findById(playerId).orElse(null);
            if (player == null)
                continue;

            Optional<PlayerMatchStatsEntity> opt = playerStatsRepo.findByMatch_IdAndPlayer_Id(matchId, playerId);
            PlayerMatchStatsEntity s = opt.orElseGet(PlayerMatchStatsEntity::new);

            s.setMatch(match);
            s.setPlayer(player);

            s.setNumber(parseIntFlex(safe(d[2]).replace(".0", "")));
            s.setNation(d[3]);
            s.setPos(d[4]);
            s.setAge(safe(d[5]).length() >= 2 ? safe(d[5]).substring(0, 2) : safe(d[5]));
            s.setMinutes(parseIntFlex(d[6]));
            s.setGls(parseIntFlex(d[7]));
            s.setAst(parseIntFlex(d[8]));
            s.setPk(parseIntFlex(d[9]));
            s.setPkAtt(parseIntFlex(d[10]));
            s.setShots(parseIntFlex(d[11]));
            s.setShotsOnTarget(parseIntFlex(d[12]));
            s.setYellowCards(parseIntFlex(d[13]));
            s.setRedCards(parseIntFlex(d[14]));
            s.setFoulsCommitted(parseIntFlex(d[15]));
            s.setFoulsDrawn(parseIntFlex(d[16]));
            s.setOffsides(parseIntFlex(d[17]));
            s.setCrosses(parseIntFlex(d[18]));
            s.setTacklesWon(parseIntFlex(d[19]));
            s.setInterceptions(parseIntFlex(d[20]));
            s.setOwnGoals(parseIntFlex(d[21]));

            playerStatsRepo.save(s);
        }
    }

    private void updateCardsFromPlayerStats() {
        List<TeamMatchStatsEntity> allTeamStats = teamStatsRepo.findAll();

        for (TeamMatchStatsEntity teamStat : allTeamStats) {
            Long matchId = teamStat.getMatch().getId();
            String teamId = teamStat.getTeam().getId();

            List<PlayerMatchStatsEntity> playerStats = playerStatsRepo.findByMatch_Id(matchId);

            int totalCards = playerStats.stream()
                    .filter(ps -> ps.getPlayer().getTeam() != null
                            && ps.getPlayer().getTeam().getId().equals(teamId))
                    .mapToInt(ps -> {
                        int yellow = ps.getYellowCards() != null ? ps.getYellowCards() : 0;
                        int red = ps.getRedCards() != null ? ps.getRedCards() : 0;
                        return yellow + red;
                    }).sum();

            teamStat.setCards(totalCards);
            teamStatsRepo.save(teamStat);
        }
    }

    private void loadPlayerSeasonStats() {
        List<PlayerMatchStatsEntity> allStats = playerStatsRepo.findAll();

        Map<String, List<PlayerMatchStatsEntity>> porJugador = allStats.stream()
                .filter(ps -> ps.getPlayer() != null)
                .collect(Collectors.groupingBy(ps -> ps.getPlayer().getId()));

        for (Map.Entry<String, List<PlayerMatchStatsEntity>> entry : porJugador.entrySet()) {
            String playerId = entry.getKey();
            List<PlayerMatchStatsEntity> stats = entry.getValue();

            transactionTemplate.execute(status -> {
                playerSeasonStatsRepo.deleteById(playerId);

                PlayerEntity player = entityManager.find(PlayerEntity.class, playerId);
                if (player == null)
                    return null;

                int partidos = stats.size();
                int minutos = sum(stats, PlayerMatchStatsEntity::getMinutes);
                int goles = sum(stats, PlayerMatchStatsEntity::getGls);
                int asistencias = sum(stats, PlayerMatchStatsEntity::getAst);
                int penMar = sum(stats, PlayerMatchStatsEntity::getPk);
                int penAtt = sum(stats, PlayerMatchStatsEntity::getPkAtt);
                int disparos = sum(stats, PlayerMatchStatsEntity::getShots);
                int disparosPuerta = sum(stats, PlayerMatchStatsEntity::getShotsOnTarget);
                int amarillas = sum(stats, PlayerMatchStatsEntity::getYellowCards);
                int rojas = sum(stats, PlayerMatchStatsEntity::getRedCards);
                int faltasCom = sum(stats, PlayerMatchStatsEntity::getFoulsCommitted);
                int faltasRec = sum(stats, PlayerMatchStatsEntity::getFoulsDrawn);
                int offsides = sum(stats, PlayerMatchStatsEntity::getOffsides);
                int centros = sum(stats, PlayerMatchStatsEntity::getCrosses);
                int entradas = sum(stats, PlayerMatchStatsEntity::getTacklesWon);
                int inter = sum(stats, PlayerMatchStatsEntity::getInterceptions);
                int autogoles = sum(stats, PlayerMatchStatsEntity::getOwnGoals);

                double goles90 = per90(goles, minutos);
                double ast90 = per90(asistencias, minutos);
                double sh90 = per90(disparos, minutos);
                double sot90 = per90(disparosPuerta, minutos);
                double y90 = per90(amarillas, minutos);
                double r90 = per90(rojas, minutos);
                double fcom90 = per90(faltasCom, minutos);
                double frec90 = per90(faltasRec, minutos);
                double off90 = per90(offsides, minutos);
                double cen90 = per90(centros, minutos);
                double ent90 = per90(entradas, minutos);
                double int90 = per90(inter, minutos);

                double precision = safeRatio(disparosPuerta, disparos);
                double penConv = safeRatio(penMar, penAtt);

                PlayerSeasonStatsEntity s = new PlayerSeasonStatsEntity();
                s.setPlayerId(playerId);
                s.setPlayer(player);

                s.setPartidos(partidos);
                s.setMinutos(minutos);
                s.setGoles(goles);
                s.setAsistencias(asistencias);
                s.setPenaltisMarcados(penMar);
                s.setPenaltisIntentados(penAtt);
                s.setDisparos(disparos);
                s.setDisparosPuerta(disparosPuerta);
                s.setAmarillas(amarillas);
                s.setRojas(rojas);
                s.setFaltasCometidas(faltasCom);
                s.setFaltasRecibidas(faltasRec);
                s.setFueraDeJuego(offsides);
                s.setCentros(centros);
                s.setEntradasGanadas(entradas);
                s.setIntercepciones(inter);
                s.setAutogoles(autogoles);

                s.setGolesPor90(goles90);
                s.setAsistenciasPor90(ast90);
                s.setDisparosPor90(sh90);
                s.setDisparosPuertaPor90(sot90);
                s.setAmarillasPor90(y90);
                s.setRojasPor90(r90);
                s.setFaltasCometidasPor90(fcom90);
                s.setFaltasRecibidasPor90(frec90);
                s.setFueraDeJuegoPor90(off90);
                s.setCentrosPor90(cen90);
                s.setEntradasGanadasPor90(ent90);
                s.setIntercepcionesPor90(int90);

                s.setPrecisionTiro(precision);
                s.setConversionPenalti(penConv);

                entityManager.persist(s);
                return null;
            });
        }
    }

    private void loadTeamSeasonStats() {
        List<TeamMatchStatsEntity> allStats = teamStatsRepo.findAll();

        Map<String, List<TeamMatchStatsEntity>> porEquipo = allStats.stream()
                .filter(ts -> ts.getTeam() != null)
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

                int gf = 0, gc = 0, v = 0, e = 0, d = 0;

                for (TeamMatchStatsEntity ts : stats) {
                    String raw = ts.getMatch().getScore();

                    if (raw != null) {
                        String score = raw.trim().replace("–", "-").replace("—", "-").replace("−", "-")
                                .replaceAll("\\s+", "");

                        if (score.matches("\\d+-\\d+")) {
                            String[] p = score.split("-");
                            int home = Integer.parseInt(p[0]);
                            int away = Integer.parseInt(p[1]);

                            int gF = ts.getSide().equals("HOME") ? home : away;
                            int gC = ts.getSide().equals("HOME") ? away : home;

                            gf += gF;
                            gc += gC;

                            if (gF > gC)
                                v++;
                            else if (gF == gC)
                                e++;
                            else
                                d++;
                        }
                    }
                }

                s.setGoles_favor(gf);
                s.setGoles_contra(gc);
                s.setVictorias(v);
                s.setEmpates(e);
                s.setDerrotas(d);
                s.setPuntos(v * 3 + e);

                s.setPosesion_media(avg(stats, x -> x.getPossession() != null ? x.getPossession().doubleValue() : null));
                s.setTiros_media(avg(stats, x -> x.getShots_total() != null ? x.getShots_total().doubleValue() : null));
                s.setTiros_puerta_media(
                        avg(stats, x -> x.getShots_on_target() != null ? x.getShots_on_target().doubleValue() : null));
                s.setParadas_media(avg(stats, x -> x.getSaves() != null ? x.getSaves().doubleValue() : null));
                s.setTarjetas_media(avg(stats, x -> x.getCards() != null ? x.getCards().doubleValue() : null));

                entityManager.persist(s);
                return null;
            });
        }
    }

    private Double avg(List<TeamMatchStatsEntity> list,
            java.util.function.Function<TeamMatchStatsEntity, Double> getter) {
        return round2(list.stream().map(getter).filter(v -> v != null).mapToDouble(Double::doubleValue).average()
                .orElse(0.0));
    }

    private BufferedReader getReader(String path) throws Exception {
        return new BufferedReader(new InputStreamReader(new ClassPathResource(path).getInputStream()));
    }

    private Integer parseIntFlex(String s) {
        try {
            s = safe(s).trim();
            if (s.isEmpty())
                return null;
            if (s.contains("."))
                return (int) Double.parseDouble(s);
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

    private int sum(List<PlayerMatchStatsEntity> stats,
            java.util.function.Function<PlayerMatchStatsEntity, Integer> getter) {
        int total = 0;
        for (PlayerMatchStatsEntity s : stats) {
            Integer v = getter.apply(s);
            if (v != null)
                total += v;
        }
        return total;
    }

    private double per90(int total, int minutos) {
        if (minutos <= 0)
            return 0.0;
        double p90 = minutos / 90.0;
        return round2(total / p90);
    }

    private double safeRatio(int n, int d) {
        if (d <= 0)
            return 0.0;
        return round4(n / (double) d);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private double round4(double v) {
        return Math.round(v * 10000.0) / 10000.0;
    }
}