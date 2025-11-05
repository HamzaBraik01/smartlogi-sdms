package com.smartlogi.sdms.entity;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_livraison")
@Getter
@Setter
@NoArgsConstructor
public class HistoriqueLivraison implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colis_id", nullable = false)
    private Colis colis;


    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutColis statut;

    @Column(name = "date_changement")
    private LocalDateTime dateChangement;

    @Column(name = "commentaire")
    private String commentaire;


    @PrePersist
    protected void onCreate() {
        if (dateChangement == null) {
            dateChangement = LocalDateTime.now();
        }
    }
}
