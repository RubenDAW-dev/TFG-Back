package com.ruben.tfg.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
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

import com.ruben.tfg.DTOs.UsuarioCreateDTO;
import com.ruben.tfg.DTOs.UsuarioDTO;
import com.ruben.tfg.DTOs.UsuarioPasswordChangeDTO;
import com.ruben.tfg.DTOs.UsuarioUpdateDTO;
import com.ruben.tfg.services.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping("/list")
    public ResponseEntity<Page<UsuarioDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir,
            @RequestParam(required = false, name = "q") String qEmail
    ) {
        return ResponseEntity.ok(service.list(page, size, sort, dir, qEmail));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Integer id) {
        return service.get(id).<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody UsuarioCreateDTO dto) {
        try {
            return ResponseEntity.ok(service.create(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("El email ya existe");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody UsuarioUpdateDTO dto) {
        try {
            return service.update(id, dto)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("El email ya existe");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        boolean ok = service.delete(id);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable Integer id, @Valid @RequestBody UsuarioPasswordChangeDTO dto) {
        boolean ok = service.changePassword(id, dto);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
}