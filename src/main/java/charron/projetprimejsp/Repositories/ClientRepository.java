package charron.projetprimejsp.Repositories;

import charron.projetprimejsp.Models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String>
{
    Optional<Client> findByEmail(String email);
}
