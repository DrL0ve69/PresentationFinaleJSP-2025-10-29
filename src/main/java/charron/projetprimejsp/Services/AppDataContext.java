package charron.projetprimejsp.Services;

import charron.projetprimejsp.Models.Client;
import charron.projetprimejsp.Models.Facture;
import charron.projetprimejsp.Models.Produit;
import charron.projetprimejsp.Repositories.ClientRepository;
import charron.projetprimejsp.Repositories.FactureRepository;
import charron.projetprimejsp.Repositories.ProduitRepository;
import charron.projetprimejsp.Repositories.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppDataContext
{
    private final ProduitRepository produitRepository;
    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AppDataContext(ProduitRepository produitRep, ClientRepository clientRep, RoleRepository roleRep, FactureRepository factureRep)
    {
        this.produitRepository = produitRep;
        this.factureRepository = factureRep;
        this.clientRepository = clientRep;
        this.roleRepository = roleRep;
    }

    // SECTION PRODUIT
    // Retourner tous les produits
    public List<Produit> selectAllProduits()
    {
        return produitRepository.findAll();
    }

    // TOP 8 PRODUITS RÉCENTS POUR PAGE ACCUEIL
    public List<Produit> selectTop8NewestProduits()
    {
        return produitRepository.findTop8ByOrderByIdProduitDesc();
    }

    // Chercher un produit par ID
    public Produit selectByIdProduit(int idProduit)
    {
        return produitRepository.findById(idProduit)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable id=" + idProduit));
    }
    // Ajouter un produit à la base de données
    public void insertProduit(Produit produit)
    {
        produitRepository.save(produit);
    }
    // update objet
    public void updateProduit(Produit produit)
    {
        produitRepository.save(produit);
    }
    // update par Id
    public void updateByIdProduit(int idProduit, Produit newProduit)
    {
        Produit produit = produitRepository.findById(idProduit)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable"));
        produit.setNom(newProduit.getNom());
        produit.setPrixUnitaire(newProduit.getPrixUnitaire());
        produit.setImage(newProduit.getImage());
        produitRepository.save(produit);
    }
    // supprimer objet
    public void deleteProduit(Produit produit)
    {
        produitRepository.delete(produit);
    }
    // supprimer par IdProduit
    public void deleteByIdProduit(int idProduit)
    {
        produitRepository.deleteById(idProduit);
    }


    // SECTION CLIENT
    // Retourner tous les clients

    // Trouver client par username
    public Client findClientByUsername(String username)
    {
        return clientRepository.findById(username).orElse(null);
    }
    // Supprime Client
    public void deleteClientByUsername(String username)
    {
        clientRepository.deleteById(username);
    }


    // SECTION FACTURE
    // Retourner toutes les factures pour les comptes admin seulement
    public List<Facture> selectAllFactures()
    {
        return factureRepository.findAll();
    }

    public List<Facture> selectAllFactureByUsername(String username)
    {
        return factureRepository.findAllByUsernameClient(username).orElse(null); // ou liste vide à voir
    }

    public Facture selectById(Integer idFacture)
    {
        return factureRepository.findById(idFacture).orElse(null);
    }

    //supprimer objet
    public void deleteFacture(Facture facture)
    {
        factureRepository.delete(facture);
    }
    // supprimer par IdFacture
    public void deleteByIdFacture(Integer idFacture)
    {
        factureRepository.deleteById(idFacture);
    }
    /*

    // Trouver une facture par son ID
    public Facture selectByIdFacture(String idFacture)
    {
        return factureRepository.findById(idFacture)
                .orElseThrow(() -> new EntityNotFoundException("Facture introuvable id=" + idFacture));
    }
    // Ajouter une facture à la base de données
    public void insertFacture(Facture facture)
    {
        //facture.setMontantTotal(); // Pour ajuster le montant total si cela n'est pas déjà fait.
        factureRepository.save(facture);
    }
    // update objet
    public void updateFacture(Facture facture)
    {
        factureRepository.save(facture);
    }
    // update par Id
    public void updateByIdFacture(String idFacture)
    {
        Facture facture = factureRepository.findById(idFacture)
                .orElseThrow(() -> new EntityNotFoundException("Facture introuvable"));
        // Si la liste de produits changent, mettre le montantTotal à jour
        //facture.setMontantTotal();
        factureRepository.save(facture);
    }
    //supprimer objet
    public void deleteFacture(Facture facture)
    {
        factureRepository.delete(facture);
    }
    // supprimer par IdFacture
    public void deleteByIdFacture(String idFacture)
    {
        factureRepository.deleteById(idFacture);
    }
    */



    // Cherche
    // Retourner tous les factures d'un seul client

    // SECTION COMPAGNIE
    // Retourner toutes les compagnies
    /*
    public List<Compagnie> selectAllCompagnies()
    {
        return compagnieRepository.findAll();
    }
    // Trouver la compagnie par ID
    public Compagnie selectByIdCompagnie(int idCompagnie)
    {
        return compagnieRepository.findById(idCompagnie)
                .orElseThrow(() -> new EntityNotFoundException("Compagnie introuvable id=" + idCompagnie));
    }
    // Ajouter une nouvelle compagnie à la base de données
    public void insertCompagnie(Compagnie compagnie)
    {
        compagnieRepository.save(compagnie);
    }
    // Update une compagnie
    public void updateCompagnie(Compagnie compagnie)
    {
        compagnieRepository.save(compagnie);
    }
    // Update avec un ID
    public void updateByIdCompagnie(int idCompagnie)
    {
        Compagnie compagnie = compagnieRepository.findById(idCompagnie).orElse(null);
        if (compagnie == null)
        {
            throw new EntityNotFoundException("Compagnie introuvable");
        }
        else { compagnieRepository.save(compagnie); }

    }
    // Supprimer par objet
    public void deleteCompagnie(Compagnie compagnie)
    {
        compagnieRepository.delete(compagnie);
    }
    // Supprimer avec ID
    public void deleteByIdCompagnie(int idCompagnie)
    {
        compagnieRepository.deleteById(idCompagnie);
    }

     */
}
