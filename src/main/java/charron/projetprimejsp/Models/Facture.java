package charron.projetprimejsp.Models;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "factures")
public class Facture
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFacture;

    // Pourrait ajouter une vrai relation
    @Column
    private String usernameClient;

    @Column
    private LocalDate dateAchat;

    // Snapshot de la liste de produit
    @Lob
    private String produits;

    // BigDecimal
    @Column
    private Double montantTotal;

    // -------------------------------------------------------------------------
    // CONSTRUCTORS
    // -------------------------------------------------------------------------

    public Facture() {}

    // À utiliser lors de la commande automatise la date
    public Facture(String usernameClient, String produits, Double montantTotal)
    {
        this.usernameClient = usernameClient;
        this.dateAchat = LocalDate.now();
        this.produits = produits;
        this.montantTotal = montantTotal;
    }

    public Facture(String usernameClient, LocalDate dateAchat, String produits, Double montantTotal) {
        this.usernameClient = usernameClient;
        this.dateAchat = dateAchat;
        this.produits = produits;
        this.montantTotal = montantTotal;
    }

    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------

    public Integer getIdFacture() {
        return idFacture;
    }

    public String getUsernameClient() {
        return usernameClient;
    }

    public LocalDate getDateAchat() {
        return dateAchat;
    }

    public String getProduits() {
        return produits;
    }

    public Double getMontantTotal() {
        return montantTotal;
    }

    // -------------------------------------------------------------------------
    // SETTERS
    // -------------------------------------------------------------------------

    public void setIdFacture(Integer idFacture) {
        this.idFacture = idFacture;
    }

    public void setUsernameClient(String usernameClient) {
        this.usernameClient = usernameClient;
    }

    public void setDateAchat(LocalDate dateAchat) {
        this.dateAchat = dateAchat;
    }

    public void setProduits(String produits) {
        this.produits = produits;
    }

    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }
}
