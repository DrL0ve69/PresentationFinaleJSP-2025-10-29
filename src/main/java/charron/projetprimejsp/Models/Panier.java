package charron.projetprimejsp.Models;

import charron.projetprimejsp.ViewModels.ProduitViewModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// Lombok problème expliquer !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
@Data
public class Panier
{
    //private Client client;

    // Changer pour le ProduitViewModel qui gère la quantité
    private List<ProduitViewModel> produits = new ArrayList<ProduitViewModel>();

    public List<ProduitViewModel> getProduits()
    {
        return produits;
    }
    public void setProduits(List<ProduitViewModel> produits)
    {
        this.produits = produits;
    }

    // 🔹 Ajouter un produit au panier
    public void ajouterProduit(Produit produit) {
        // On vérifie si le produit existe déjà dans le panier
        for (ProduitViewModel pvm : produits) {
            if (pvm.getProduit().getIdProduit() == produit.getIdProduit()) {
                // Incrémente la quantité existante
                pvm.setQuantite(pvm.getQuantite() + 1);
                return;
            }
        }
        // Sinon, on ajoute un nouveau ProduitViewModel avec quantité = 1
        produits.add(new ProduitViewModel(produit, 1, null));
    }

    // Enlever 1 de la qté
    public void soustraireProduit(int idProduit) {
        // Vérifier que le produit est bel et bien dans le panier
        for (ProduitViewModel pvm : produits) {
            if (pvm.getProduit().getIdProduit() == idProduit) {
                // Si qté = 0 on le retire complètement
                if (pvm.getQuantite() == 1) {
                    retirerProduit(pvm);
                }
                pvm.setQuantite(pvm.getQuantite() - 1);
                return;
            }
        }

    }

    // Méthode pour enlever un produit
    public void retirerProduit(ProduitViewModel produit) {
        this.produits.remove(produit);
    }

    // 🔹 Retirer complètement un produit
    public void retirerProduit(int idProduit) {
        produits.removeIf(pvm -> pvm.getProduit().getIdProduit() == idProduit);
    }


    // 🔹 Calculer le total du panier
    public double getTotal() {
        double total = 0.00;
        for (ProduitViewModel pvm : produits)
        {
            total += pvm.getProduit().getPrixUnitaire() * pvm.getQuantite();
        }
        return total;
    }

    // 🔹 Obtenir le nombre total d’articles (quantités cumulées)
    public int getTotalArticles() {
        int total = 0;
        for (ProduitViewModel pvm : produits) {
            total += pvm.getQuantite();
        }
        return total;
    }

}

