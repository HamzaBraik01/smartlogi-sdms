package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.ClientExpediteurDTO;
import com.smartlogi.sdms.service.interfaces.ClientExpediteurService;
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
@RequestMapping("/api/v1/client-expediteurs")
@Tag(name = "Clients Expéditeurs", description = "API pour la gestion des clients expéditeurs")
public class ClientExpediteurController {

    private final ClientExpediteurService clientExpediteurService;

    public ClientExpediteurController(ClientExpediteurService clientExpediteurService) {
        this.clientExpediteurService = clientExpediteurService;
    }

    @Operation(summary = "Créer un nouveau client expéditeur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée ou email déjà utilisé)")
    })
    @PreAuthorize("hasRole('MANAGER') or hasAuthority('USER_MANAGE')")
    @PostMapping
    public ResponseEntity<ClientExpediteurDTO> createClientExpediteur(@Valid @RequestBody ClientExpediteurDTO dto) {
        ClientExpediteurDTO savedDto = clientExpediteurService.save(dto);
        return ResponseEntity
                .created(URI.create("/api/v1/client-expediteurs/" + savedDto.getId()))
                .body(savedDto);
    }

    @Operation(summary = "Récupérer un client expéditeur par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client trouvé"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé avec cet ID")
    })
    @PreAuthorize("hasRole('MANAGER') or @colisSecurityService.isCurrentUser(#id, authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<ClientExpediteurDTO> getClientExpediteurById(@Parameter(description = "ID (String/UUID) du client à rechercher") @PathVariable String id) {
        return ResponseEntity.ok(clientExpediteurService.findById(id));
    }

    @Operation(summary = "Récupérer la liste paginée de tous les clients expéditeurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste paginée des clients")
    })
    @PreAuthorize("hasRole('MANAGER') or hasAuthority('USER_MANAGE')")
    @GetMapping
    public ResponseEntity<Page<ClientExpediteurDTO>> getAllClientExpediteurs(@Parameter(description = "Paramètres de pagination (page, size, sort)") Pageable pageable) {
        return ResponseEntity.ok(clientExpediteurService.findAll(pageable));
    }

    @Operation(summary = "Mettre à jour un client expéditeur existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée ou email déjà utilisé)"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé avec cet ID")
    })
    @PreAuthorize("hasRole('MANAGER') or @colisSecurityService.isCurrentUser(#id, authentication)")
    @PutMapping("/{id}")
    public ResponseEntity<ClientExpediteurDTO> updateClientExpediteur(@Parameter(description = "ID (String/UUID) du client à mettre à jour") @PathVariable String id, @Valid @RequestBody ClientExpediteurDTO dto) {
        return ResponseEntity.ok(clientExpediteurService.update(id, dto));
    }

    @Operation(summary = "Supprimer un client expéditeur par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé avec cet ID")
    })
    @PreAuthorize("hasRole('MANAGER') or hasAuthority('USER_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientExpediteur(@Parameter(description = "ID (String/UUID) du client à supprimer") @PathVariable String id) {
        clientExpediteurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}