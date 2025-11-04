package charron.projetprimejsp.Services;

import charron.projetprimejsp.Models.Produit;
import charron.projetprimejsp.Repositories.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProduitCreationService
{
    @Autowired
    private ProduitRepository repo;

    public Produit createProduit(String nom, Double prix, byte[] image)
    {
        Produit produit = new Produit(nom, prix, image);
        repo.save(produit);
        return produit;
    }
}
