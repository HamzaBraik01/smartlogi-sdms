package com.smartlogi.sdms.dto;

import lombok.Data;
import java.util.List;

@Data
public class StatistiquesTourneeDTO {
    private List<StatistiqueLivreurDTO> parLivreur;
    private List<StatistiqueZoneDTO> parZone;
}