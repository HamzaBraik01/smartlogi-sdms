package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.ColisDTO;
import com.smartlogi.sdms.dto.UpdateStatutRequestDTO;
import com.smartlogi.sdms.entity.enumeration.Priorite;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import com.smartlogi.sdms.service.interfaces.ColisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/colis")
@Tag(name = "Colis", description = "API pour la gestion complète des colis")
public class ColisController {

    private final ColisService colisService;

    public ColisController(ColisService colisService) {
        this.colisService = colisService;
    }

    @Operation(summary = "Créer une nouvelle demande de livraison (multi-produits)",
            description = "US Client (SDMS-25) + US Gestionnaire (SDMS-35)")
    @PostMapping
    public ResponseEntity<ColisDTO> createColis(@Valid @RequestBody ColisDTO colisDTO) {
        ColisDTO savedColis = colisService.creerDemandeLivraison(colisDTO);
        return ResponseEntity
                .created(URI.create("/api/v1/colis/" + savedColis.getId()))
                .body(savedColis);
    }

    @Operation(summary = "Filtrer et paginer tous les colis",
            description = "US Gestionnaire (SDMS-31)")
    @GetMapping
    public ResponseEntity<Page<ColisDTO>> filterColis(
            @RequestParam(required = false) StatutColis statut,
            @RequestParam(required = false) String zoneId,
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) Priorite priorite,
            Pageable pageable) {

        Page<ColisDTO> colisPage = colisService.findAllColisByCriteria(statut, zoneId, ville, priorite, pageable);
        return ResponseEntity.ok(colisPage);
    }

    @Operation(summary = "Récupérer un colis par son ID (N° de suivi)",
            description = "US Destinataire (SDMS-27)")
    @GetMapping("/{id}")
    public ResponseEntity<ColisDTO> getColisById(@PathVariable String id) {
        return ResponseEntity.ok(colisService.findById(id));
    }

    @Operation(summary = "Mettre à jour le statut d'un colis",
            description = "US Livreur (SDMS-26)")
    @PatchMapping("/{colisId}/statut")
    public ResponseEntity<ColisDTO> updateStatut(
            @PathVariable String colisId,
            @Valid @RequestBody UpdateStatutRequestDTO request) {

        ColisDTO updatedColis = colisService.updateStatutColis(
                colisId,
                request.getStatut(),
                request.getCommentaire()
        );
        return ResponseEntity.ok(updatedColis);
    }

    @Operation(summary = "Récupérer les colis d'un client expéditeur",
            description = "US Client (SDMS-26)")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<Page<ColisDTO>> getColisByClient(@PathVariable String clientId, Pageable pageable) {
        return ResponseEntity.ok(colisService.findColisByClientExpediteur(clientId, pageable));
    }

    @Operation(summary = "Récupérer les colis d'un destinataire",
            description = "US Destinataire (SDMS-27)")
    @GetMapping("/destinataire/{destinataireId}")
    public ResponseEntity<Page<ColisDTO>> getColisByDestinataire(@PathVariable String destinataireId, Pageable pageable) {
        return ResponseEntity.ok(colisService.findColisByDestinataire(destinataireId, pageable));
    }

    @Operation(summary = "Récupérer les colis assignés à un livreur",
            description = "US Livreur (SDMS-28)")
    @GetMapping("/livreur/{livreurId}")
    public ResponseEntity<Page<ColisDTO>> getColisByLivreur(@PathVariable String livreurId, Pageable pageable) {
        return ResponseEntity.ok(colisService.findColisByLivreur(livreurId, pageable));
    }


    @Operation(summary = "Supprimer un colis (Admin/Gestionnaire)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColis(@PathVariable String id) {
        colisService.delete(id);
        return ResponseEntity.noContent().build();
    }
}