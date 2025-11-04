package charron.projetprimejsp.Repositories;

import charron.projetprimejsp.Models.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Integer>
{
    Optional<List<Facture>> findAllByUsernameClient(String username);

    // Trouve toutes les factures pour un client donné, triées par date d'achat (la plus récente en premier)
    List<Facture> findAllByUsernameClientOrderByDateAchatDesc(String usernameClient);
}
