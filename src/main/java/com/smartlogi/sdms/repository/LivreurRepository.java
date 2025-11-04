package com.smartlogi.sdms.repository;
import com.smartlogi.sdms.entity.Livreur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LivreurRepository extends JpaRepository<Livreur, String> {
    Optional<Livreur> findByEmail(String email);

    @Query("SELECT l FROM Livreur l WHERE " +
            "LOWER(l.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(l.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(l.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(l.telephone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(l.vehicule) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Livreur> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
