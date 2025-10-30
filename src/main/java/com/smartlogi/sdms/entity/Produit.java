package com.smartlogi.sdms.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "produit")
@Getter
@Setter
@NoArgsConstructor
public class Produit implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private String id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "poids")
    private Double poids;

    @Column(name = "prix")
    private BigDecimal prix;

    @OneToMany(mappedBy = "produit")
    private Set<ColisProduit> colisProduits = new HashSet<>();
}
