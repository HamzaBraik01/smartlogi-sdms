package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.ColisDTO;
import com.smartlogi.sdms.dto.UpdateStatutRequestDTO;
import com.smartlogi.sdms.entity.enumeration.Priorite;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import com.smartlogi.sdms.service.interfaces.ColisService;
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
@RequestMapping("/api/v1/colis")
@Tag(name = "Colis", description = "API pour la gestion complète des colis")
public class ColisController {

    private final ColisService colisService;

    public ColisController(ColisService colisService) {
        this.colisService = colisService;
    }

    @Operation(summary = "Créer une nouvelle demande de livraison (multi-produits)",
            description = "US Client (SDMS-25) + US Gestionnaire (SDMS-35)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Colis créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (ex: produit non trouvé, client manquant, validation échouée)")
    })
    @PostMapping
    public ResponseEntity<ColisDTO> createColis(@Valid @RequestBody ColisDTO colisDTO) {
        ColisDTO savedColis = colisService.creerDemandeLivraison(colisDTO);
        return ResponseEntity
                .created(URI.create("/api/v1/colis/" + savedColis.getId()))
                .body(savedColis);
    }

    @Operation(summary = "Filtrer et paginer tous les colis",
            description = "US Gestionnaire (SDMS-31)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste paginée des colis filtrés")
    })
    @GetMapping
    public ResponseEntity<Page<ColisDTO>> filterColis(
            @Parameter(description = "Filtrer par statut") @RequestParam(required = false) StatutColis statut,
            @Parameter(description = "Filtrer par ID de zone") @RequestParam(required = false) String zoneId,
            @Parameter(description = "Filtrer par ville de destination (recherche partielle)") @RequestParam(required = false) String ville,
            @Parameter(description = "Filtrer par priorité") @RequestParam(required = false) Priorite priorite,
            Pageable pageable) {

        Page<ColisDTO> colisPage = colisService.findAllColisByCriteria(statut, zoneId, ville, priorite, pageable);
        return ResponseEntity.ok(colisPage);
    }

    @Operation(summary = "Récupérer un colis par son ID (N° de suivi)",
            description = "US Destinataire (SDMS-27)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colis trouvé"),
            @ApiResponse(responseCode = "404", description = "Colis non trouvé avec cet ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ColisDTO> getColisById(@Parameter(description = "ID (N° de suivi) du colis") @PathVariable String id) {
        return ResponseEntity.ok(colisService.findById(id));
    }

    @Operation(summary = "Mettre à jour le statut d'un colis",
            description = "US Livreur (SDMS-26)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Transition de statut invalide (ex: LIVRE -> EN_STOCK)"),
            @ApiResponse(responseCode = "404", description = "Colis non trouvé")
    })
    @PatchMapping("/{colisId}/statut")
    public ResponseEntity<ColisDTO> updateStatut(@Parameter(description = "ID du colis à mettre à jour")
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste paginée des colis du client"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    @GetMapping("/client/{clientId}")
    public ResponseEntity<Page<ColisDTO>> getColisByClient(@Parameter(description = "ID du client expéditeur") @PathVariable String clientId, Pageable pageable) {
        return ResponseEntity.ok(colisService.findColisByClientExpediteur(clientId, pageable));
    }

    @Operation(summary = "Récupérer les colis d'un destinataire",
            description = "US Destinataire (SDMS-27)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste paginée des colis du destinataire")
    })
    @GetMapping("/destinataire/{destinataireId}")
    public ResponseEntity<Page<ColisDTO>> getColisByDestinataire(@Parameter(description = "ID du destinataire") @PathVariable String destinataireId, Pageable pageable) {
        return ResponseEntity.ok(colisService.findColisByDestinataire(destinataireId, pageable));
    }

    @Operation(summary = "Récupérer les colis assignés à un livreur",
            description = "US Livreur (SDMS-28)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste paginée des colis du livreur (triée par priorité)")
    })
    @GetMapping("/livreur/{livreurId}")
    public ResponseEntity<Page<ColisDTO>> getColisByLivreur(@Parameter(description = "ID du livreur") @PathVariable String livreurId, Pageable pageable) {
        return ResponseEntity.ok(colisService.findColisByLivreur(livreurId, pageable));
    }


    @Operation(summary = "Supprimer un colis (Admin/Gestionnaire)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Colis supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Colis non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColis(@Parameter(description = "ID du colis à supprimer") @PathVariable String id) {
        colisService.delete(id);
        return ResponseEntity.noContent().build();
    }
}