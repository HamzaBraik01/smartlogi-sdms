package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.admin.PermissionDTO;
import com.smartlogi.sdms.dto.admin.RoleDTO;
import com.smartlogi.sdms.entity.Permission;
import com.smartlogi.sdms.entity.Role;
import com.smartlogi.sdms.exception.ResourceAlreadyExistsException;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.repository.PermissionRepository;
import com.smartlogi.sdms.repository.RoleRepository;
import com.smartlogi.sdms.service.interfaces.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public AdminServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    // ========== GESTION DES RÔLES ==========

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        if (roleRepository.existsByNom(roleDTO.getNom())) {
            throw new ResourceAlreadyExistsException("Un rôle avec le nom '" + roleDTO.getNom() + "' existe déjà");
        }

        Role role = new Role(roleDTO.getNom(), roleDTO.getDescription());
        role.setActif(roleDTO.getActif() != null ? roleDTO.getActif() : true);

        Role savedRole = roleRepository.save(role);
        logger.info("Rôle créé: {}", savedRole.getNom());

        return toRoleDTO(savedRole);
    }

    @Override
    public RoleDTO updateRole(String id, RoleDTO roleDTO) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé avec l'ID: " + id));

        // Vérifier si le nouveau nom n'est pas déjà utilisé par un autre rôle
        if (!role.getNom().equals(roleDTO.getNom()) && roleRepository.existsByNom(roleDTO.getNom())) {
            throw new ResourceAlreadyExistsException("Un rôle avec le nom '" + roleDTO.getNom() + "' existe déjà");
        }

        role.setNom(roleDTO.getNom());
        role.setDescription(roleDTO.getDescription());
        if (roleDTO.getActif() != null) {
            role.setActif(roleDTO.getActif());
        }

        Role updatedRole = roleRepository.save(role);
        logger.info("Rôle mis à jour: {}", updatedRole.getNom());

        return toRoleDTO(updatedRole);
    }

    @Override
    public void deleteRole(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé avec l'ID: " + id));

        roleRepository.delete(role);
        logger.info("Rôle supprimé: {}", role.getNom());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO getRoleById(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé avec l'ID: " + id));
        return toRoleDTO(role);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO getRoleByNom(String nom) {
        Role role = roleRepository.findByNom(nom)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé avec le nom: " + nom));
        return toRoleDTO(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoleDTO> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable).map(this::toRoleDTO);
    }

    @Override
    public RoleDTO setRoleActive(String id, boolean actif) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé avec l'ID: " + id));

        role.setActif(actif);
        Role updatedRole = roleRepository.save(role);
        logger.info("Rôle {} {}", updatedRole.getNom(), actif ? "activé" : "désactivé");

        return toRoleDTO(updatedRole);
    }

    // ========== GESTION DES PERMISSIONS ==========

    @Override
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        if (permissionRepository.existsByNom(permissionDTO.getNom())) {
            throw new ResourceAlreadyExistsException("Une permission avec le nom '" + permissionDTO.getNom() + "' existe déjà");
        }

        Permission permission = new Permission(
                permissionDTO.getNom(),
                permissionDTO.getDescription(),
                permissionDTO.getRessource(),
                permissionDTO.getAction()
        );
        permission.setActif(permissionDTO.getActif() != null ? permissionDTO.getActif() : true);

        Permission savedPermission = permissionRepository.save(permission);
        logger.info("Permission créée: {}", savedPermission.getNom());

        return toPermissionDTO(savedPermission);
    }

    @Override
    public PermissionDTO updatePermission(String id, PermissionDTO permissionDTO) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission non trouvée avec l'ID: " + id));

        // Vérifier si le nouveau nom n'est pas déjà utilisé
        if (!permission.getNom().equals(permissionDTO.getNom()) &&
            permissionRepository.existsByNom(permissionDTO.getNom())) {
            throw new ResourceAlreadyExistsException("Une permission avec le nom '" + permissionDTO.getNom() + "' existe déjà");
        }

        permission.setNom(permissionDTO.getNom());
        permission.setDescription(permissionDTO.getDescription());
        permission.setRessource(permissionDTO.getRessource());
        permission.setAction(permissionDTO.getAction());
        if (permissionDTO.getActif() != null) {
            permission.setActif(permissionDTO.getActif());
        }

        Permission updatedPermission = permissionRepository.save(permission);
        logger.info("Permission mise à jour: {}", updatedPermission.getNom());

        return toPermissionDTO(updatedPermission);
    }

    @Override
    public void deletePermission(String id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission non trouvée avec l'ID: " + id));

        permissionRepository.delete(permission);
        logger.info("Permission supprimée: {}", permission.getNom());
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionDTO getPermissionById(String id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission non trouvée avec l'ID: " + id));
        return toPermissionDTO(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionDTO> getAllPermissions(Pageable pageable) {
        return permissionRepository.findAll(pageable).map(this::toPermissionDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<PermissionDTO> getPermissionsByRessource(String ressource) {
        return permissionRepository.findByRessource(ressource).stream()
                .map(this::toPermissionDTO)
                .collect(Collectors.toSet());
    }

    // ========== GESTION DES ASSOCIATIONS RÔLE-PERMISSION ==========

    @Override
    public RoleDTO assignPermissionToRole(String roleId, String permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé avec l'ID: " + roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission non trouvée avec l'ID: " + permissionId));

        role.addPermission(permission);
        Role updatedRole = roleRepository.save(role);

        logger.info("Permission '{}' assignée au rôle '{}'", permission.getNom(), role.getNom());
        return toRoleDTO(updatedRole);
    }

    @Override
    public RoleDTO removePermissionFromRole(String roleId, String permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé avec l'ID: " + roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission non trouvée avec l'ID: " + permissionId));

        role.removePermission(permission);
        Role updatedRole = roleRepository.save(role);

        logger.info("Permission '{}' retirée du rôle '{}'", permission.getNom(), role.getNom());
        return toRoleDTO(updatedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<PermissionDTO> getPermissionsOfRole(String roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé avec l'ID: " + roleId));

        return role.getPermissions().stream()
                .map(this::toPermissionDTO)
                .collect(Collectors.toSet());
    }

    @Override
    public RoleDTO assignPermissionsToRole(String roleId, Set<String> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé avec l'ID: " + roleId));

        for (String permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission non trouvée avec l'ID: " + permissionId));
            role.addPermission(permission);
        }

        Role updatedRole = roleRepository.save(role);
        logger.info("{} permissions assignées au rôle '{}'", permissionIds.size(), role.getNom());

        return toRoleDTO(updatedRole);
    }

    // ========== MÉTHODES DE MAPPING ==========

    private RoleDTO toRoleDTO(Role role) {
        RoleDTO dto = new RoleDTO(
                role.getId(),
                role.getNom(),
                role.getDescription(),
                role.getActif()
        );
        dto.setPermissions(
                role.getPermissions().stream()
                        .map(this::toPermissionDTO)
                        .collect(Collectors.toSet())
        );
        return dto;
    }

    private PermissionDTO toPermissionDTO(Permission permission) {
        return new PermissionDTO(
                permission.getId(),
                permission.getNom(),
                permission.getDescription(),
                permission.getRessource(),
                permission.getAction(),
                permission.getActif()
        );
    }
}

