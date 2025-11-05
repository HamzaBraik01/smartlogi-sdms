package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.ProduitDTO;
import com.smartlogi.sdms.service.interfaces.ProduitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PostMapping
    public ResponseEntity<ProduitDTO> createProduit(@Valid @RequestBody ProduitDTO produitDTO) {
        ProduitDTO savedProduit = produitService.save(produitDTO);
        return ResponseEntity
                .created(URI.create("/api/v1/produits/" + savedProduit.getId()))
                .body(savedProduit);
    }

    @Operation(summary = "Récupérer un produit par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProduitDTO> getProduitById(@PathVariable String id) {
        ProduitDTO produit = produitService.findById(id);
        return ResponseEntity.ok(produit);
    }

    @Operation(summary = "Récupérer la liste paginée de tous les produits")
    @GetMapping
    public ResponseEntity<Page<ProduitDTO>> getAllProduits(Pageable pageable) {
        Page<ProduitDTO> produits = produitService.findAll(pageable);
        return ResponseEntity.ok(produits);
    }

    @Operation(summary = "Mettre à jour un produit existant")
    @PutMapping("/{id}")
    public ResponseEntity<ProduitDTO> updateProduit(@PathVariable String id, @Valid @RequestBody ProduitDTO produitDTO) {
        ProduitDTO updatedProduit = produitService.update(id, produitDTO);
        return ResponseEntity.ok(updatedProduit);
    }

    @Operation(summary = "Supprimer un produit par son ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable String id) {
        produitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}