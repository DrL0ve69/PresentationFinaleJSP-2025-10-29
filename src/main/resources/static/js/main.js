/*
// GESTION DU PANIER avec AJAX
// Pour les bulles info. Source: BootStrap
// GESTION DU PANIER AVEC BULLE INFO
const toastEl = document.getElementById('panier-toast');
const toastBody = document.getElementById('panier-toast-body');
const panierToast = new bootstrap.Toast(toastEl, { delay: 1500 }); // disparaît après 1.5s
// Met à jour le badge du panier
async function updatePanierCount()
{
    try
    {
        // Faire l'appel et attendre la promesse et réponse
        const response = await fetch('/ProjetFinalJSP/panier/count');
        if (!response.ok) {
            console.warn("Erreur réseau:", response.status);
            return;
        }


        const text = await response.json();
        let count = 0;
        try {
            count = JSON.parse(text);
        } catch (e) {
            console.warn("Réponse inattendue:", text);
        }
        document.getElementById('panier-count').textContent = count;
    } catch (error) {
        console.error('Erreur:', error);
    }
}

// Ajouter le produit au panier
async function ajouterAuPanier(button)
{
    const produitId = button.getAttribute('data-id');
    try {
        const response = await fetch(`/panier/ajouter/${produitId}`, { method: 'POST' });
        const data = await response.text();
        console.log(data);

        if (response.ok)
        {
            // Succès
            toastBody.textContent = "Produit ajouté !";
            toastEl.classList.remove('bg-danger');
            toastEl.classList.add('bg-success');
        }
        else
        {
            // Erreur serveur
            toastBody.textContent = "Non-Disponible";
            toastEl.classList.remove('bg-success');
            toastEl.classList.add('bg-danger');
        }

        panierToast.show();
        await updatePanierCount(); // Mettre à jour après l'ajout

    } catch (error) {
        console.error('Erreur:', error);
    }
}

// Vider le panier
async function viderPanier()
{
    try {
        const res = await fetch('/panier/vider', { method: 'POST' });
        const msg = await res.text();

        // Affiche toast selon succès
        toastBody.textContent = res.ok ? "Panier vidé !" : msg;
        toastEl.className = res.ok
            ? "toast align-items-center text-white bg-success border-0"
            : "toast align-items-center text-white bg-danger border-0";
        panierToast.show();

        // Mettre à jour le badge
        await updatePanierCount();
    } catch(e) {
        console.error(e);
        toastBody.textContent = "Erreur lors du vidage";
        toastEl.className = "toast align-items-center text-white bg-danger border-0";
        panierToast.show();
    }
}
 */

// CHARGEMENT DYNAMIQUE DES PAGES (login/register/panier)
// ==========================

async function loadContent(url) {
    const main = document.getElementById("main-content");
    console.log(main);

    try {


        const response = await fetch(url, {
            headers: { "X-Requested-With": "XMLHttpRequest" }
        });

        if (!response.ok) throw new Error("Erreur HTTP " + response.status);

        const html = await response.text();
        main.innerHTML = html;

        // Si le contenu chargé contient des formulaires ou boutons,
        // on peut re-lier des événements ici si besoin.

    } catch (err) {
        console.error("Erreur loadContent:", err);
        main.innerHTML = `
			<div class="alert alert-danger mt-4" role="alert">
				Erreur de chargement du contenu : ${err.message}
			</div>`;
    }
}

// Appeller la fonction lors du DomLoad pour afficher le count
//document.addEventListener('DOMContentLoaded', updatePanierCount);