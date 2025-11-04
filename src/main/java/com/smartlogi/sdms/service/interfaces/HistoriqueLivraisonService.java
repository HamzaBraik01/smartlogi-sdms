package com.smartlogi.sdms.service.interfaces;

import com.smartlogi.sdms.dto.HistoriqueLivraisonDTO;
import java.util.List;
public interface HistoriqueLivraisonService {
    List<HistoriqueLivraisonDTO> findHistoriqueByColisId(String colisId);
}
