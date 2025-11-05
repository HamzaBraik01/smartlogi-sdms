package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.ZoneDTO;
import com.smartlogi.sdms.service.interfaces.ZoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/zones")
@Tag(name = "Zones", description = "API pour la gestion des zones géographiques")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @Operation(summary = "Créer une nouvelle zone")
    @PostMapping
    public ResponseEntity<ZoneDTO> createZone(@Valid @RequestBody ZoneDTO zoneDTO) {
        ZoneDTO savedZone = zoneService.save(zoneDTO);
        return ResponseEntity
                .created(URI.create("/api/v1/zones/" + savedZone.getId()))
                .body(savedZone);
    }

    @Operation(summary = "Récupérer une zone par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<ZoneDTO> getZoneById(@PathVariable String id) {
        ZoneDTO zone = zoneService.findById(id);
        return ResponseEntity.ok(zone);
    }

    @Operation(summary = "Récupérer la liste paginée de toutes les zones")
    @GetMapping
    public ResponseEntity<Page<ZoneDTO>> getAllZones(Pageable pageable) {
        Page<ZoneDTO> zones = zoneService.findAll(pageable);
        return ResponseEntity.ok(zones);
    }

    @Operation(summary = "Mettre à jour une zone existante")
    @PutMapping("/{id}")
    public ResponseEntity<ZoneDTO> updateZone(@PathVariable String id, @Valid @RequestBody ZoneDTO zoneDTO) {
        ZoneDTO updatedZone = zoneService.update(id, zoneDTO);
        return ResponseEntity.ok(updatedZone);
    }

    @Operation(summary = "Supprimer une zone par son ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable String id) {
        zoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}