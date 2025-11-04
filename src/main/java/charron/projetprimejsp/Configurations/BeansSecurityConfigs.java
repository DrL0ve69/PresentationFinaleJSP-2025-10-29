package charron.projetprimejsp.Configurations;

import charron.projetprimejsp.Services.ClientDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Pour le tag preAuthorized
public class BeansSecurityConfigs
{
    private final ClientDetailsService clientDetailsService;

    public BeansSecurityConfigs(ClientDetailsService clientDetailsService)
    {
        this.clientDetailsService = clientDetailsService;
    }

    // PassEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}

    // DAO
    // Authentication provider basé sur UserDetailsService
    @Bean
    public DaoAuthenticationProvider authProvider()
    {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(clientDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception
    {
        return config.getAuthenticationManager();
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults()
    {
        return new GrantedAuthorityDefaults(""); // supprime le préfixe "ROLE_"
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.authorizeHttpRequests(auth -> auth
                // Autoriser toutes les  ressources statiques
                .requestMatchers(
                        "/css/**",
                        "/webjars/**",
                        "/favicon.ico",
                        "/js/**",
                        "/images/**", "/fragments/**").permitAll()

                // Pages réservées aux admins
                /*
                .requestMatchers("/Produits/delete/**",
                        "/Produits/add",
                        "/Produits/update/**").hasRole("Admin")
                */
                // Tout le reste nécessite authentification
                //.anyRequest().authenticated()

                // 2. PUBLIC PAGES: Accessible à tous même sans compte
                .requestMatchers("/Home/login",
                        "/Home/register", "/Home/register**",
                        "/Home/accueil**", "/Home/about-us","/Home/**",
                        "/panier/**").permitAll()

                // 3. ADMIN PAGES: Seul les comptes ayant le role qui contient roleName = admin
                .requestMatchers("/admin/**").hasAuthority("admin")

                // 4. AUTHENTICATED PAGES: nécessite l'authentification (loggedIn)
                .requestMatchers("/profil", "/panier/passerCommande").authenticated()

                // 5. Tout le mode peut performer le log in
                .requestMatchers("/Home/perform_login").permitAll()

                // 6. CATCH-ALL: En cas d'oublie
                .anyRequest().authenticated()
        );

        // Login personnalisé .loginProcessingUrl("/Home/perform_login")  ???
        http.formLogin(form -> form
                .loginPage("/Home/login")                   // mapping vers login.html
                .loginProcessingUrl("/Home/perform_login")
                .defaultSuccessUrl("/Home/login-success", true)
                .failureUrl("/Home/login?error=true")
                .permitAll()
        );

        // Logout
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/Home/accueil?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
        );


        http.exceptionHandling(ex -> ex
                .accessDeniedPage("/accessdenied") // Page d'accès refusé Unauthorized access (403) au cas
        );

        //  CSRF temporairement pour tester les POST de register
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}
