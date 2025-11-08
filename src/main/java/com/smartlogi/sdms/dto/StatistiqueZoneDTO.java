package com.smartlogi.sdms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatistiqueZoneDTO {
    private String zoneId;
    private String zoneNom;
    private long nombreColis;
    private double poidsTotal;
}