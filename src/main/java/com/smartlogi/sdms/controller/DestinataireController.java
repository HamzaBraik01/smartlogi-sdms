package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.DestinataireDTO;
import com.smartlogi.sdms.service.interfaces.DestinataireService;
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
@RequestMapping("/api/v1/destinataires")
@Tag(name = "Destinataires", description = "API pour la gestion des destinataires de colis")
public class DestinataireController {

    private final DestinataireService destinataireService;

    public DestinataireController(DestinataireService destinataireService) {
        this.destinataireService = destinataireService;
    }

    @Operation(summary = "Créer un nouveau destinataire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Destinataire créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée ou email déjà utilisé)")
    })
    @PreAuthorize("hasAnyRole('MANAGER', 'CLIENT') or hasAuthority('USER_MANAGE')")
    @PostMapping
    public ResponseEntity<DestinataireDTO> createDestinataire(@Valid @RequestBody DestinataireDTO dto) {
        DestinataireDTO savedDto = destinataireService.save(dto);
        return ResponseEntity
                .created(URI.create("/api/v1/destinataires/" + savedDto.getId()))
                .body(savedDto);
    }

    @Operation(summary = "Récupérer un destinataire par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Destinataire trouvé"),
            @ApiResponse(responseCode = "404", description = "Destinataire non trouvé avec cet ID")
    })
    @PreAuthorize("hasRole('MANAGER') or @colisSecurityService.isCurrentUser(#id, authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<DestinataireDTO> getDestinataireById(@Parameter(description = "ID (String/UUID) du destinataire à rechercher") @PathVariable String id) {
        return ResponseEntity.ok(destinataireService.findById(id));
    }

    @Operation(summary = "Récupérer la liste paginée de tous les destinataires")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste paginée des destinataires")
    })
    @PreAuthorize("hasRole('MANAGER') or hasAuthority('USER_MANAGE')")
    @GetMapping
    public ResponseEntity<Page<DestinataireDTO>> getAllDestinataires(@Parameter(description = "Paramètres de pagination (page, size, sort)") Pageable pageable) {
        return ResponseEntity.ok(destinataireService.findAll(pageable));
    }

    @Operation(summary = "Mettre à jour un destinataire existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Destinataire mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée ou email déjà utilisé)"),
            @ApiResponse(responseCode = "404", description = "Destinataire non trouvé avec cet ID")
    })
    @PreAuthorize("hasRole('MANAGER') or @colisSecurityService.isCurrentUser(#id, authentication)")
    @PutMapping("/{id}")
    public ResponseEntity<DestinataireDTO> updateDestinataire(@Parameter(description = "ID (String/UUID) du destinataire à mettre à jour") @PathVariable String id, @Valid @RequestBody DestinataireDTO dto) {
        return ResponseEntity.ok(destinataireService.update(id, dto));
    }

    @Operation(summary = "Supprimer un destinataire par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Destinataire supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Destinataire non trouvé avec cet ID")
    })
    @PreAuthorize("hasRole('MANAGER') or hasAuthority('USER_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDestinataire(@Parameter(description = "ID (String/UUID) du destinataire à supprimer") @PathVariable String id) {
        destinataireService.delete(id);
        return ResponseEntity.noContent().build();
    }
}