package com.swapily.swapilybackend.Entity;
import jakarta.persistence.*;

@Entity
@Table(name = "demandeechange")
public class Demmandechange {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String DateDE;
    private String statut;
    private String message;
    private Long utilisateurId;
    private Long produitId;
    @ManyToOne
    @JoinColumn(name = "produitId", insertable = false, updatable = false)
    private Produit produit;
    @ManyToOne
    @JoinColumn(name = "utilisateurId", insertable = false, updatable = false)
    private Utilisateur utilisateur;
//    @ManyToOne
//    @JoinColumn(name = "image_changeId", insertable = false, updatable = false)
//    private ImageEchange imageEchange;


    // GETTER UND SETTER


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDateDE() {
        return DateDE;
    }

    public void setDateDE(String dateDE) {
        DateDE = dateDE;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
//    public ImageEchange getImageEchange() {
//        return echange;
//    }
//    public void setImageEchange(ImageEchange echange) {
//        this.echange = imageEchange;
//    }
}
