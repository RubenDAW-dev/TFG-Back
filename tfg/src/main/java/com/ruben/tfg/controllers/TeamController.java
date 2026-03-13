package com.ruben.tfg.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruben.tfg.DTOs.SearchItemDTO;
import com.ruben.tfg.entities.TeamEntity;
import com.ruben.tfg.services.TeamService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService service;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        try {
            List<TeamEntity> lista = service.getAll();
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            TeamEntity team = service.getById(id);
            if (team == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Equipo no encontrado: " + id);
            return ResponseEntity.ok(team);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody TeamEntity team) {
        try {
            TeamEntity actualizado = service.update(team);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Equipo no encontrado: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            service.delete(id);
            return ResponseEntity.ok("Equipo eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Equipo no encontrado: " + e.getMessage());
        }
    }
 // Añadir en TeamController — nuevo endpoint de búsqueda:

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(defaultValue = "") String q) {
        try {
            List<SearchItemDTO> lista = service.search(q);
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

}