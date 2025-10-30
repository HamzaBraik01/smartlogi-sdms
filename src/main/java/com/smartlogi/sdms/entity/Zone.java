package com.smartlogi.sdms.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "zone")
@Getter
@Setter
@NoArgsConstructor
public class Zone implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private String id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "code_postal")
    private String codePostal;

    @Column(name = "ville")
    private String ville;


    @OneToMany(mappedBy = "zone")
    private Set<Livreur> livreurs = new HashSet<>();

    @OneToMany(mappedBy = "zone")
    private Set<Colis> colis = new HashSet<>();
}
