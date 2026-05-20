package com.swapily.swapilybackend.Controller;

import com.swapily.swapilybackend.Entity.*;
import com.swapily.swapilybackend.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/echange")
@CrossOrigin(origins = "http://localhost:5173")
public class ImageEchangeController {

    @Autowired
    private ImageEchangeRepository imageEchangeRepository;

    @PostMapping("/image")
    public ResponseEntity<String> uploadImageEchange(
            @RequestParam("imageEchange") MultipartFile file,
            @RequestParam("utilisateurId") Long utilisateurId,
            @RequestParam("produitId") Long produitId,
            @RequestParam("demmandechangeId") Long demmandechangeId  // ajout param
    ) {
        try {
            byte[] imageBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            ImageEchange image = new ImageEchange();
            image.setImageData(base64Image);
            image.setUtilisateurId(utilisateurId);
            image.setProduitLieId(produitId);
            image.setDemandesechangeId(demmandechangeId);  // setter ajouté

            imageEchangeRepository.save(image);
            return ResponseEntity.ok("Image enregistrée avec succès");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erreur lors de l'enregistrement de l'image");
        }
    }


    @GetMapping("/images/utilisateur/{utilisateurId}")
    public List<ImageEchange> getImagesByUtilisateur(@PathVariable Long utilisateurId) {
        return imageEchangeRepository.findByUtilisateurId(utilisateurId);
    }

    @GetMapping("/images/produit/{produitId}")
    public List<ImageEchange> getImagesByProduit(@PathVariable Long produitId) {
        return imageEchangeRepository.findByProduitLieId(produitId);
    }
    @GetMapping("/images/demmande/{demmandechangeId}")
    public List<ImageEchange> getImagesByDemmande(@PathVariable Long demmandechangeId) {
        return imageEchangeRepository.findByDemmandechangeId(demmandechangeId);
    }
    @GetMapping("/all")
    public List<ImageEchange> getAllImages() {
        return imageEchangeRepository.findAll();
    }

}