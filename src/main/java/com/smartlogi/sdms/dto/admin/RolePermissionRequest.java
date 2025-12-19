package com.smartlogi.sdms.dto.admin;

import jakarta.validation.constraints.NotBlank;


public class RolePermissionRequest {

    @NotBlank(message = "L'ID du rôle est obligatoire")
    private String roleId;

    @NotBlank(message = "L'ID de la permission est obligatoire")
    private String permissionId;

    public RolePermissionRequest() {
    }

    public RolePermissionRequest(String roleId, String permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }
}

