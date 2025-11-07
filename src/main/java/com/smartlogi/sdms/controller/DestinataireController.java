package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.DestinataireDTO;
import com.smartlogi.sdms.service.interfaces.DestinataireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    @PostMapping
    public ResponseEntity<DestinataireDTO> createDestinataire(@Valid @RequestBody DestinataireDTO dto) {
        DestinataireDTO savedDto = destinataireService.save(dto);
        return ResponseEntity
                .created(URI.create("/api/v1/destinataires/" + savedDto.getId()))
                .body(savedDto);
    }

    @Operation(summary = "Récupérer un destinataire par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<DestinataireDTO> getDestinataireById(@PathVariable String id) {
        return ResponseEntity.ok(destinataireService.findById(id));
    }

    @Operation(summary = "Récupérer la liste paginée de tous les destinataires")
    @GetMapping
    public ResponseEntity<Page<DestinataireDTO>> getAllDestinataires(Pageable pageable) {
        return ResponseEntity.ok(destinataireService.findAll(pageable));
    }

    @Operation(summary = "Mettre à jour un destinataire existant")
    @PutMapping("/{id}")
    public ResponseEntity<DestinataireDTO> updateDestinataire(@PathVariable String id, @Valid @RequestBody DestinataireDTO dto) {
        return ResponseEntity.ok(destinataireService.update(id, dto));
    }

    @Operation(summary = "Supprimer un destinataire par son ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDestinataire(@PathVariable String id) {
        destinataireService.delete(id);
        return ResponseEntity.noContent().build();
    }
}