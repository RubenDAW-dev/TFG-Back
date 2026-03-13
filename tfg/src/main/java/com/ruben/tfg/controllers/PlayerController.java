package com.ruben.tfg.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruben.tfg.DTOs.PlayerDTO;
import com.ruben.tfg.DTOs.SearchItemDTO;
import com.ruben.tfg.entities.PlayerEntity;
import com.ruben.tfg.services.PlayerService;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService service;

    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        try {
            List<PlayerEntity> lista = service.getAll();
            if (lista.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            PlayerEntity player = service.getById(id);
            if (player == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Jugador no encontrado: " + id);
            return ResponseEntity.ok(player);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PlayerDTO player) {
        try {
            PlayerEntity creado = service.crear(player);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear jugador: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody PlayerEntity player) {
        try {
            PlayerEntity actualizado = service.update(player);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Jugador no encontrado: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            service.delete(id);
            return ResponseEntity.ok("Jugador eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Jugador no encontrado: " + e.getMessage());
        }
    }
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(defaultValue = "") String q) {
        try {
            List<SearchItemDTO> lista = service.search(q);
            System.out.println("Search query: '" + q + "' - Results found: " + lista.size());
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}