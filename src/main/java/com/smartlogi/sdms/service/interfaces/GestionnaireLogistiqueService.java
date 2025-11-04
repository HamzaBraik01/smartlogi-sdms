package com.smartlogi.sdms.service.interfaces;

import com.smartlogi.sdms.dto.GestionnaireLogistiqueDTO;
import com.smartlogi.sdms.dto.GlobalSearchResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GestionnaireLogistiqueService {


    GlobalSearchResponseDTO rechercher(String motCle, Pageable pageable);


    GestionnaireLogistiqueDTO save(GestionnaireLogistiqueDTO dto);

    GestionnaireLogistiqueDTO update(String id, GestionnaireLogistiqueDTO dto);

    GestionnaireLogistiqueDTO findById(String id);

    Page<GestionnaireLogistiqueDTO> findAll(Pageable pageable);

    void delete(String id);
}