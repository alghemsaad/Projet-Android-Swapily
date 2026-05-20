package com.swapily.swapilybackend.Controller;

import com.swapily.swapilybackend.Entity.*;
import com.swapily.swapilybackend.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/produit")
@CrossOrigin(origins = "http://localhost:5173")
public class ProduitController {
    @Autowired
    private ProduitRepository produitRepository;
    @PostMapping("/produit")
    public Produit createProduit(
            @RequestParam("photo") MultipartFile imageFile,
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("categorie") String categorie,
            @RequestParam("statut") String statut,
            @RequestParam("localisation") String localisation,
            @RequestParam("utilisateur_id") Long utilisateur_id

    ) throws IOException {
        Produit produit = new Produit();
        produit.setTitre(titre);
        produit.setDescription(description);
        produit.setCategorie(categorie);
        produit.setStatut(statut);
        produit.setLocalisation(localisation);
        produit.setUtilisateurId(utilisateur_id);


        // enregistrer l'image comme blob
        produit.setPhoto(imageFile.getBytes());
        return produitRepository.save(produit);
    }

    // Produits avec le statut "changer"
    @GetMapping("/changer")
    public List<Produit> getProduitsWithStatutChanger() {
        return produitRepository.findByStatutOrderByIdDesc("changer");
    }

    // Produits avec le statut "donner"
    @GetMapping("/donner")
    public List<Produit> getProduitsWithStatutDonner() {
        return produitRepository.findByStatutOrderByIdDesc("donner");
    }

    //tou les produits
    @GetMapping("/all")
    public List<Produit> getAllProduits() {
        return produitRepository.findAllByOrderByIdDesc();
    }

    //Produit dettails by ID
    @GetMapping("/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Long id) {
        Optional<Produit> produit = produitRepository.findById(id);
        return produit.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    // Supprimer un produit par ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduit(@PathVariable Long id) {
        Optional<Produit> produitOptional = produitRepository.findById(id);
        if (produitOptional.isPresent()) {
            produitRepository.deleteById(id);
            return ResponseEntity.ok("Produit supprimé avec succès.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // PUT update produit
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduit(@PathVariable Long id, @RequestBody Produit updatedProduit) {
        Optional<Produit> produitOpt = produitRepository.findById(id);
        if (!produitOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Produit produit = produitOpt.get();

        // Màj champs
        produit.setTitre(updatedProduit.getTitre());
        produit.setDescription(updatedProduit.getDescription());
        produit.setCategorie(updatedProduit.getCategorie());
        produit.setLocalisation(updatedProduit.getLocalisation());
        produit.setStatut(updatedProduit.getStatut());

        // Mise à jour photo uniquement si non null
        if (updatedProduit.getPhoto() != null) {
            produit.setPhoto(updatedProduit.getPhoto());
        }

        produitRepository.save(produit);
        return ResponseEntity.ok(produit);
    }
}
