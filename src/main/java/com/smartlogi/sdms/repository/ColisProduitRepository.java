package com.smartlogi.sdms.repository;
import com.smartlogi.sdms.entity.ColisProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColisProduitRepository extends JpaRepository<ColisProduit, String> {
}
