package charron.projetprimejsp.Configurations;

import charron.projetprimejsp.Services.ClientCreationService;
import charron.projetprimejsp.Services.ProduitCreationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SeedInitializer implements CommandLineRunner
{
    private final ClientCreationService clientCreationService;
    private final ProduitCreationService produitCreationService;

    public SeedInitializer(ClientCreationService clientCreationService, ProduitCreationService produitCreationService)
    {
        this.clientCreationService = clientCreationService;
        this.produitCreationService = produitCreationService;
    }


    @Override
    public void run(String... args)
    {
        // Mettre la condition de création: Si la liste de user est vide ou null on mets ceux-ci

/*
        // Crée un client USER
        clientCreationService.createClient("john", "john@example.com", "password123", "User", LocalDate.now(), null);

        // Crée un admin
        clientCreationService.createClient("admin", "admin@example.com", "admin123", "Admin", LocalDate.now(), null);

        produitCreationService.createProduit("Pommes",12.99,null);
        produitCreationService.createProduit("Bananes",18.99,null);
        produitCreationService.createProduit("Raisins",69.00,null);



        System.out.println("Clients initiaux créés !");
*/

    }


}
