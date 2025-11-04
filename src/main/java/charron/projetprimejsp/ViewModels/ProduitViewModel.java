package charron.projetprimejsp.ViewModels;

import charron.projetprimejsp.Models.Produit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class ProduitViewModel {
    private Produit produit;
    private int quantite;
    private String imagePVM;

    public ProduitViewModel() {}
    public ProduitViewModel(Produit produit, int quantite, String imagePVM) {
        this.produit = produit;
        this.quantite = quantite;
        this.imagePVM = imagePVM;
    }



    public void setImagePVM() {
        if (produit.getImage() != null) {
            imagePVM = Base64.getEncoder().encodeToString(produit.getImage());
        }

    }

    public Produit getProduit() {
        return this.produit;
    }

    public int getQuantite() {
        return this.quantite;
    }

    public String getImagePVM() {
        return this.imagePVM;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void setImagePVM(String imagePVM) {
        this.imagePVM = imagePVM;
    }
/*
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ProduitViewModel)) return false;
        final ProduitViewModel other = (ProduitViewModel) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$produit = this.getProduit();
        final Object other$produit = other.getProduit();
        if (this$produit == null ? other$produit != null : !this$produit.equals(other$produit)) return false;
        if (this.getQuantite() != other.getQuantite()) return false;
        final Object this$imagePVM = this.getImagePVM();
        final Object other$imagePVM = other.getImagePVM();
        if (this$imagePVM == null ? other$imagePVM != null : !this$imagePVM.equals(other$imagePVM)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ProduitViewModel;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $produit = this.getProduit();
        result = result * PRIME + ($produit == null ? 43 : $produit.hashCode());
        result = result * PRIME + this.getQuantite();
        final Object $imagePVM = this.getImagePVM();
        result = result * PRIME + ($imagePVM == null ? 43 : $imagePVM.hashCode());
        return result;
    }

    public String toString() {
        return "ProduitViewModel(produit=" + this.getProduit() + ", quantite=" + this.getQuantite() + ", imagePVM=" + this.getImagePVM() + ")";
    }

 */
}
