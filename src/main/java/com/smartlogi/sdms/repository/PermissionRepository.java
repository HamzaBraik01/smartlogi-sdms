package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {


    Optional<Permission> findByNom(String nom);


    boolean existsByNom(String nom);


    List<Permission> findByActifTrue();


    List<Permission> findByRessource(String ressource);


    List<Permission> findByAction(String action);


    List<Permission> findByRessourceAndAction(String ressource, String action);


    @Query("SELECT DISTINCT p.ressource FROM Permission p WHERE p.actif = true")
    List<String> findDistinctRessources();
}

