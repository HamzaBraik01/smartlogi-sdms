package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.ColisDTO;
import com.smartlogi.sdms.dto.GlobalSearchResponseDTO;
import com.smartlogi.sdms.dto.HistoriqueLivraisonDTO;
import com.smartlogi.sdms.dto.StatistiquesTourneeDTO;
import com.smartlogi.sdms.service.interfaces.ColisService;
import com.smartlogi.sdms.service.interfaces.GestionnaireLogistiqueService;
import com.smartlogi.sdms.service.interfaces.HistoriqueLivraisonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gestion")
@Tag(name = "Gestion (Manager)", description = "API pour les opérations du gestionnaire logistique")
public class GestionnaireLogistiqueController {

    private final GestionnaireLogistiqueService gestionnaireLogistiqueService;
    private final ColisService colisService;
    private final HistoriqueLivraisonService historiqueLivraisonService;

    public GestionnaireLogistiqueController(GestionnaireLogistiqueService gestionnaireLogistiqueService,
                                            ColisService colisService,
                                            HistoriqueLivraisonService historiqueLivraisonService) {
        this.gestionnaireLogistiqueService = gestionnaireLogistiqueService;
        this.colisService = colisService;
        this.historiqueLivraisonService = historiqueLivraisonService;
    }

    @Operation(summary = "Assigner un colis à un livreur",
            description = "US Gestionnaire (SDMS-30)")
    @PatchMapping("/colis/{colisId}/assigner/{livreurId}")
    public ResponseEntity<ColisDTO> assignerColis(
            @Parameter(description = "ID du colis à assigner") @PathVariable String colisId,
            @Parameter(description = "ID du livreur qui reçoit le colis") @PathVariable String livreurId) {

        ColisDTO updatedColis = colisService.assignerColisLivreur(colisId, livreurId);
        return ResponseEntity.ok(updatedColis);
    }

    @Operation(summary = "Recherche globale (Colis, Clients, Livreurs)",
            description = "US Gestionnaire (SDMS-32)")
    @GetMapping("/recherche")
    public ResponseEntity<GlobalSearchResponseDTO> rechercher(
            @Parameter(description = "Terme à rechercher (nom, email, ville, n° de suivi...)")
            @RequestParam("q") String motCle,
            Pageable pageable) {

        return ResponseEntity.ok(gestionnaireLogistiqueService.rechercher(motCle, pageable));
    }

    @Operation(summary = "Obtenir les statistiques des tournées",
            description = "US Gestionnaire (SDMS-33). Calcule le poids/nb colis par livreur/zone.")
    @GetMapping("/statistiques")
    public ResponseEntity<StatistiquesTourneeDTO> getStatistiques() {
        return ResponseEntity.ok(gestionnaireLogistiqueService.getStatistiquesTournees());
    }

    @Operation(summary = "Consulter l'historique complet d'un colis",
            description = "US Gestionnaire (SDMS-33)")
    @GetMapping("/colis/{colisId}/historique")
    public ResponseEntity<List<HistoriqueLivraisonDTO>> getHistoriqueColis(
            @Parameter(description = "ID du colis à inspecter") @PathVariable String colisId) {

        return ResponseEntity.ok(historiqueLivraisonService.findHistoriqueByColisId(colisId));
    }
}