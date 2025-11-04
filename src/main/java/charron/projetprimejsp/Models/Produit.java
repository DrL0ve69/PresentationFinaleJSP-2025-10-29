package charron.projetprimejsp.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "produits")
@Data
public class Produit {
    @Id // Clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Default AUTO
    private Integer idProduit;

    @Column(length = 50)
    private String nom;

    @Column(name = "prixUnitaire", nullable = false)
    private double prixUnitaire;


    @Lob // Indique que c'est un Large Object (généralement TEXT ou CLOB en SQL)
    @Column(nullable = true)
    private byte[] image;


    public Produit(String nom, double prixUnitaire, byte[] image) {
        this.nom = nom;
        this.prixUnitaire = prixUnitaire;
        this.image = image;
    }

    public Produit(int idProduit, String nom, double prixUnitaire, byte[] image) {
        this.idProduit = idProduit;
        this.nom = nom;
        this.prixUnitaire = prixUnitaire;
        this.image = image;
    }

    public Produit() {
    }

    public int getIdProduit() {
        return this.idProduit;
    }

    public String getNom() {
        return this.nom;
    }

    public double getPrixUnitaire() {
        return this.prixUnitaire;
    }

    public byte[] getImage() {
        return this.image;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public void setImage(byte[] image) {
        this.image = image;
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
