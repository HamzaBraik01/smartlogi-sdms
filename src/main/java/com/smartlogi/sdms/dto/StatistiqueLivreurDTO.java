package com.smartlogi.sdms.dto;

import lombok.Data;

@Data
public class StatistiqueLivreurDTO {
    private String livreurId;
    private String livreurNom; // Pratique pour l'affichage
    private long nombreColis;
    private double poidsTotal;

    public StatistiqueLivreurDTO(String livreurId, String livreurNom, long nombreColis, double poidsTotal) {
        this.livreurId = livreurId;
        this.livreurNom = livreurNom;
        this.nombreColis = nombreColis;
        this.poidsTotal = poidsTotal;
    }
}