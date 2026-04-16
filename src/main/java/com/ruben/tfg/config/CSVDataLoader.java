package com.ruben.tfg.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DataLoaderService - Maneja TODO:
 * 1. Carga inicial de datos (equipos, jugadores, partidos, stats)
 * 2. Actualización de datos existentes
 * 3. Inserción de nuevos datos
 * 4. Recalcular season stats automáticamente
 * 
 * Lógica: Si existe → actualiza, si no existe → crea, si no cambió → no hace nada
 */

@RequiredArgsConstructor
@Slf4j
public class CSVDataLoader implements CommandLineRunner {

    private final TeamRepository teamRepo;
    private final PlayerRepository playerRepo;
    private final MatchRepository matchRepo;
    private final TeamMatchStatsRepository teamStatsRepo;
    private final PlayerMatchStatsRepository playerStatsRepo;
    private final PlayerSeasonStatsRepository playerSeasonStatsRepo;
    private final TeamSeasonStatsRepository teamSeasonStatsRepo;
    private final TransactionTemplate transactionTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("========== INICIANDO CARGA DE DATOS ==========");

            // 1. EQUIPOS (carga o actualiza)
            loadOrUpdateTeams();

            // 2. ESCUDOS (carga o actualiza)
            loadOrUpdateTeamShields();

            // 3. JUGADORES (carga o actualiza)
            loadOrUpdatePlayers();

            // 4. PARTIDOS (carga o actualiza)
            loadOrUpdateMatches();

            // 5. TEAM MATCH STATS (carga o actualiza)
            loadOrUpdateTeamMatchStats();

            // 6. PLAYER MATCH STATS (carga o actualiza)
            loadOrUpdatePlayerMatchStats();

            // 7. RECALCULAR TARJETAS DE EQUIPOS
            updateCardsFromPlayerStats();

            // 8. RECALCULAR SEASON STATS (SIEMPRE, porque pueden haber cambios)
            recalculateAllPlayerSeasonStats();
            recalculateAllTeamSeasonStats();

            log.info("========== CARGA DE DATOS COMPLETADA ==========");
        } catch (Exception e) {
            log.error("❌ Error durante la carga de datos", e);
            throw e;
        }
    }

    // ========================
    // EQUIPOS - Carga o Actualiza
    // ========================
    private void loadOrUpdateTeams() throws Exception {
        log.info("📂 Procesando equipos...");
        BufferedReader br = getReader("csv/equipos_final_ids.csv");
        br.readLine(); // Saltar encabezado

        int created = 0, updated = 0;
        String line;

        while ((line = br.readLine()) != null) {
            String[] d = line.split(",");
            String teamId = d[4];

            TeamEntity existingTeam = teamRepo.findById(teamId).orElse(null);

            if (existingTeam == null) {
                // ✅ CREAR NUEVO
                TeamEntity t = new TeamEntity();
                t.setId(teamId);
                t.setNombre(d[0]);
                t.setEstadio(d[1]);
                t.setCiudad(d[2]);
                t.setCapacidad(parseIntFlex(d[3]));
                teamRepo.save(t);
                created++;
            } else {
                // ✅ ACTUALIZAR EXISTENTE (por si cambió nombre, etc)
                existingTeam.setNombre(d[0]);
                existingTeam.setEstadio(d[1]);
                existingTeam.setCiudad(d[2]);
                existingTeam.setCapacidad(parseIntFlex(d[3]));
                teamRepo.save(existingTeam);
                updated++;
            }
        }

        log.info("✅ Equipos: {} creados, {} actualizados", created, updated);
    }

    // ========================
    // ESCUDOS - Carga o Actualiza
    // ========================
    private void loadOrUpdateTeamShields() throws Exception {
        log.info("📂 Procesando escudos...");
        CSVReader reader = new CSVReader(
                new InputStreamReader(new ClassPathResource("csv/TEAMS_IMAGES.csv").getInputStream()));

        reader.readNext();
        String[] d;
        int updated = 0;

        while ((d = reader.readNext()) != null) {
            if (d.length >= 2) {
                String teamName = d[0].trim();
                String shieldUrl = d[1].trim();

                List<TeamEntity> teams = teamRepo.findAll().stream()
                        .filter(t -> t.getNombre().equalsIgnoreCase(teamName))
                        .toList();

                if (!teams.isEmpty()) {
                    TeamEntity team = teams.get(0);
                    if (!shieldUrl.equals(team.getEscudo())) {
                        team.setEscudo(shieldUrl);
                        teamRepo.save(team);
                        updated++;
                    }
                }
            }
        }

        log.info("✅ Escudos: {} actualizados", updated);
    }

    // ========================
    // JUGADORES - Carga o Actualiza
    // ========================
    private void loadOrUpdatePlayers() throws Exception {
        log.info("📂 Procesando jugadores...");
        Map<String, String> playerImages = loadPlayerImages();

        CSVReader reader = new CSVReader(
                new InputStreamReader(new ClassPathResource("csv/jugadores_laliga_ids_FINAL.csv").getInputStream()));

        reader.readNext();
        String[] d;
        int created = 0, updated = 0;

        while ((d = reader.readNext()) != null) {
            String playerId = d[0];
            String teamId = d[1];
            String playerName = d[2];

            PlayerEntity existingPlayer = playerRepo.findById(playerId).orElse(null);

            if (existingPlayer == null) {
                // ✅ CREAR NUEVO
                PlayerEntity p = new PlayerEntity();
                p.setId(playerId);
                p.setNombre(playerName);
                p.setNacionalidad(d[3]);
                p.setPosicion(d[4]);
                p.setEdad(parseAge(d[6]));
                p.setTeam(teamRepo.findById(teamId).orElse(null));

                String imageUrl = playerImages.get(playerName);
                if (imageUrl != null) {
                    p.setImageUrl(imageUrl);
                }

                playerRepo.save(p);
                created++;
            } else {
                // ✅ ACTUALIZAR EXISTENTE
                existingPlayer.setNombre(playerName);
                existingPlayer.setNacionalidad(d[3]);
                existingPlayer.setPosicion(d[4]);
                existingPlayer.setEdad(parseAge(d[6]));
                existingPlayer.setTeam(teamRepo.findById(teamId).orElse(null));

                String imageUrl = playerImages.get(playerName);
                if (imageUrl != null && !imageUrl.equals(existingPlayer.getImageUrl())) {
                    existingPlayer.setImageUrl(imageUrl);
                }

                playerRepo.save(existingPlayer);
                updated++;
            }
        }

        log.info("✅ Jugadores: {} creados, {} actualizados", created, updated);
    }

    private Map<String, String> loadPlayerImages() throws Exception {
        Map<String, String> images = new java.util.HashMap<>();

        CSVReader reader = new CSVReader(
                new InputStreamReader(new ClassPathResource("csv/players_images_la_liga.csv").getInputStream()));

        reader.readNext();
        String[] d;

        while ((d = reader.readNext()) != null) {
            if (d.length >= 3) {
                images.put(d[0], d[2]);
            }
        }

        return images;
    }

    // ========================
    // PARTIDOS - Carga o Actualiza
    // ========================
    private void loadOrUpdateMatches() throws Exception {
        log.info("📂 Procesando partidos...");
        BufferedReader br = getReader("csv/PARTIDOS_FINAL.CSV");
        br.readLine();

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int created = 0, updated = 0, deleted = 0;
        String line;

        while ((line = br.readLine()) != null) {
            String[] d = splitCSV(line);
            Long matchId = Long.parseLong(d[0]);
            String homeTeamId = d[1];
            String awayTeamId = d[2];
            Integer wk = parseIntFlex(d[3]);

            MatchEntity existingMatch = matchRepo.findById(matchId).orElse(null);
            
            if (existingMatch == null && wk != null) {
                List<MatchEntity> possibleDuplicates = matchRepo.findByHomeTeam_IdAndAwayTeam_IdAndWk(
                    homeTeamId, awayTeamId, wk
                );
                
                if (!possibleDuplicates.isEmpty()) {
                    existingMatch = possibleDuplicates.get(0);
                    
                    if (!existingMatch.getId().equals(matchId)) {
                        log.info("🔄 Partido actualizado con nuevo ID: {} -> {} ({}vs{})", 
                            existingMatch.getId(), matchId, homeTeamId, awayTeamId);
                        
                        Long oldId = existingMatch.getId();
                        
                        existingMatch = null; // Forzar creación con nuevo ID
                        
                        matchRepo.deleteById(oldId);
                        deleted++;
                    }
                }
            }

            TeamEntity homeTeam = teamRepo.findById(homeTeamId).orElse(null);
            TeamEntity awayTeam = teamRepo.findById(awayTeamId).orElse(null);

            if (existingMatch == null) {
                // ✅ CREAR NUEVO
                MatchEntity m = new MatchEntity();
                m.setId(matchId);
                m.setHomeTeam(homeTeam);
                m.setAwayTeam(awayTeam);
                m.setWk(wk);
                m.setDay(d[4]);
                m.setDate(parseDateOrNull(d[5], df));
                m.setTime(emptyToNull(d[6]));
                m.setScore(emptyToNull(d[7]));
                m.setAttendance(parseIntFlex(safe(d[8]).replace(".0", "")));
                m.setVenue(emptyToNull(d[9]));
                m.setReferee(emptyToNull(d[10]));
                matchRepo.save(m);
                created++;
            } else {
                // ✅ ACTUALIZAR EXISTENTE (especialmente la hora)
                existingMatch.setHomeTeam(homeTeam);
                existingMatch.setAwayTeam(awayTeam);
                existingMatch.setWk(wk);
                existingMatch.setDay(d[4]);
                existingMatch.setDate(parseDateOrNull(d[5], df));
                existingMatch.setTime(emptyToNull(d[6])); // 🕐 Actualizar hora
                existingMatch.setScore(emptyToNull(d[7]));
                existingMatch.setAttendance(parseIntFlex(safe(d[8]).replace(".0", "")));
                existingMatch.setVenue(emptyToNull(d[9]));
                existingMatch.setReferee(emptyToNull(d[10]));
                matchRepo.save(existingMatch);
                updated++;
            }
        }

        log.info("✅ Partidos: {} creados, {} actualizados, {} duplicados eliminados", created, updated, deleted);
    }

    // ========================
    // TEAM MATCH STATS - Carga o Actualiza
    // ========================
    private void loadOrUpdateTeamMatchStats() throws Exception {
        log.info("📂 Procesando team match stats...");
        BufferedReader br = getReader("csv/TEAM_MATCH_STATS_FINAL.csv");
        br.readLine();

        int created = 0, updated = 0;
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

            if (opt.isEmpty()) {
                // ✅ CREAR NUEVO
                TeamMatchStatsEntity s = new TeamMatchStatsEntity();
                s.setMatch(match);
                s.setTeam(team);
                s.setSide(d[2]);
                s.setPossession(parseIntFlex(d[3]));
                s.setShots_on_target(parseIntFlex(d[4]));
                s.setShots_total(parseIntFlex(d[5]));
                s.setSaves(parseIntFlex(d[6]));
                s.setCards(parseIntFlex(d[7]));
                teamStatsRepo.save(s);
                created++;
            } else {
                // ✅ ACTUALIZAR EXISTENTE
                TeamMatchStatsEntity s = opt.get();
                s.setSide(d[2]);
                s.setPossession(parseIntFlex(d[3]));
                s.setShots_on_target(parseIntFlex(d[4]));
                s.setShots_total(parseIntFlex(d[5]));
                s.setSaves(parseIntFlex(d[6]));
                s.setCards(parseIntFlex(d[7]));
                teamStatsRepo.save(s);
                updated++;
            }
        }

        log.info("✅ Team Match Stats: {} creados, {} actualizados", created, updated);
    }

    // ========================
    // PLAYER MATCH STATS - Carga o Actualiza
    // ========================
    private void loadOrUpdatePlayerMatchStats() throws Exception {
        log.info("📂 Procesando player match stats...");
        CSVReader reader = new CSVReader(
                new InputStreamReader(new ClassPathResource("csv/PLAYER_STATS_FINAL.CSV").getInputStream()));

        reader.readNext();
        String[] d;
        int created = 0, updated = 0;

        while ((d = reader.readNext()) != null) {
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

            if (opt.isEmpty()) {
                // ✅ CREAR NUEVO
                PlayerMatchStatsEntity s = new PlayerMatchStatsEntity();
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
                created++;
            } else {
                // ✅ ACTUALIZAR EXISTENTE
                PlayerMatchStatsEntity s = opt.get();
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
                updated++;
            }
        }

        log.info("✅ Player Match Stats: {} creados, {} actualizados", created, updated);
    }

    // ========================
    // ACTUALIZAR TARJETAS (desde player stats)
    // ========================
    private void updateCardsFromPlayerStats() {
        log.info("📂 Recalculando tarjetas de equipos...");
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

            if (teamStat.getCards() == null || !teamStat.getCards().equals(totalCards)) {
                teamStat.setCards(totalCards);
                teamStatsRepo.save(teamStat);
            }
        }

        log.info("✅ Tarjetas actualizadas");
    }

    // ========================
    // RECALCULAR SEASON STATS (TODOS LOS JUGADORES)
    // ========================
    private void recalculateAllPlayerSeasonStats() {
        log.info("📂 Recalculando season stats de jugadores...");
        List<PlayerEntity> allPlayers = playerRepo.findAll();

        for (PlayerEntity player : allPlayers) {
            recalculatePlayerSeasonStats(player.getId());
        }

        log.info("✅ Season stats de jugadores actualizados");
    }

    private void recalculatePlayerSeasonStats(String playerId) {
        transactionTemplate.execute(status -> {
            List<PlayerMatchStatsEntity> stats = playerStatsRepo.findByPlayerId(playerId);

            if (stats.isEmpty()) {
                playerSeasonStatsRepo.deleteById(playerId);
                return null;
            }

            PlayerEntity player = entityManager.find(PlayerEntity.class, playerId);
            if (player == null)
                return null;

            // Calcular agregados
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

            // Por 90 minutos
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

            // Ratios
            double precision = safeRatio(disparosPuerta, disparos);
            double penConv = safeRatio(penMar, penAtt);

            PlayerSeasonStatsEntity s = playerSeasonStatsRepo.findById(playerId)
                    .orElseGet(() -> new PlayerSeasonStatsEntity());

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

            playerSeasonStatsRepo.save(s);
            return null;
        });
    }

    // ========================
    // RECALCULAR SEASON STATS (TODOS LOS EQUIPOS)
    // ========================
    private void recalculateAllTeamSeasonStats() {
        log.info("📂 Recalculando season stats de equipos...");
        List<TeamEntity> allTeams = teamRepo.findAll();

        for (TeamEntity team : allTeams) {
            recalculateTeamSeasonStats(team.getId());
        }

        log.info("✅ Season stats de equipos actualizados");
    }

    private void recalculateTeamSeasonStats(String teamId) {
        transactionTemplate.execute(status -> {
            List<TeamMatchStatsEntity> stats = teamStatsRepo.findByTeam_Id(teamId);

            if (stats.isEmpty()) {
                teamSeasonStatsRepo.deleteById(teamId);
                return null;
            }

            TeamEntity team = entityManager.find(TeamEntity.class, teamId);
            if (team == null)
                return null;

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

            TeamSeasonStatsEntity s = teamSeasonStatsRepo.findById(teamId)
                    .orElseGet(() -> new TeamSeasonStatsEntity());

            s.setTeamId(teamId);
            s.setTeam(team);
            s.setPartidos(stats.size());
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

            teamSeasonStatsRepo.save(s);
            return null;
        });
    }

    // ========================
    // UTILIDADES
    // ========================
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

    private Integer parseAge(String age) {
        try {
            if (age == null || age.isEmpty())
                return null;
            return Integer.parseInt(age.split("-")[0]);
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

    private double avg(List<TeamMatchStatsEntity> list,
            java.util.function.Function<TeamMatchStatsEntity, Double> getter) {
        return round2(list.stream().map(getter).filter(v -> v != null).mapToDouble(Double::doubleValue).average()
                .orElse(0.0));
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private double round4(double v) {
        return Math.round(v * 10000.0) / 10000.0;
    }
}