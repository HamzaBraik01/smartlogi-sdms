package com.smartlogi.sdms.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "colis_produit")
@Getter
@Setter
@NoArgsConstructor
public class ColisProduit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colis_id", nullable = false)
    private Colis colis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;
    @Column(name = "quantite")
    private Integer quantite;
    @Column(name = "date_ajout")
    private LocalDateTime dataAjout;

    @PrePersist
    protected void onCreate() {
        dataAjout = LocalDateTime.now();
    }
}
