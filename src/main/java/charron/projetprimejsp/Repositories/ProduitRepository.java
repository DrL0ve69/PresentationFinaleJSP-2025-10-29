package charron.projetprimejsp.Repositories;

import charron.projetprimejsp.Models.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Integer>
{
    List<Produit> findTop8ByOrderByIdProduitDesc();
}
