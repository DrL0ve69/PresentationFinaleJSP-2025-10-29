package charron.projetprimejsp.Controllers;

import charron.projetprimejsp.Models.Facture;
import charron.projetprimejsp.Models.Panier;
import charron.projetprimejsp.Models.Produit;
import charron.projetprimejsp.Repositories.FactureRepository;
import charron.projetprimejsp.Repositories.ProduitRepository;
import charron.projetprimejsp.Services.SessionManager;
import charron.projetprimejsp.ViewModels.ProduitViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.DecimalFormat;
import java.util.List;

/*
/panier/get → retourne la liste des ProduitViewModel

/panier/ajouter/{id} → ajoute 1 unité

/panier/retirer/{id} → retire 1 unité (supprime si quantité = 0)

/panier/supprimer/{id} → supprime complètement le produit

/panier/vider → vide le panier

/panier/count → retourne le nombre total d’articles
 */
// Gestion du panier
// RestController pour le AJAX

@Controller
@RequestMapping("/panier")
public class PanierController
{
    private static final String PANIER_KEY = "panier";

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private SessionManager<Panier> sessionManager;

    // Récupère ou crée un panier en session
    private Panier getOrCreatePanier() {
        Panier panier = sessionManager.getAttribute(PANIER_KEY);
        if (panier == null) {
            panier = new Panier();
            sessionManager.setAttribute(PANIER_KEY, panier);
        }
        return panier;
    }
    @GetMapping("/afficherPanier")
    public ModelAndView afficherPanier() {
        Panier panier = getOrCreatePanier();
        return new ModelAndView("panier","produits", panier.getProduits());
    }

    // Retourne la liste complète du panier
    @GetMapping("/get")
    @ResponseBody
    public ResponseEntity<List<ProduitViewModel>> getPanier() {
        Panier panier = getOrCreatePanier();
        return ResponseEntity.ok(panier.getProduits());
    }

    // Ajouter 1 unité DEVRAIT ETRE UN GET OU BIEN CHANGER LA MÉTHODE DANS PANIER.JS
    @PostMapping("/ajouter/{id}")
    @ResponseBody
    public ResponseEntity<String> ajouterProduit(@PathVariable int id)
    {
        try
        {
            Produit produit = produitRepository.findById(id).orElse(null);
            if (produit == null) return ResponseEntity.badRequest().body("Produit inexistant");

            Panier panier = getOrCreatePanier();
            panier.ajouterProduit(produit);

            sessionManager.setAttribute(PANIER_KEY, panier);
            return ResponseEntity.ok("Produit ajouté");
        }catch (Exception e) {

            return ResponseEntity.internalServerError().body("Erreur serveur: " + e.getMessage());
        }

    }

    // Retirer 1 unité
    @PostMapping("/retirer/{id}")
    @ResponseBody
    public ResponseEntity<String> retirerProduit(@PathVariable int id) {
        Panier panier = getOrCreatePanier();
        panier.soustraireProduit(id);
        sessionManager.setAttribute(PANIER_KEY, panier);
        return ResponseEntity.ok("Produit retiré");
    }

    // Supprimer complètement un produit
    @PostMapping("/supprimer/{id}")
    @ResponseBody
    public ResponseEntity<String> supprimerProduit(@PathVariable int id) {
        Panier panier = getOrCreatePanier();
        panier.retirerProduit(id);
        sessionManager.setAttribute(PANIER_KEY, panier);
        return ResponseEntity.ok("Produit supprimé");
    }

    // Vider le panier
    @PostMapping("/vider")
    @ResponseBody
    public ResponseEntity<String> viderPanier() {
        Panier panier = getOrCreatePanier();
        panier.getProduits().clear();
        sessionManager.setAttribute(PANIER_KEY, panier);
        return ResponseEntity.ok("Panier vidé");
    }

    // Nombre total d’articles
    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Integer> countPanier() {
        Panier panier = getOrCreatePanier();
        return ResponseEntity.ok(panier.getTotalArticles());
    }

    // Passer la commande :
    @PostMapping("/passerCommande")
    @ResponseBody
    public ResponseEntity<String> passerCommande()
    {
        Panier panier = getOrCreatePanier();

        if (panier.getProduits().isEmpty())
        {
            // RÉPONSE NÉGATIVE SI PANIER DE LA SESSION EST VIDE
            return ResponseEntity.badRequest().body("Le panier est vide. Impossible de passer une commande.");
        }

        try {
            // 1. Trouver l'utilisateur authentifié

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // Retourne le id(username?)

            if(username.equals("anonymousUser") || username.isEmpty())
            {
                // RÉPONSE NÉGATIVE SI PANIER DE LA SESSION EST VIDE
                return ResponseEntity.badRequest().body("Commande impossible! Vous devez vous connecter.");
            }

            // 2. Préparer le snapshot ^pour la base de donnée
            // Construire le string qui représente les produits
            StringBuilder produitsSnapshot = new StringBuilder();
            DecimalFormat df = new DecimalFormat("0.00");
            for (ProduitViewModel pvm : panier.getProduits())
            {
                produitsSnapshot.append(
                        String.format("%s (Qté: %d @ %s$); ",
                                pvm.getProduit().getNom(),
                                pvm.getQuantite(),
                                df.format(pvm.getProduit().getPrixUnitaire())
                        )
                );
            }

            // 3. Créer et sauvegarder la facture
            Facture facture = new Facture(
                    username,
                    produitsSnapshot.toString().trim(),
                    panier.getTotal()
            );
            factureRepository.save(facture);

            // 4. Vider le panier
            panier.getProduits().clear();
            sessionManager.setAttribute(PANIER_KEY, panier);

            return ResponseEntity.ok("Commande passée avec succès ! Facture #" + facture.getIdFacture());

        } catch (Exception e) {
            // imrpime l'erreur
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur lors de la commande: " + e.getMessage());
        }
    }
}
