package charron.projetprimejsp.Repositories;

import charron.projetprimejsp.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>
{

    // Permet de retrouver un rôle par son nom (authority)
    //Role findByAuthority(String role);

    Role findByNomRole(String roleName);
}
