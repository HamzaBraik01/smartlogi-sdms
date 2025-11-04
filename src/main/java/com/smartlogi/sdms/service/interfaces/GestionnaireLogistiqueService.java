package com.smartlogi.sdms.service.interfaces;

import com.smartlogi.sdms.dto.GestionnaireLogistiqueDTO;
import com.smartlogi.sdms.dto.GlobalSearchResponseDTO;
import com.smartlogi.sdms.dto.StatistiquesTourneeDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GestionnaireLogistiqueService {


    GlobalSearchResponseDTO rechercher(String motCle, Pageable pageable);


    GestionnaireLogistiqueDTO save(@Valid GestionnaireLogistiqueDTO dto);

    GestionnaireLogistiqueDTO update(String id, @Valid GestionnaireLogistiqueDTO dto);

    GestionnaireLogistiqueDTO findById(String id);

    Page<GestionnaireLogistiqueDTO> findAll(Pageable pageable);

    void delete(String id);
    StatistiquesTourneeDTO getStatistiquesTournees();

}