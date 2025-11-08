package com.smartlogi.sdms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatistiqueLivreurDTO {
    private String livreurId;
    private String livreurNom;
    private long nombreColis;
    private double poidsTotal;
}