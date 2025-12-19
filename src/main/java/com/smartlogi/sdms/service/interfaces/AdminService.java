package com.smartlogi.sdms.service.interfaces;

import com.smartlogi.sdms.dto.admin.PermissionDTO;
import com.smartlogi.sdms.dto.admin.RoleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

/**
 * Interface de service pour la gestion des rôles et permissions.
 * Fournit les opérations CRUD et la gestion des associations rôle-permission.
 */
public interface AdminService {

    // ========== GESTION DES RÔLES ==========


    RoleDTO createRole(RoleDTO roleDTO);


    RoleDTO updateRole(String id, RoleDTO roleDTO);


    void deleteRole(String id);


    RoleDTO getRoleById(String id);


    RoleDTO getRoleByNom(String nom);


    Page<RoleDTO> getAllRoles(Pageable pageable);


    RoleDTO setRoleActive(String id, boolean actif);

    // ========== GESTION DES PERMISSIONS ==========


    PermissionDTO createPermission(PermissionDTO permissionDTO);


    PermissionDTO updatePermission(String id, PermissionDTO permissionDTO);


    void deletePermission(String id);


    PermissionDTO getPermissionById(String id);


    Page<PermissionDTO> getAllPermissions(Pageable pageable);


    Set<PermissionDTO> getPermissionsByRessource(String ressource);

    // ========== GESTION DES ASSOCIATIONS RÔLE-PERMISSION ==========


    RoleDTO assignPermissionToRole(String roleId, String permissionId);


    RoleDTO removePermissionFromRole(String roleId, String permissionId);


    Set<PermissionDTO> getPermissionsOfRole(String roleId);


    RoleDTO assignPermissionsToRole(String roleId, Set<String> permissionIds);
}

