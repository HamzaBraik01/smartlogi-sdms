package com.smartlogi.sdms.repository;
import com.smartlogi.sdms.entity.Destinataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DestinataireRepository extends JpaRepository<Destinataire, String> {
    Optional<Destinataire> findByEmail(String email);
}
