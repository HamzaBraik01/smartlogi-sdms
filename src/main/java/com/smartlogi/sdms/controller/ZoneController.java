package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.ZoneDTO;
import com.smartlogi.sdms.service.interfaces.ZoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Zone créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de zone invalides (ex: nom manquant ou validation échouée)")
    })
    @PostMapping
    public ResponseEntity<ZoneDTO> createZone(@Valid @RequestBody ZoneDTO zoneDTO) {
        ZoneDTO savedZone = zoneService.save(zoneDTO);
        return ResponseEntity
                .created(URI.create("/api/v1/zones/" + savedZone.getId()))
                .body(savedZone);
    }

    @Operation(summary = "Récupérer une zone par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zone trouvée"),
            @ApiResponse(responseCode = "404", description = "Zone non trouvée avec cet ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ZoneDTO> getZoneById(@Parameter(description = "ID (String/UUID) de la zone à rechercher") @PathVariable String id) {
        ZoneDTO zone = zoneService.findById(id);
        return ResponseEntity.ok(zone);
    }

    @Operation(summary = "Récupérer la liste paginée de toutes les zones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste paginée des zones (peut être vide)")
    })
    @GetMapping
    public ResponseEntity<Page<ZoneDTO>> getAllZones(@Parameter(description = "Paramètres de pagination (page, size, sort)") Pageable pageable) {
        Page<ZoneDTO> zones = zoneService.findAll(pageable);
        return ResponseEntity.ok(zones);
    }

    @Operation(summary = "Mettre à jour une zone existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zone mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de zone invalides"),
            @ApiResponse(responseCode = "404", description = "Zone non trouvée avec cet ID")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ZoneDTO> updateZone(@Parameter(description = "ID (String/UUID) de la zone à mettre à jour") @PathVariable String id, @Valid @RequestBody ZoneDTO zoneDTO) {
        ZoneDTO updatedZone = zoneService.update(id, zoneDTO);
        return ResponseEntity.ok(updatedZone);
    }

    @Operation(summary = "Supprimer une zone par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Zone supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Zone non trouvée avec cet ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@Parameter(description = "ID (String/UUID) de la zone à supprimer") @PathVariable String id) {
        zoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}