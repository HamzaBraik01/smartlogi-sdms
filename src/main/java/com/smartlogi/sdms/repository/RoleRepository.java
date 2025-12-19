package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, String> {


    Optional<Role> findByNom(String nom);


    boolean existsByNom(String nom);


    List<Role> findByActifTrue();


    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.nom = :permissionNom")
    List<Role> findByPermissionNom(String permissionNom);
}

