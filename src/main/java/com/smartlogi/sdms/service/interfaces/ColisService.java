package com.smartlogi.sdms.service.interfaces;

import com.smartlogi.sdms.dto.ColisDTO;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ColisService {


    ColisDTO creerDemandeLivraison(ColisDTO colisDTO);


    ColisDTO update(String id, ColisDTO colisDTO);


    ColisDTO findById(String id);


    Page<ColisDTO> findAll(Pageable pageable);


    void delete(String id);

    Page<ColisDTO> findColisByClientExpediteur(String clientExpediteurId, Pageable pageable);
    Page<ColisDTO> findColisByDestinataire(String destinataireId, Pageable pageable);
    Page<ColisDTO> findColisByLivreur(String livreurId, Pageable pageable);
    ColisDTO updateStatutColis(String colisId, StatutColis newStatut, String commentaire);
}