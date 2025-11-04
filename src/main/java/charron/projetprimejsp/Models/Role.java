package charron.projetprimejsp.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Entity
@Table(name = "roles")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class Role implements GrantedAuthority
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRole;

    @Column(nullable = false, unique = true)
    private String nomRole; // ex: "ROLE_USER", "ROLE_ADMIN" suppression du préfixe avec le bean

    @ManyToMany(mappedBy = "roles")
    private List<Client> clients;

    public Role(){}

    // Constructeur avec le nomRole seulement ID générer par IDENTITY
    public Role(String authority)
    {
        this.nomRole = authority;
    }

    public Role(String nomRole, List<Client> clients)
    {
        this.nomRole = nomRole;
        this.clients = clients;
    }

    @Override
    public String getAuthority()
    {
        return nomRole;
    }

    @Override
    public String toString(){return nomRole;}

    public String getNomRole(){return nomRole;}
    public Long getIdRole(){return idRole;}
    public void setNomRole(String nomRole){this.nomRole = nomRole;}

    // Getter and Setter for the new clients field
    public List<Client> getClients() { return clients; }
    public void setClients(List<Client> clients) { this.clients = clients; }

}
