package charron.projetprimejsp.Controllers;

import charron.projetprimejsp.Models.Client;
import charron.projetprimejsp.Models.Produit;
import charron.projetprimejsp.Models.ProduitDTO;
import charron.projetprimejsp.Models.Role;
import charron.projetprimejsp.Repositories.ClientRepository;
import charron.projetprimejsp.Repositories.FactureRepository;
import charron.projetprimejsp.Repositories.RoleRepository;
import charron.projetprimejsp.Services.AppDataContext;
import charron.projetprimejsp.Services.ClientCreationService;
import charron.projetprimejsp.Services.ProduitCreationService;
import charron.projetprimejsp.ViewModels.ProduitViewModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasAuthority('admin')")// accessible uniquement aux Admins
@RequestMapping("/admin")
public class AdminController
{
    private ModelMapper modelMapper;
    private final AppDataContext appContext;
    private final ProduitCreationService produitCreationService;
    private ClientCreationService clientCreationService;
    private RoleRepository roleRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    public AdminController(AppDataContext appContext,
                           ProduitCreationService produitCreationService,
                           ModelMapper modelMapper,
                           ClientCreationService clientCreationService,
                           FactureRepository factureRepository,
                           RoleRepository roleRepository)
    {
        this.appContext = appContext;
        this.produitCreationService = produitCreationService;
        this.modelMapper = modelMapper;
        this.clientCreationService = clientCreationService;
        this.factureRepository = factureRepository;
        this.roleRepository = roleRepository;
    }

    // Affichage de tous les produits
    @GetMapping("/produits")
    public ModelAndView produits(Model model)
    {
        List<Produit> produitsOG = appContext.selectAllProduits();
        List<ProduitDTO> produitDTO = produitsOG.stream().map(p -> modelMapper.map(p, ProduitDTO.class)).toList();

        model.addAttribute("produits", produitDTO);
        model.addAttribute("produitForm", new Produit());
        model.addAttribute("messageSucces","Opération effectuée avec succèes");
        return new ModelAndView("admin-produit");
    }

    // Ajouter un produit
    @PostMapping("/produits/ajouter")
    public String ajouterProduit(@RequestParam("nom") String nom, @RequestParam("prixUnitaire") Double prix,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException
    {
        produitCreationService.createProduit(nom,prix,imageFile.getBytes());
        return "redirect:/admin/produits";
    }

    // Supprimer un produit
    @GetMapping("/produits/supprimer/{id}")
    public String supprimerProduit(@PathVariable("id") int id) {
        appContext.deleteByIdProduit(id);
        return "redirect:/admin/produits";
    }

    // Formulaire modification produit
    /*
    Se trouve dans un modal donc pas nécessaire
    @GetMapping("/produits/modifier/{id}")
    public String modifierProduitForm(@PathVariable("id") int id, Model model) {
        Produit produit = appContext.selectByIdProduit(id);
        model.addAttribute("produitForm", produit);
        model.addAttribute("produits", appContext.selectAllProduits());
        // Ajouter Succes ou erreur
        return "redirect:/admin/produits";
    }

     */

    // Enregistrer modification
    @PostMapping("/produits/modifier")
    public String modifierProduit(@ModelAttribute("produitForm") Produit produit,
                                  @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            produit.setImage(imageFile.getBytes());
        }
        appContext.updateProduit(produit);
        return "redirect:/admin/produits";
    }

    // #######################################
    // SECTION POUR PAGE CLIENT
    // #####################################
    // Liste des clients
    @GetMapping("/clients")
    public ModelAndView clients()
    {
        // La liste des clients
        List<Client> allClients = clientRepository.findAll();

        // Liste des rôle
        List<Role> allRoles = roleRepository.findAll();

        ModelAndView mv = new ModelAndView("admin-clients");
        mv.addObject("clients", allClients);
        mv.addObject("allRoles", allRoles);
        // Pour besoin futur
        mv.addObject("clientForm", new Client());

        return mv;
    }

    // Suppression des clients
    @GetMapping("/clients/supprimer/{username}")
    public String supprimerClient(@PathVariable("username") String usernameToDelete,
                                  Authentication auth,
                                  RedirectAttributes redirectAttributes) {

        // Récupérer le nom d'utilisateur
        String currentAdminUsername = auth.getName();

        // 1. VÉRIFICATION DE SÉCURITÉ : Empêcher la suppression de son propre compte
        if (currentAdminUsername.equals(usernameToDelete)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de supprimer votre propre compte administrateur.");
            return "redirect:/admin/clients";
        }

        try
        {
            // 2. TENTER LA SUPPRESSION
            appContext.deleteClientByUsername(usernameToDelete);
            redirectAttributes.addFlashAttribute("successMessage", "Le compte '" + usernameToDelete + "' a été supprimé avec succès.");
        } catch (Exception e)
        {
            // (utilisateur non trouvé, problème de DB, etc.)
            System.err.println("Erreur lors de la suppression du client: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression du compte : " + e.getMessage());
        }

        // Redirection vers la liste des clients
        return "redirect:/admin/clients";
    }

    // Choix de nouveaux ou anciens rôle:
    // Assigner un rôle (existant ou nouveau) à un client
    @PostMapping("/clients/assignRole")
    public String assignRoleToClient(@RequestParam("username") String username,
                                     @RequestParam("roleName") String roleName,
                                     RedirectAttributes redirectAttributes)
    {

        if (roleName == null || roleName.trim().isEmpty())
        {
            redirectAttributes.addFlashAttribute("errorMessage", "Le nom du rôle ne peut pas être vide.");
            return "redirect:/admin/clients";
        }

        try
        {

            clientCreationService.addRoleToClient(username, roleName.trim());

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Le rôle '" + roleName.trim() + "' a été assigné au client '" + username + "' avec succès."
            );

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Erreur lors de l'assignation du rôle : " + e.getMessage()
            );
        }

        return "redirect:/admin/clients";
    }

    // AJOUTER UN RÔLE
    // Promote un client au rôle d'administrateur
    /*
    @GetMapping("/clients/addRole/{username}")
    public String promouvoirClientAdmin(@PathVariable String username, RedirectAttributes redirectAttributes) {

        final String ROLE_NAME = "admin"; // Le rôle à ajouter

        try {
            // Calling the service method with the resilient "find or create role" logic
            clientCreationService.addRoleToClient(username, ROLE_NAME);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Le client '" + username + "' a été promu avec succès au rôle d'Admin."
            );

        } catch (RuntimeException e) {
            // Handles cases where the client might not be found or other errors
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Erreur lors de la promotion du client : " + e.getMessage()
            );
        }

        return "redirect:/admin/clients";
    }

     */

    // #######################################
    // SECTION POUR PAGE factures
    // #####################################

    // La vue
    @GetMapping("/factures")
    public ModelAndView factures()
    {
        List<?> allFactures = appContext.selectAllFactures();

        ModelAndView mv = new ModelAndView("admin-factures");
        mv.addObject("factures", allFactures);

        return mv;
    }

    @GetMapping("/factures/supprimer/{id}")
    public String supprimerFacture(@PathVariable("id") Integer idFacture,
                                   RedirectAttributes redirectAttributes) {

        try
        {

            appContext.deleteByIdFacture(idFacture);

            redirectAttributes.addFlashAttribute("successMessage",
                    "La facture N° " + idFacture + " a été supprimée avec succès.");

        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de la facture: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erreur lors de la suppression de la facture N° " + idFacture + " : " + e.getMessage());
        }

        return "redirect:/admin/factures";
    }
}
