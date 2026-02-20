package com.ruben.tfg.controllers;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ruben.tfg.DTOs.MatchDTO;
import com.ruben.tfg.entities.MatchEntity;
import com.ruben.tfg.services.MatchService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService service;

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        try {
            List<MatchEntity> lista = service.getAll();
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            MatchEntity match = service.getById(id);
            if (match == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partido no encontrado: " + id);
            return ResponseEntity.ok(match);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody MatchDTO match) {
        try {
            service.update(match);
            return ResponseEntity.ok("Partido actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partido no encontrado: " + e.getMessage());
        }
    }

    @GetMapping("/week/{wk}")
    public ResponseEntity<?> getByWeek(@PathVariable Integer wk) {
        try {
            List<MatchEntity> lista = service.getByWeek(wk);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/home/{teamId}")
    public ResponseEntity<?> getByHomeTeam(@PathVariable String teamId) {
        try {
            List<MatchEntity> lista = service.getByHomeTeam(teamId);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/away/{teamId}")
    public ResponseEntity<?> getByAwayTeam(@PathVariable String teamId) {
        try {
            List<MatchEntity> lista = service.getByAwayTeam(teamId);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<?> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<MatchEntity> lista = service.getByDate(date);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}