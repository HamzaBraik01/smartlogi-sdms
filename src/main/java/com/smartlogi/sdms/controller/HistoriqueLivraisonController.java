package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.HistoriqueLivraisonDTO;
import com.smartlogi.sdms.service.interfaces.HistoriqueLivraisonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Historique Colis", description = "API pour consulter l'historique d'un colis")
public class HistoriqueLivraisonController {

    private final HistoriqueLivraisonService historiqueLivraisonService;

    public HistoriqueLivraisonController(HistoriqueLivraisonService historiqueLivraisonService) {
        this.historiqueLivraisonService = historiqueLivraisonService;
    }

    @Operation(summary = "Consulter l'historique complet d'un colis",
            description = "US Gestionnaire (SDMS-34) - Endpoint public/client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parcel history (sorted newest to oldest)"),
            @ApiResponse(responseCode = "404", description = "Parcel not found")
    })
    @PreAuthorize("@colisSecurityService.canAccess(#id, authentication)")
    @GetMapping("/colis/{id}/historique")
    public ResponseEntity<List<HistoriqueLivraisonDTO>> getHistoriqueParColisId(
            @Parameter(description = "ID (Tracking Number) of the parcel") @PathVariable String id) {

        List<HistoriqueLivraisonDTO> historique = historiqueLivraisonService.findHistoriqueByColisId(id);
        return ResponseEntity.ok(historique);
    }
}