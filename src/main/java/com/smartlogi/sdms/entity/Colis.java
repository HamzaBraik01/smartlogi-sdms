package com.smartlogi.sdms.entity;
import com.smartlogi.sdms.entity.enumeration.Priorite;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "colis")
@Getter
@Setter
@NoArgsConstructor
public class Colis implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private String id;

    @Column(name = "description")
    private String description;

    @Column(name = "poids_total")
    private Double poidsTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutColis statut;

    @Enumerated(EnumType.STRING)
    @Column(name = "priorite")
    private Priorite priorite;

    @Column(name = "ville_destination")
    private String villeDestination;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_dernier_statut")
    private LocalDateTime dateDernierStatut;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_expediteur_id")
    private ClientExpediteur clientExpediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id")
    private Destinataire destinataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livreur_id")
    private Livreur livreur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;


    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HistoriqueLivraison> historiqueLivraisons = new HashSet<>();

    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ColisProduit> colisProduits = new HashSet<>();


    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateDernierStatut = LocalDateTime.now();
        if (statut == null) {
            statut = StatutColis.CREE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dateDernierStatut = LocalDateTime.now();
    }
}
