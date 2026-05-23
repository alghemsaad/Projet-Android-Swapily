package com.swapily.swapilybackend.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "evaliation")
public class Evaliation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int note;

    private String commentaire;

    private String dateEvaluation;

    private Long utilisateurNoteurId;

    private Long utilisateurNoteeId;

    private Long produitId;
    @ManyToOne
    @JoinColumn(name = "utilisateurNoteurId", referencedColumnName = "id", insertable = false, updatable = false)
    private Utilisateur utilisateurNoteur;
    @ManyToOne
    @JoinColumn(name = "produitId", referencedColumnName = "id", insertable = false, updatable = false)
    private Produit produit;


    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getDateEvaluation() {
        return dateEvaluation;
    }

    public void setDateEvaluation(String dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }

    public Long getUtilisateurNoteurId() {
        return utilisateurNoteurId;
    }

    public void setUtilisateurNoteurId(Long utilisateurNoteurId) {
        this.utilisateurNoteurId = utilisateurNoteurId;
    }

    public Long getUtilisateurNoteeId() {
        return utilisateurNoteeId;
    }

    public void setUtilisateurNoteeId(Long utilisateurNoteeId) {
        this.utilisateurNoteeId = utilisateurNoteeId;
    }

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }
    public Utilisateur getUtilisateurNoteur() {
        return utilisateurNoteur;
    }
    public void setUtilisateurNoteur(Utilisateur utilisateurNoteur) {
        this.utilisateurNoteur = utilisateurNoteur;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }
}
