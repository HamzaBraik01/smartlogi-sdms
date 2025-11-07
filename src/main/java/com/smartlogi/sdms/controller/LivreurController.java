package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.LivreurDTO;
import com.smartlogi.sdms.service.interfaces.LivreurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/livreurs")
@Tag(name = "Livreurs", description = "API pour la gestion des livreurs")
public class LivreurController {

    private final LivreurService livreurService;

    public LivreurController(LivreurService livreurService) {
        this.livreurService = livreurService;
    }

    @Operation(summary = "Créer un nouveau livreur")
    @PostMapping
    public ResponseEntity<LivreurDTO> createLivreur(@Valid @RequestBody LivreurDTO dto) {
        LivreurDTO savedDto = livreurService.save(dto);
        return ResponseEntity
                .created(URI.create("/api/v1/livreurs/" + savedDto.getId()))
                .body(savedDto);
    }

    @Operation(summary = "Récupérer un livreur par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<LivreurDTO> getLivreurById(@PathVariable String id) {
        return ResponseEntity.ok(livreurService.findById(id));
    }

    @Operation(summary = "Récupérer la liste paginée de tous les livreurs")
    @GetMapping
    public ResponseEntity<Page<LivreurDTO>> getAllLivreurs(Pageable pageable) {
        return ResponseEntity.ok(livreurService.findAll(pageable));
    }

    @Operation(summary = "Mettre à jour un livreur existant")
    @PutMapping("/{id}")
    public ResponseEntity<LivreurDTO> updateLivreur(@PathVariable String id, @Valid @RequestBody LivreurDTO dto) {
        return ResponseEntity.ok(livreurService.update(id, dto));
    }

    @Operation(summary = "Supprimer un livreur par son ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLivreur(@PathVariable String id) {
        livreurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}