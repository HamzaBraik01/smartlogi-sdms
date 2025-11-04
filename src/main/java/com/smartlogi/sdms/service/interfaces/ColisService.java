package com.smartlogi.sdms.service.interfaces;

import com.smartlogi.sdms.dto.ColisDTO;
import com.smartlogi.sdms.dto.StatistiquesTourneeDTO;
import com.smartlogi.sdms.entity.enumeration.Priorite;
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
    ColisDTO assignerColisLivreur(String colisId, String livreurId);
    Page<ColisDTO> findAllColisByCriteria(StatutColis statut, String zoneId, String ville, Priorite priorite, Pageable pageable);
    StatistiquesTourneeDTO getStatistiquesTournees();
}