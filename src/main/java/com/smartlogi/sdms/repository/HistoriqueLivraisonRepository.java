package com.smartlogi.sdms.repository;
import com.smartlogi.sdms.entity.HistoriqueLivraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueLivraisonRepository extends JpaRepository<HistoriqueLivraison, String> {

    // Trie automatiquement l'historique du plus récent au plus ancien
    List<HistoriqueLivraison> findAllByColisIdOrderByDateChangementDesc(String colisId);
}