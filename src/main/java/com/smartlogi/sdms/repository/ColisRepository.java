package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.dto.StatistiqueLivreurDTO;
import com.smartlogi.sdms.dto.StatistiqueZoneDTO;
import com.smartlogi.sdms.entity.Colis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColisRepository extends JpaRepository<Colis, String>, JpaSpecificationExecutor<Colis> {
    Page<Colis> findAllByClientExpediteurId(String clientExpediteurId, Pageable pageable);
    Page<Colis> findAllByDestinataireId(String destinataireId, Pageable pageable);
    Page<Colis> findAllByLivreurId(String livreurId, Pageable pageable);
    @Query("SELECT c FROM Colis c WHERE " +
            "CAST(c.id AS string) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " + // Cherche sur l'ID (UUID)
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.villeDestination) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Colis> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT NEW com.smartlogi.sdms.dto.StatistiqueLivreurDTO(" +
            "  l.id, " +
            "  CONCAT(l.nom, ' ', l.prenom), " +
            "  COUNT(c.id), " +
            "  COALESCE(SUM(c.poidsTotal), 0.0)" +
            ") " +
            "FROM Colis c JOIN c.livreur l " +
            "WHERE c.statut IN ('COLLECTE', 'EN_STOCK', 'EN_TRANSIT') " +
            "GROUP BY l.id, l.nom, l.prenom")
    List<StatistiqueLivreurDTO> findStatistiquesParLivreur();


    @Query("SELECT NEW com.smartlogi.sdms.dto.StatistiqueZoneDTO(" +
            "  z.id, " +
            "  z.nom, " +
            "  COUNT(c.id), " +
            "  COALESCE(SUM(c.poidsTotal), 0.0)" +
            ") " +
            "FROM Colis c JOIN c.zone z " +
            "WHERE c.statut IN ('COLLECTE', 'EN_STOCK', 'EN_TRANSIT') " +
            "GROUP BY z.id, z.nom")
    List<StatistiqueZoneDTO> findStatistiquesParZone();
}
