package com.smartlogi.sdms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "nom", unique = true, nullable = false)
    private String nom;

    @Column(name = "description")
    private String description;

    @Column(name = "actif")
    private Boolean actif = true;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    public Role(String nom, String description) {
        this.nom = nom;
        this.description = description;
        this.actif = true;
    }


    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }


    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }


    public boolean hasPermission(String permissionNom) {
        return this.permissions.stream()
                .anyMatch(p -> p.getNom().equals(permissionNom));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role role)) return false;
        return id != null && id.equals(role.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

