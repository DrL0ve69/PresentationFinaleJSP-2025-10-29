package charron.projetprimejsp.Controllers;

import charron.projetprimejsp.Models.Client;
import charron.projetprimejsp.Models.Facture;
import charron.projetprimejsp.Models.Produit;
import charron.projetprimejsp.Models.ProduitDTO;
import charron.projetprimejsp.Repositories.FactureRepository;
import charron.projetprimejsp.Services.AppDataContext;
import charron.projetprimejsp.Services.ClientCreationService;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/Home")
public class HomeController
{
    private AppDataContext appContext;
    private ClientCreationService clientCreationService;
    @Autowired
    private FactureRepository factureRepository;
    private ModelMapper modelMapper;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    public HomeController(AppDataContext appContext, ClientCreationService clientCreationService, ModelMapper modelMapper)
    {
        this.appContext = appContext;
        this.clientCreationService = clientCreationService;
        this.modelMapper = modelMapper;
    }

    // ===================================================================
    // FIX 1: InitBinder Pour le bug de la photo qui ne reconnaît pas null
    // ===================================================================
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Exclude 'photo' from automatic binding to the Client entity.
        // This allows us to handle the MultipartFile separately using @RequestParam.
        binder.setDisallowedFields("photo");
    }

    @GetMapping("/accueil")
    public ModelAndView accueil(@RequestParam(value = "logout", required = false) String logout)
    {
        ModelAndView mv = new ModelAndView("accueil");
        // SEULEMENT 8 PLUS RÉCENTS PRODUITS POUR PAGE ACCUEIL
        List<Produit> produitsOG = appContext.selectTop8NewestProduits();
        List<ProduitDTO> produitDTO = produitsOG.stream().map(p -> modelMapper.map(p, ProduitDTO.class)).toList();


        if (logout != null) {
            mv.addObject("successMessage", "Déconnexion réussie ! À bientôt.");
        }
        mv.addObject("produits", produitDTO);
        return mv;
    }

    // La vue login pourrait etre Response<Entity> pour etre injectée dans le main-content
    @GetMapping("/login")
    public ModelAndView login(@RequestParam(value = "error", required = false) String error)
    {
        ModelAndView mv = new ModelAndView("login");
        if (error != null) {
            mv.addObject("errorMessage", "Nom d’utilisateur ou mot de passe incorrect !");
        }
        return mv;
    }
    // Succes login
    @GetMapping("/login-success")
    public String loginSuccess(Authentication auth, RedirectAttributes redirectAttributes)
    {
        //Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //System.out.println("Logged in user: " + auth.getName());

        // Ajoute l'attribut lors de la redirection lien POST vers GET
        // Pas nécessaire de mettre le model dans le controlleur dû à la redirection
        redirectAttributes.addFlashAttribute("successMessage", "Connexion réussie! Bienvenue.");
        // Si le client(user) est Admin

        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("admin")))
        {
            return "redirect:/admin/produits";
        }
        // Redirect to your accueil page
        return "redirect:/Home/accueil";
    }

    // Formulaire register:
    @GetMapping("/register")
    public String register(Model model)
    {
        if (!model.containsAttribute("client"))
        {
            model.addAttribute("client", new Client());
        }
        return "register";
    }


    @PostMapping("/register")
    public String processRegister(@Valid Client client,
                                  BindingResult bindingResult,
                                  @RequestParam(value = "photo", required = false) MultipartFile photo,
                                  RedirectAttributes redirectAttributes)
    {

        // 1. VÉRIFIER LES ERREURS
        if (bindingResult.hasErrors())
        {
            // sur l'attribut th:fields
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.client", bindingResult);
            redirectAttributes.addFlashAttribute("client", client); // Repopulate fields
            return "redirect:/Home/register?error=validation";
        }

        // 2. S'EXÉCUTE SEULEMENT SI LA VALIDATION EST PASSÉE
        try
        {
            // Photo Handling (works because 'photo' is disallowed by InitBinder)
            byte[] photoBytes = (photo != null && !photo.isEmpty()) ? photo.getBytes() : null;

            String role = (client.getUsername().equals("DrLove") || client.getUsername().equals("AhmedAdmin")) ? "admin" : "Client";

            // client.getPassword() raw pass
            clientCreationService.createClient(
                    client.getUsername(),
                    client.getEmail(),
                    client.getPassword(),
                    role,
                    client.getDateNaissance(),
                    photoBytes
            );

            // Success
            redirectAttributes.addFlashAttribute("successMessage", "Inscription réussie! Veuillez vous connecter.");
            return "redirect:/Home/login";

        }
        catch (Exception e)
        {
            // 1.  DataIntegrityViolationException (du service)
            String errorMessage = "Erreur lors de l'inscription: " + e.getMessage();

            // ATTRAPE L'EXEPTION LANCÉE DANS LE CREATIONSERVICE
            if (e instanceof DataIntegrityViolationException && e.getMessage().contains("nom d'utilisateur")) {
                errorMessage = "Le nom d'utilisateur est déjà pris. Veuillez en choisir un autre.";
            }
            // Pour le email idem
            else if (e instanceof DataIntegrityViolationException && e.getMessage().contains("adresse email")) {
                 errorMessage = "Cette adresse email est déjà utilisée.";
            }


            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            // Doit avoir le client comme attribut lors de la redirection
            redirectAttributes.addFlashAttribute("client", client);
            return "redirect:/Home/register?error=true";
        }
    }

    // Page about us
    @GetMapping("/about-us")
    public ModelAndView showAboutUs()
    {
        return new ModelAndView("about-us");
    }
    // Tous les produits
    @GetMapping("/all-produits")
    public ModelAndView allProduits()
    {
        ModelAndView mv = new ModelAndView("all-produits");
        List<Produit> produitsOG = appContext.selectAllProduits();

        List<ProduitDTO> produitDTO = produitsOG.stream()
                .map(p -> modelMapper.map(p, ProduitDTO.class))
                .toList();

        mv.addObject("produits", produitDTO);
        return mv;
    }

    // ===================================================================
    // GESTION DU PROFIL PERSO
    // ===================================================================
    // Page profil de l'utilisateur
    @GetMapping("/profil")
    public ModelAndView profil(Authentication auth)
    {
        Client client = (Client) auth.getPrincipal();
        String username = client.getUsername();

        // 1. Récupérer les factures pour ce client
        List<Facture> factures = factureRepository.findAllByUsernameClientOrderByDateAchatDesc(username);

        // 2. Créer le ModelAndView initial
        ModelAndView mv = new ModelAndView("profil", "client", client);

        // 3. Ajouter les factures au modèle
        mv.addObject("factures", factures);

        // TRansfert en base64 pour affichage
        if (client.getPhoto() != null && client.getPhoto().length > 0) {
            String base64Image = Base64.getEncoder().encodeToString(client.getPhoto());


            mv.addObject("imageBase64", base64Image); // Avec l'image encodée
            return mv;
        }

        // Retour facture + image
        return mv;
    }

    // Page de l'utilisateur perso, protégé dans le chain
    @GetMapping("/profil/modifier")
    public String showModifierProfilForm(Authentication auth, Model model)
    {
        String currentUsername = auth.getName();

        // Vérifier si il s'agit d'un nouveau formulaire ou bien erreur de validation
        if (!model.containsAttribute("clientUpdate"))
        {
            // Trouve le client
            Client clientToUpdate = appContext.findClientByUsername(currentUsername);

            if (clientToUpdate == null)
            {
                // Voudrait dire qu'une erreur c'est produite
                return "redirect:/Home/accueil";
            }

            // Pour le binding
            model.addAttribute("clientUpdate", clientToUpdate);
        }

        // Peut-être utile dans le contexte
        model.addAttribute("client", auth.getPrincipal());

        return "modifier-profil";
    }


    @PostMapping("/profil/delete")
    public String deleteProfil(Authentication auth, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if (auth == null) {
            return "redirect:/Home/login";
        }

        // auth.getName() retourne le username
        String usernameToDelete = auth.getName();

        try {
            appContext.deleteClientByUsername(usernameToDelete);

            // Invalider la session pour ne pas avoir de problème. fucki shit fucksddfjskf
            SecurityContextHolder.clearContext();
            if (request.getSession(false) != null) {
                request.getSession(false).invalidate();
            }

            redirectAttributes.addFlashAttribute("successMessage", "Votre compte a été supprimé avec succès. Au revoir!");
            return "redirect:/Home/accueil";

        } catch (Exception e) {
            // Log the error
            System.err.println("Error deleting client profile: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression du compte.");
            return "redirect:/Home/profil";
        }
    }

    // POST mapping for modifications
    @PostMapping("/profil/modifier")
    public String processModifierProfil(@Valid @ModelAttribute("clientUpdate") Client clientUpdate,
                                        BindingResult bindingResult,
                                        @RequestParam(value = "password", required = false) String newPassword,
                                        @RequestParam(value = "newPhoto", required = false) MultipartFile newPhoto,
                                        Authentication auth,
                                        RedirectAttributes redirectAttributes)
    {
        // 1. Vérifier les erreurs

        // Problème avec password si laissé vide
        if (newPassword != null && !newPassword.trim().isEmpty() && newPassword.trim().length() < 4)
        {
            // Ajoute l'erreur car n'est plus sur le min-lenght de la propriété!
            bindingResult.rejectValue("password", "Size.client.password", "Le mot de passe doit avoir au moins 4 caractères.");
        }

        if (bindingResult.hasErrors())
        {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.clientUpdate", bindingResult);
            redirectAttributes.addFlashAttribute("clientUpdate", clientUpdate);
            redirectAttributes.addFlashAttribute("errorMessage", "Validation error. Please check required fields.");
            return "redirect:/Home/profil/modifier";
        }

        try {

            // Pour le update ou non de la photo
            byte[] photoBytes = null;
            if (newPhoto != null && !newPhoto.isEmpty())
            {
                photoBytes = newPhoto.getBytes();
            }

            // 3. Update avec le service
            clientCreationService.updateClientDetails(
                    clientUpdate.getUsername(),
                    clientUpdate.getDateNaissance(),
                    newPassword,
                    photoBytes
            );

            redirectAttributes.addFlashAttribute("successMessage", "Profil modifié avec succès!");

        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Erreur lors de la modification: " + e.getMessage());

            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la modification: " + e.getMessage());
            redirectAttributes.addFlashAttribute("clientUpdate", clientUpdate);
            return "redirect:/Home/profil/modifier";
        }

        return "redirect:/Home/profil"; // Succès retourne à la vue profil
    }
}

    /*
    @GetMapping()
    public String afficherCatalogue(Model model)
    {
        // Récupère tous les produits
        List<Produit> produits = appContext.selectAllProduits();
        // Passe la liste au template
        model.addAttribute("produits", produits);
        // Renvoie à l'accueil avec un model produits
        return "accueil";
    }
    */
