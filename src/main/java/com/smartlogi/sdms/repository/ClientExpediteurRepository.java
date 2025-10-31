package com.smartlogi.sdms.repository;
import com.smartlogi.sdms.entity.ClientExpediteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientExpediteurRepository extends JpaRepository<ClientExpediteur, String> {
    Optional<ClientExpediteur> findByEmail(String email);
}
