package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.ClientExpediteurDTO;
import com.smartlogi.sdms.service.interfaces.ClientExpediteurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    @PostMapping
    public ResponseEntity<ClientExpediteurDTO> createClientExpediteur(@Valid @RequestBody ClientExpediteurDTO dto) {
        ClientExpediteurDTO savedDto = clientExpediteurService.save(dto);
        return ResponseEntity
                .created(URI.create("/api/v1/client-expediteurs/" + savedDto.getId()))
                .body(savedDto);
    }

    @Operation(summary = "Récupérer un client expéditeur par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<ClientExpediteurDTO> getClientExpediteurById(@PathVariable String id) {
        return ResponseEntity.ok(clientExpediteurService.findById(id));
    }

    @Operation(summary = "Récupérer la liste paginée de tous les clients expéditeurs")
    @GetMapping
    public ResponseEntity<Page<ClientExpediteurDTO>> getAllClientExpediteurs(Pageable pageable) {
        return ResponseEntity.ok(clientExpediteurService.findAll(pageable));
    }

    @Operation(summary = "Mettre à jour un client expéditeur existant")
    @PutMapping("/{id}")
    public ResponseEntity<ClientExpediteurDTO> updateClientExpediteur(@PathVariable String id, @Valid @RequestBody ClientExpediteurDTO dto) {
        return ResponseEntity.ok(clientExpediteurService.update(id, dto));
    }

    @Operation(summary = "Supprimer un client expéditeur par son ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientExpediteur(@PathVariable String id) {
        clientExpediteurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}