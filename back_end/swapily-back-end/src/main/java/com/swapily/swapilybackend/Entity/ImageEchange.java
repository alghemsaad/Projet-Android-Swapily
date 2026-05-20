package com.swapily.swapilybackend.Entity;
import jakarta.persistence.*;

@Entity
public class ImageEchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private String imageData;
    private Long utilisateurId;
    private Long produitLieId;
    private Long DemandesechangeId;
    @ManyToOne
    @JoinColumn(name = "DemandesechangeId", insertable = false, updatable = false)
    private Demmandechange demmandechange;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImageData() { return imageData; }

    public void setImageData(String imageData) { this.imageData = imageData; }

    public Long getUtilisateurId() { return utilisateurId; }

    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }

    public Long getProduitLieId() { return produitLieId; }

    public void setProduitLieId(Long produitLieId) { this.produitLieId = produitLieId; }

    public Long getDemandesechangeId() {
        return DemandesechangeId;
    }

    public void setDemandesechangeId(Long demandesechangeId) {
        DemandesechangeId = demandesechangeId;
    }

    public Demmandechange getDemmandechange() {
        return demmandechange;
    }

    public void setDemmandechange(Demmandechange demmandechange) {
        this.demmandechange = demmandechange;
    }
}

