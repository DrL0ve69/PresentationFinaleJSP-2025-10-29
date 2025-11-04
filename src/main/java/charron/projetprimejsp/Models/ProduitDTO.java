package charron.projetprimejsp.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
@Data
public class ProduitDTO
{

    private Integer idProduit;

    private String nom;

    private double prixUnitaire;

    private String image;
    public ProduitDTO(){}
    public ProduitDTO(Integer idProduit, String nom, double prixUnitaire, String image)
    {
        this.idProduit = idProduit;
        this.nom = nom;
        this.prixUnitaire = prixUnitaire;
        this.image = image;
    }
    public String getNom() {return nom;}
    public double getPrixUnitaire() {return prixUnitaire;}
    public String getImage() {return image;}
    public Integer getIdProduit() {return idProduit;}

    public void setNom(String nom) {this.nom = nom;}
    public void setImage(String image) {this.image = image;}

    public void setIdProduit(Integer idProduit) {
        this.idProduit = idProduit;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }
    // Relation avec la compagnie qui vend le produit pourrait ajouter facture aussi.
    // Si la compagnie est supprimée, les produits aussi.
    /*
    @ManyToOne(optional = false)
    @JoinColumn(name = "idCompagnie", referencedColumnName = "idCompagnie")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Compagnie compagnie;

     */
}
