package com.ruben.tfg.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;
import com.ruben.tfg.entities.MatchEntity;
import com.ruben.tfg.entities.PlayerEntity;
import com.ruben.tfg.entities.PlayerMatchStatsEntity;
import com.ruben.tfg.entities.TeamEntity;
import com.ruben.tfg.entities.TeamMatchStatsEntity;
import com.ruben.tfg.repositories.MatchRepository;
import com.ruben.tfg.repositories.PlayerMatchStatsRepository;
import com.ruben.tfg.repositories.PlayerRepository;
import com.ruben.tfg.repositories.TeamMatchStatsRepository;
import com.ruben.tfg.repositories.TeamRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CSVDataLoader implements CommandLineRunner {

    private final TeamRepository teamRepo;
    private final PlayerRepository playerRepo;
    private final MatchRepository matchRepo;
    private final TeamMatchStatsRepository teamStatsRepo;
    private final PlayerMatchStatsRepository playerStatsRepo;

    @Override
    public void run(String... args) throws Exception {
        // Ejecuta siempre, pero con UPSERT: no duplica y actualiza lo que cambie
        loadTeams();
        loadPlayers();
        loadMatches();
        loadTeamMatchStats();
        loadPlayerMatchStats();
        updateCardsFromPlayerStats();
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
            t.setNombre(d[0]);                               // equipo
            t.setEstadio(d[1]);                              // estadio
            t.setCiudad(d[2]);                               // ciudad
            t.setCapacidad(parseIntOrNull(d[3]));            // capacidad (por si viene vacío)

            teamRepo.save(t); // insert o update
        }
    }

    // ---------------------------- JUGADORES ----------------------------
    private void loadPlayers() throws Exception {
        CSVReader reader = new CSVReader(
            new InputStreamReader(
                new ClassPathResource("csv/jugadores_laliga_ids_FINAL.csv").getInputStream()
            )
        );

        reader.readNext(); // cabecera

        // Nuevo orden de columnas: player_id, team_id, Player, Nation, Pos, Squad, Age, ...
        String[] d;
        while ((d = reader.readNext()) != null) {
            String playerId = d[0];
            String teamId   = d[1];

            PlayerEntity p = playerRepo.findById(playerId).orElseGet(PlayerEntity::new);
            p.setId(playerId);
            p.setNombre(d[2]);        // Player
            p.setNacionalidad(d[3]);  // Nation
            p.setPosicion(d[4]);      // Pos (ya viene bien aunque tenga "MF,FW")
            p.setEdad(parseAge(d[6])); // Age
            p.setTeam(teamRepo.findById(teamId).orElse(null));

            playerRepo.save(p);
        }
    }

    private Integer parseAge(String age) {
        try {
            if (age == null || age.isEmpty()) return null;
            // Formato esperado "21-269" -> me quedo con "21"
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

            // Claves foráneas
            m.setHomeTeam(teamRepo.findById(d[1]).orElse(null));
            m.setAwayTeam(teamRepo.findById(d[2]).orElse(null));

            // Datos
            m.setWk(parseIntOrNull(d[3]));
            m.setDay(d[4]);
            m.setDate(parseDateOrNull(d[5], df));
            m.setTime(emptyToNull(d[6]));
            m.setScore(emptyToNull(d[7]));

            // Attendance viene como "12403.0" -> quitamos el ".0"
            m.setAttendance(parseIntOrNull(safe(d[8]).replace(".0", "")));

            m.setVenue(emptyToNull(d[9]));
            m.setReferee(emptyToNull(d[10]));

            matchRepo.save(m); // insert o update
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

            // UPSERT por (match_id, team_id)
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

            teamStatsRepo.save(s); // insert o update
        }
    }

    // ---------------------------- PLAYER MATCH STATS ----------------------------
    private void loadPlayerMatchStats() throws Exception {
        CSVReader reader = new CSVReader(
            new InputStreamReader(
                new ClassPathResource("csv/PLAYER_STATS_FINAL.CSV").getInputStream()
            )
        );

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

            // UPSERT por (match_id, player_id)
            Optional<PlayerMatchStatsEntity> opt = playerStatsRepo.findByMatch_IdAndPlayer_Id(matchId, playerId);
            PlayerMatchStatsEntity s = opt.orElseGet(PlayerMatchStatsEntity::new);

            s.setMatch(match);
            s.setPlayer(player);

            Integer number = parseIntOrNull(safe(d[2]).replace(".0", ""));
            s.setNumber(number);
            s.setNation(d[3]);
            s.setPos(d[4]);
            // Te pediste: "siempre los dos primeros caracteres" para la edad en stats
            s.setAge(safe(d[5]).substring(0, 2));
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

            playerStatsRepo.save(s); // insert o update
        }
    }
    private void updateCardsFromPlayerStats() {
        List<TeamMatchStatsEntity> allTeamStats = teamStatsRepo.findAll();

        for (TeamMatchStatsEntity teamStat : allTeamStats) {
            Long matchId = teamStat.getMatch().getId();
            String teamId = teamStat.getTeam().getId();

            // Obtener todos los jugadores de ese partido
            List<PlayerMatchStatsEntity> playerStats = playerStatsRepo.findByMatch_Id(matchId);

            // Filtrar los que pertenecen a este equipo (via player -> team)
            int totalCards = playerStats.stream()
                .filter(ps -> ps.getPlayer().getTeam() != null
                           && ps.getPlayer().getTeam().getId().equals(teamId))
                .mapToInt(ps -> {
                    int yellow = ps.getYellowCards() != null ? ps.getYellowCards() : 0;
                    int red    = ps.getRedCards()    != null ? ps.getRedCards()    : 0;
                    return yellow + red;
                })
                .sum();

            teamStat.setCards(totalCards);
            teamStatsRepo.save(teamStat);
        }

        System.out.println("✔ Tarjetas actualizadas en TeamMatchStats");
    }
    // ---------------------------- HELPERS ----------------------------
    private BufferedReader getReader(String path) throws Exception {
        return new BufferedReader(new InputStreamReader(new ClassPathResource(path).getInputStream()));
    }

    private Integer parseIntOrNull(String s) {
        try {
            s = safe(s).trim();
            if (s.isEmpty()) return null;
            return Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
    }

    private String[] splitCSV(String line) {
        // separa por comas respetando comillas dobles
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
            if (s.isEmpty()) return null;
            return LocalDate.parse(s, df);
        } catch (Exception e) {
            return null;
        }
    }
}