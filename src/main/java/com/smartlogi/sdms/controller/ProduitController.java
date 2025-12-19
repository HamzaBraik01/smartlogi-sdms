package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.ProduitDTO;
import com.smartlogi.sdms.service.interfaces.ProduitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/produits")
@Tag(name = "Produits", description = "API pour la gestion du catalogue de produits")
public class ProduitController {

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @Operation(summary = "Créer un nouveau produit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produit créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de produit invalides (ex: nom manquant, prix négatif)")
    })
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<ProduitDTO> createProduit(@Valid @RequestBody ProduitDTO produitDTO) {
        ProduitDTO savedProduit = produitService.save(produitDTO);
        return ResponseEntity
                .created(URI.create("/api/v1/produits/" + savedProduit.getId()))
                .body(savedProduit);
    }

    @Operation(summary = "Récupérer un produit par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit trouvé"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé avec cet ID")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ProduitDTO> getProduitById(@Parameter(description = "ID (String/UUID) du produit à rechercher") @PathVariable String id) {
        ProduitDTO produit = produitService.findById(id);
        return ResponseEntity.ok(produit);
    }

    @Operation(summary = "Récupérer la liste paginée de tous les produits")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste paginée des produits")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Page<ProduitDTO>> getAllProduits(@Parameter(description = "Paramètres de pagination (page, size, sort)") Pageable pageable) {
        Page<ProduitDTO> produits = produitService.findAll(pageable);
        return ResponseEntity.ok(produits);
    }

    @Operation(summary = "Mettre à jour un produit existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de produit invalides"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé avec cet ID")
    })
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ProduitDTO> updateProduit(@Parameter(description = "ID (String/UUID) du produit à mettre à jour") @PathVariable String id, @Valid @RequestBody ProduitDTO produitDTO) {
        ProduitDTO updatedProduit = produitService.update(id, produitDTO);
        return ResponseEntity.ok(updatedProduit);
    }

    @Operation(summary = "Supprimer un produit par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produit supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé avec cet ID"),
            @ApiResponse(responseCode = "409", description = "Conflit : Le produit est utilisé dans un ou plusieurs colis (contrainte FK)")
    })
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@Parameter(description = "ID (String/UUID) du produit à supprimer") @PathVariable String id) {
        produitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}