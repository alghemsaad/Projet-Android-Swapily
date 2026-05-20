package com.swapily.swapilybackend.Entity;
import jakarta.persistence.*;

@Entity
public class Avis {
    @Id
    @GeneratedValue
    private Long id;
    private String  contenu;
    private int note_app;
    private Long utilisateurId;
    @ManyToOne
    @JoinColumn(name = "utilisateurId", insertable = false, updatable = false)
    private Utilisateur utilisateur;
    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public int getNote_app() {
        return note_app;
    }

    public void setNote_app(int note_app) {
        this.note_app = note_app;
    }

    public Long getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}
