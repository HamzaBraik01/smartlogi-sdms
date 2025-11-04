package com.smartlogi.sdms.dto;

import lombok.Data;

@Data
public class StatistiqueZoneDTO {
    private String zoneId;
    private String zoneNom;
    private long nombreColis;
    private double poidsTotal;

    public StatistiqueZoneDTO(String zoneId, String zoneNom, long nombreColis, double poidsTotal) {
        this.zoneId = zoneId;
        this.zoneNom = zoneNom;
        this.nombreColis = nombreColis;
        this.poidsTotal = poidsTotal;
    }
}