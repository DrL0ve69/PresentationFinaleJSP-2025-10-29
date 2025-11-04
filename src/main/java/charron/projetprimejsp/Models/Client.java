package charron.projetprimejsp.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class Client implements UserDetails
{
    @Id
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Le nom d'utilisateur est requis.")
    private String username;

    @Column(unique = true, nullable = false)
    @Email(message = "L'adresse email doit être valide.")
    @NotBlank(message = "L'email est requis.")
    private String email;

    @Column(nullable = false)
    @NotNull(message = "La date de naissance est requise.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateNaissance;

    @Column(nullable = true)
    @Lob
    private byte[] photo;

    @Column(nullable = false)
    @Size(min = 4, message = "Le mot de passe doit avoir au moins 4 caractères.")
    @NotBlank(message = "Le mot de passe est requis.")
    private String password;

    //@OneToMany(fetch = FetchType.EAGER)
    //@Column(nullable = true)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_roles",
            joinColumns = @JoinColumn(name = "client_username", referencedColumnName = "username"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "idRole")
    )
    private List<Role> roles;


    // -------------------------------------------------------------------------
    // CONSTRUCTORS
    // -------------------------------------------------------------------------

    // Constructeur avec le un string pour role
    public Client(String username, String email, String plainPassword, String roleName, LocalDate dateNaissance)
    {
        this.username = username;
        this.email = email;
        this.dateNaissance = dateNaissance;
        //this.photo = photo;
        this.password = plainPassword;
        this.roles = new ArrayList<>();
        roles.add(new Role(roleName));
    }

    // Constructeur avec le un string pour role
    public Client(String username, String email, String plainPassword, String roleName, LocalDate dateNaissance, byte[] photo)
    {
        this.username = username;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.photo = photo;
        this.password = plainPassword;
        this.roles = new ArrayList<>();
        this.addRole(new Role(roleName));
    }


    // @NoArgsConstructor
    public Client()
    {
    }

    // @AllArgsConstructor
    public Client(String username, String email, LocalDate dateNaissance, byte[] photo, String password, List<Role> roles)
    {
        this.username = username;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.photo = photo;
        this.password = password;
        this.roles = roles;
    }

    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------

    // The method for getUsername() is already explicitly defined below
    // public String getUsername() { return username; }

    public String getEmail() {
        return email;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public byte[] getPhoto() {
        return photo;
    }

    // The method for getPassword() is already explicitly defined below
    // public String getPassword() { return password; }

    public List<Role> getRoles() {
        return roles;
    }

    // -------------------------------------------------------------------------
    // SETTERS
    // -------------------------------------------------------------------------

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    // -------------------------------------------------------------------------
    // USERDETAILS AND HELPER METHODS (from your original class)
    // -------------------------------------------------------------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return roles;
    }

    public void addRole(Role role)
    {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }

    public List<String> listeRoleToString()
    {
        List<String> listeRoles = new ArrayList<>();

        if (roles != null) {
            roles.forEach(role -> listeRoles.add(role.getAuthority()));
        }
        return listeRoles;
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Crée la table de liaison entre nomRole&client
    /*
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_roles",
            joinColumns = @JoinColumn(name = "client_username"),
            inverseJoinColumns = @JoinColumn(name = "nomRole")
    )

     */
    // On peut ajouter une liste de facture aux clients, ou bien par la recherche dans la table factures mappedBy = "client", cascade = CascadeType.ALL
    /*
    @OneToMany()
    @Column(nullable = true)
    private List<Facture> listeFactures;

     */


}
