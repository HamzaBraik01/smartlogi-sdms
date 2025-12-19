package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.LivreurDTO;
import com.smartlogi.sdms.service.interfaces.LivreurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/livreurs")
@Tag(name = "Livreurs", description = "API pour la gestion des livreurs")
@PreAuthorize("hasRole('MANAGER') or hasAuthority('LIVREUR_MANAGE')")
public class LivreurController {

    private final LivreurService livreurService;

    public LivreurController(LivreurService livreurService) {
        this.livreurService = livreurService;
    }

    @Operation(summary = "Créer un nouveau livreur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Driver created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data (validation failed, email already in use, or invalid zoneId)")
    })
    @PostMapping
    public ResponseEntity<LivreurDTO> createLivreur(@Valid @RequestBody LivreurDTO dto) {
        LivreurDTO savedDto = livreurService.save(dto);
        return ResponseEntity
                .created(URI.create("/api/v1/livreurs/" + savedDto.getId()))
                .body(savedDto);
    }

    @Operation(summary = "Récupérer un livreur par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver found"),
            @ApiResponse(responseCode = "404", description = "Driver not found with this ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LivreurDTO> getLivreurById(@Parameter(description = "ID (String/UUID) of the driver to search for") @PathVariable String id) {
        return ResponseEntity.ok(livreurService.findById(id));
    }

    @Operation(summary = "Récupérer la liste paginée de tous les livreurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated list of drivers")
    })
    @GetMapping
    public ResponseEntity<Page<LivreurDTO>> getAllLivreurs(@Parameter(description = "Pagination parameters (page, size, sort)") Pageable pageable) {
        return ResponseEntity.ok(livreurService.findAll(pageable));
    }

    @Operation(summary = "Mettre à jour un livreur existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data (validation failed, email already in use, or invalid zoneId)"),
            @ApiResponse(responseCode = "404", description = "Driver not found with this ID")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LivreurDTO> updateLivreur(@Parameter(description = "ID (String/UUID) of the driver to update") @PathVariable String id, @Valid @RequestBody LivreurDTO dto) {
        return ResponseEntity.ok(livreurService.update(id, dto));
    }

    @Operation(summary = "Supprimer un livreur par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Driver deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Driver not found with this ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLivreur(@Parameter(description = "ID (String/UUID) of the driver to delete") @PathVariable String id) {
        livreurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}