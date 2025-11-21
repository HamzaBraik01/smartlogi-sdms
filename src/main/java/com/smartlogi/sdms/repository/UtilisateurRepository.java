package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, String> {


    Optional<Utilisateur> findByEmail(String email);
}

