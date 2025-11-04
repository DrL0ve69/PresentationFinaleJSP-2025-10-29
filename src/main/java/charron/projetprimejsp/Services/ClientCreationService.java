package charron.projetprimejsp.Services;

import charron.projetprimejsp.Models.Client;
import charron.projetprimejsp.Models.Role;
import charron.projetprimejsp.Repositories.ClientRepository;
import charron.projetprimejsp.Repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDate;

@Service
public class ClientCreationService
{
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Client createClient(String username, String email, String plainPassword, String roleName, LocalDate dateNaissance, byte[] photo)
    {
        // 1. Vérifier si le username existe
        if (clientRepository.findById(username).isPresent()) {
            // Attrapé par le controlleur
            throw new DataIntegrityViolationException("Le nom d'utilisateur est déjà utilisé.");
        }
        // 2. Idem pour le email
        if (clientRepository.findByEmail(email).isPresent())
        {
            throw new DataIntegrityViolationException("L'adresse email est déjà utilisée.");
        }
        Role role = roleRepository.findByNomRole(roleName);
        if (role == null || role.getNomRole() == null)
        {
            role = new Role(roleName);
            roleRepository.save(role);
        }

        // Encode password
        String encodedPassword = passwordEncoder.encode(plainPassword);

        // Créer le client. devrait ajouter une condition
        Client client = new Client();
        client.setUsername(username);
        client.setEmail(email);
        client.setPassword(encodedPassword);
        client.setDateNaissance(dateNaissance);
        // Ajouter validation
        client.setPhoto(photo);
        client.addRole(role);

        clientRepository.save(client);
        return client;
    }


    // Pour update utilisateur
    public Client updateClientDetails(
            String username,
            LocalDate dateNaissance,
            String newPlainPassword,
            byte[] newPhotoBytes)
    {
        // 1. Trouve le client
        Client existingClient = clientRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("Client not found: " + username));


        // client aura une date de naissance, validation
        existingClient.setDateNaissance(dateNaissance);

        // 3. La photo
        if (newPhotoBytes != null && newPhotoBytes.length > 0) {
            existingClient.setPhoto(newPhotoBytes);
        }

        // 4. Password doit être modifier si et seulement si le champs est vide
        if (newPlainPassword != null && !newPlainPassword.trim().isEmpty()) // && newPass >= 4?
        {
            String encodedPassword = passwordEncoder.encode(newPlainPassword);
            existingClient.setPassword(encodedPassword);
        }

        // 5. Savaugard & update de la sécuritécontexte
        Client updatedClient = clientRepository.save(existingClient);
        refreshSecurityContext(updatedClient.getUsername());

        return updatedClient;
    }

    public Client saveClient(Client client)
    {
        return clientRepository.save(client);
    }

    private void refreshSecurityContext(String username)
    {
        // Doit rfraichir l'utilisateur
        UserDetails userDetails = clientDetailsService.loadUserByUsername(username);

        // Nouvelle objet doit être créer
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(), // Password déjà encodé
                userDetails.getAuthorities()
        );

        // Update the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    public void addRoleToClient(String username, String roleName) throws RuntimeException {
        // 1. Fetch Client
        Client client = clientRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("Client non trouvé : " + username));

        // 2. Fetch Role
        Role role = roleRepository.findByNomRole(roleName);

        if (role == null || role.getNomRole() == null)
        {
            role = new Role(roleName);
            roleRepository.save(role);
        }

        // ajout role
        client.addRole(role);

        //Save
        clientRepository.save(client);
    }
}
