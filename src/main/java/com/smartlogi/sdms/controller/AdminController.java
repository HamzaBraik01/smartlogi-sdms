package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.admin.PermissionDTO;
import com.smartlogi.sdms.dto.admin.RoleDTO;
import com.smartlogi.sdms.service.interfaces.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;


@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Administration", description = "API pour la gestion des rôles et permissions (Admin uniquement)")
@PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_MANAGE') or hasAuthority('PERMISSION_MANAGE')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ========== GESTION DES RÔLES ==========

    @Operation(summary = "Créer un nouveau rôle",
            description = "Crée un nouveau rôle dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rôle créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou rôle déjà existant")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_MANAGE')")
    @PostMapping("/roles")
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        RoleDTO createdRole = adminService.createRole(roleDTO);
        return ResponseEntity
                .created(URI.create("/api/v1/admin/roles/" + createdRole.getId()))
                .body(createdRole);
    }

    @Operation(summary = "Récupérer un rôle par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rôle trouvé"),
            @ApiResponse(responseCode = "404", description = "Rôle non trouvé")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_MANAGE')")
    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleDTO> getRoleById(
            @Parameter(description = "ID du rôle") @PathVariable String id) {
        return ResponseEntity.ok(adminService.getRoleById(id));
    }

    @Operation(summary = "Récupérer un rôle par son nom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rôle trouvé"),
            @ApiResponse(responseCode = "404", description = "Rôle non trouvé")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_MANAGE')")
    @GetMapping("/roles/by-name/{nom}")
    public ResponseEntity<RoleDTO> getRoleByNom(
            @Parameter(description = "Nom du rôle") @PathVariable String nom) {
        return ResponseEntity.ok(adminService.getRoleByNom(nom));
    }

    @Operation(summary = "Récupérer tous les rôles avec pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste paginée des rôles")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_MANAGE')")
    @GetMapping("/roles")
    public ResponseEntity<Page<RoleDTO>> getAllRoles(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllRoles(pageable));
    }

    @Operation(summary = "Mettre à jour un rôle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rôle mis à jour"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Rôle non trouvé")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_MANAGE')")
    @PutMapping("/roles/{id}")
    public ResponseEntity<RoleDTO> updateRole(
            @Parameter(description = "ID du rôle") @PathVariable String id,
            @Valid @RequestBody RoleDTO roleDTO) {
        return ResponseEntity.ok(adminService.updateRole(id, roleDTO));
    }

    @Operation(summary = "Supprimer un rôle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rôle supprimé"),
            @ApiResponse(responseCode = "404", description = "Rôle non trouvé")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_MANAGE')")
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "ID du rôle") @PathVariable String id) {
        adminService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activer/Désactiver un rôle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut mis à jour"),
            @ApiResponse(responseCode = "404", description = "Rôle non trouvé")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_MANAGE')")
    @PatchMapping("/roles/{id}/active")
    public ResponseEntity<RoleDTO> setRoleActive(
            @Parameter(description = "ID du rôle") @PathVariable String id,
            @Parameter(description = "Statut actif") @RequestParam boolean actif) {
        return ResponseEntity.ok(adminService.setRoleActive(id, actif));
    }

    // ========== GESTION DES PERMISSIONS ==========

    @Operation(summary = "Créer une nouvelle permission",
            description = "Crée une nouvelle permission dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Permission créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou permission déjà existante")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PERMISSION_MANAGE')")
    @PostMapping("/permissions")
    public ResponseEntity<PermissionDTO> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        PermissionDTO createdPermission = adminService.createPermission(permissionDTO);
        return ResponseEntity
                .created(URI.create("/api/v1/admin/permissions/" + createdPermission.getId()))
                .body(createdPermission);
    }

    @Operation(summary = "Récupérer une permission par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permission trouvée"),
            @ApiResponse(responseCode = "404", description = "Permission non trouvée")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PERMISSION_MANAGE')")
    @GetMapping("/permissions/{id}")
    public ResponseEntity<PermissionDTO> getPermissionById(
            @Parameter(description = "ID de la permission") @PathVariable String id) {
        return ResponseEntity.ok(adminService.getPermissionById(id));
    }

    @Operation(summary = "Récupérer toutes les permissions avec pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste paginée des permissions")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PERMISSION_MANAGE')")
    @GetMapping("/permissions")
    public ResponseEntity<Page<PermissionDTO>> getAllPermissions(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllPermissions(pageable));
    }

    @Operation(summary = "Récupérer les permissions par ressource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des permissions")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PERMISSION_MANAGE')")
    @GetMapping("/permissions/by-ressource/{ressource}")
    public ResponseEntity<Set<PermissionDTO>> getPermissionsByRessource(
            @Parameter(description = "Nom de la ressource") @PathVariable String ressource) {
        return ResponseEntity.ok(adminService.getPermissionsByRessource(ressource));
    }

    @Operation(summary = "Mettre à jour une permission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permission mise à jour"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Permission non trouvée")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PERMISSION_MANAGE')")
    @PutMapping("/permissions/{id}")
    public ResponseEntity<PermissionDTO> updatePermission(
            @Parameter(description = "ID de la permission") @PathVariable String id,
            @Valid @RequestBody PermissionDTO permissionDTO) {
        return ResponseEntity.ok(adminService.updatePermission(id, permissionDTO));
    }

    @Operation(summary = "Supprimer une permission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Permission supprimée"),
            @ApiResponse(responseCode = "404", description = "Permission non trouvée")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PERMISSION_MANAGE')")
    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Void> deletePermission(
            @Parameter(description = "ID de la permission") @PathVariable String id) {
        adminService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }

    // ========== GESTION DES ASSOCIATIONS RÔLE-PERMISSION ==========

    @Operation(summary = "Assigner une permission à un rôle",
            description = "Ajoute une permission aux droits d'un rôle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permission assignée"),
            @ApiResponse(responseCode = "404", description = "Rôle ou permission non trouvé")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_MANAGE')")
    @PostMapping("/roles/{roleId}/permissions/{permissionId}")
    public ResponseEntity<RoleDTO> assignPermissionToRole(
            @Parameter(description = "ID du rôle") @PathVariable String roleId,
            @Parameter(description = "ID de la permission") @PathVariable String permissionId) {
        return ResponseEntity.ok(adminService.assignPermissionToRole(roleId, permissionId));
    }

    @Operation(summary = "Retirer une permission d'un rôle",
            description = "Retire une permission des droits d'un rôle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permission retirée"),
            @ApiResponse(responseCode = "404", description = "Rôle ou permission non trouvé")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_MANAGE')")
    @DeleteMapping("/roles/{roleId}/permissions/{permissionId}")
    public ResponseEntity<RoleDTO> removePermissionFromRole(
            @Parameter(description = "ID du rôle") @PathVariable String roleId,
            @Parameter(description = "ID de la permission") @PathVariable String permissionId) {
        return ResponseEntity.ok(adminService.removePermissionFromRole(roleId, permissionId));
    }

    @Operation(summary = "Récupérer toutes les permissions d'un rôle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des permissions du rôle"),
            @ApiResponse(responseCode = "404", description = "Rôle non trouvé")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_MANAGE')")
    @GetMapping("/roles/{roleId}/permissions")
    public ResponseEntity<Set<PermissionDTO>> getPermissionsOfRole(
            @Parameter(description = "ID du rôle") @PathVariable String roleId) {
        return ResponseEntity.ok(adminService.getPermissionsOfRole(roleId));
    }

    @Operation(summary = "Assigner plusieurs permissions à un rôle",
            description = "Ajoute plusieurs permissions aux droits d'un rôle en une seule opération")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissions assignées"),
            @ApiResponse(responseCode = "404", description = "Rôle ou permission non trouvé")
    })
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_MANAGE')")
    @PostMapping("/roles/{roleId}/permissions/batch")
    public ResponseEntity<RoleDTO> assignPermissionsToRole(
            @Parameter(description = "ID du rôle") @PathVariable String roleId,
            @RequestBody Set<String> permissionIds) {
        return ResponseEntity.ok(adminService.assignPermissionsToRole(roleId, permissionIds));
    }
}

