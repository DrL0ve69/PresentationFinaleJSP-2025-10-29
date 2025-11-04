// Toasts
const toastEl = document.getElementById('panier-toast');
const toastBody = document.getElementById('panier-toast-body');
const panierToast = new bootstrap.Toast(toastEl, { delay: 1500 });

// Affiche le panier
// Affiche le panier
async function afficherPanier() {
    try {
        const res = await fetch('/ProjetPrimeJSP/panier/get');
        const panier = await res.json();

        // --- NEW VARIABLES TO REFERENCE THE CONTAINERS ---
        const emptyAlert = document.getElementById('panier-empty-alert');
        const fullContent = document.getElementById('panier-full-content');

        // --- LOGIC TO CHECK IF PANIER IS EMPTY ---
        if (panier.length === 0) {
            // Panier is EMPTY: Show the alert, hide the table.
            if (emptyAlert) emptyAlert.style.display = 'block';
            if (fullContent) fullContent.style.display = 'none';
        } else {
            // Panier is NOT EMPTY: Hide the alert, show the table.
            if (emptyAlert) emptyAlert.style.display = 'none';
            if (fullContent) fullContent.style.display = 'block';

            // --- EXISTING TABLE UPDATE LOGIC (only runs if not empty) ---
            const tbody = document.querySelector('#panier-table tbody');
            if (tbody) {
                tbody.innerHTML = "";

                let totalPanier = 0;
                let qteItemsTotalPanier = 0;

                panier.forEach(item => {
                    const totalItem = item.produit.prixUnitaire * item.quantite;
                    qteItemsTotalPanier += item.quantite;
                    totalPanier += totalItem;

                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                    <td>${item.produit.nom}</td>
                    <td>${item.produit.prixUnitaire.toFixed(2)} $</td>
                    <td>${item.quantite}</td>
                    <td>${totalItem.toFixed(2)} $</td>
                    <td>
                        <button class="btn btn-sm btn-success" onclick="ajouterAuPanier(this);" data-id="${item.produit.idProduit}">+</button>
                        <button class="btn btn-sm btn-danger" onclick="retirerDuPanier(this);" data-id="${item.produit.idProduit}">-</button>
                        <button class="btn btn-sm btn-secondary" onclick="supprimerProduit(this);" data-id="${item.produit.idProduit}">Supprimer</button>
                    </td>
                    `;
                    tbody.appendChild(tr);
                });

                document.getElementById('panier-total').textContent = totalPanier.toFixed(2) + "$";
                document.getElementById('panier-total-items').textContent = qteItemsTotalPanier + " item(s)";
                await updatePanierCount();
            }
        }

        await updatePanierCount(); // update peu importe

    } catch (e) {
        console.error(e);
    }
}


// Actions
async function ajouterAuPanier(button) { await modifierPanier(button.getAttribute('data-id'), '/ProjetPrimeJSP/panier/ajouter/'); }
async function retirerDuPanier(button) { await modifierPanier(button.getAttribute('data-id'), '/ProjetPrimeJSP/panier/retirer/'); }
async function supprimerProduit(button) { await modifierPanier(button.getAttribute('data-id'), '/ProjetPrimeJSP/panier/supprimer/'); }
async function viderPanier() { await modifierPanier('', '/ProjetPrimeJSP/panier/vider'); }

// Fonction générique
async function modifierPanier(id, url) {
    try {
        const res = await fetch(url + id, { method: 'POST' }); // Ou POST?
        toastBody.textContent = res.ok ? "Action réussie !" : await res.text();
        toastEl.className = res.ok ? "toast align-items-center text-white bg-success border-0"
            : "toast align-items-center text-white bg-danger border-0";
        panierToast.show();
        await updatePanierCount()
        await afficherPanier();
    } catch (e) {
        console.error(e);
    }
}

// Badge
async function updatePanierCount() {
    try {
        const response = await fetch('/ProjetPrimeJSP/panier/count');
        if(!response.ok) throw new Error('Erreur récupération count');
        const count = await response.json();
        console.log(count)

        const badge = document.getElementById('panier-count');
        const badgeFloating = document.getElementById('panier-count-floating');
        // Ajouter les éléments du panier aussi
        if(badge) badge.textContent = count;
        if(badgeFloating) badgeFloating.textContent = count;

    } catch (error) {
        console.error('Erreur updatePanierCount:', error);
    }
}

// PASSER UNE COMMANDE
// ... existing modifierPanier and other functions ...

async function passerCommande()
{
    try
    {

        const res = await fetch('/ProjetPrimeJSP/panier/passerCommande', { method: 'POST' });

        if (res.ok)
        {
            toastBody.textContent = await res.text(); // Message de succès dans le contrôlleur
            toastEl.className = "toast align-items-center text-white bg-success border-0";
            // Apres succes mettre à jour le compte & l'affichage
            await updatePanierCount();
            // Pour le panier vide
            await afficherPanier();
        }
        else
        {
            // ERREUR RES.NOTOK
            const errorText = await res.text();
            toastBody.textContent = errorText;
            toastEl.className = "toast align-items-center text-white bg-danger border-0";
        }

        panierToast.show();
    } catch (e) {
        console.error("Erreur lors de la soumission de la commande:", e);
        toastBody.textContent = "Erreur réseau lors de la commande.";
        toastEl.className = "toast align-items-center text-white bg-danger border-0";
        panierToast.show();
    }
}


// Initialisation après chargement du DOM
document.addEventListener('DOMContentLoaded', () =>
{
    // Ne lance afficherPanier que si tableau panier présent

    if(document.querySelector('#panier-table tbody')) {
        afficherPanier();
    }
    updatePanierCount(); // Toujours mettre à jour le badge si présent

    // L'ajout sur les ajouteraupanier pour ne pas scroll en haut à chaque fois
    const addToCartButtons = document.querySelectorAll('.js-add-to-cart');

    addToCartButtons.forEach(button =>
    {
        button.addEventListener('click', (event) =>
        {
            // 1. Empêche le scrollUp sur le click
            event.preventDefault();

            // 2. La fonction doit avoir son propre btn de défini & le update
            ajouterAuPanier(button);
            updatePanierCount();

        });
    });

});


/*
CODE POUR ASYNC À TESTER
document.addEventListener('DOMContentLoaded', async () => {
    try {
        const tbodyExists = document.querySelector('#panier-table tbody');

        if (tbodyExists) {
            await afficherPanier();
        }

        await updatePanierCount();

    } catch (error) {
        console.error('Erreur dans le chargement du panier:', error);
        // Redirect to Spring Boot error page
        window.location.href = '/error'; // Spring Boot will render your errorpage.html
    }
});

// Context path
const contextPath = '/EcommerceJSP';

// Toasts
const toastEl = document.getElementById('panier-toast');
const toastBody = document.getElementById('panier-toast-body');
const panierToast = new bootstrap.Toast(toastEl, { delay: 1500 });

// Affiche le panier
async function afficherPanier() {
	try {
		const res = await fetch(`${contextPath}/panier/get`);
		const panier = await res.json();

		console.log(panier);

		const tbody = document.querySelector('#panier-table tbody');
		tbody.innerHTML = "";

		let totalPanier = 0;

		panier.forEach(item => {
			const totalItem = item.produit.prixUnitaire * item.quantite;
			totalPanier += totalItem;

			const tr = document.createElement('tr');
			tr.innerHTML = `
                <td>${item.produit.nom}</td>
                <td>${item.produit.prixUnitaire.toFixed(2)} $</td>
                <td>${item.quantite}</td>
                <td>${totalItem.toFixed(2)} $</td>
                <td>
                    <button class="btn btn-sm btn-success" onclick="ajouterAuPanier(this)" data-id="${item.produit.idProduit}">+</button>
                    <button class="btn btn-sm btn-danger" onclick="retirerDuPanier(this)" data-id="${item.produit.idProduit}">-</button>
                    <button class="btn btn-sm btn-secondary" onclick="supprimerProduit(this)" data-id="${item.produit.idProduit}">Supprimer</button>
                </td>
            `;
			tbody.appendChild(tr);
		});

		document.getElementById('panier-total').textContent = totalPanier.toFixed(2);
		await updatePanierCount();
	} catch (e) {
		console.error(e);
		// Redirect to global error page
		window.location.href = `${contextPath}/error`;
	}
}

// Actions
async function ajouterAuPanier(button) { await modifierPanier(button.getAttribute('data-id'), `${contextPath}/panier/ajouter/`); }
async function retirerDuPanier(button) { await modifierPanier(button.getAttribute('data-id'), `${contextPath}/panier/retirer/`); }
async function supprimerProduit(button) { await modifierPanier(button.getAttribute('data-id'), `${contextPath}/panier/supprimer/`); }
async function viderPanier() { await modifierPanier('', `${contextPath}/panier/vider`); }

// Fonction générique
async function modifierPanier(id, url)
{
	try
	{
		// Peut-être ajouter le contexPath ici aussi!
		const res = await fetch(url + id, { method: 'POST' });
		console.log(res);

		toastBody.textContent = res.ok ? "Action réussie !" : await res.text();
		toastEl.className = res.ok
			? "toast align-items-center text-white bg-success border-0"
			: "toast align-items-center text-white bg-danger border-0";
		panierToast.show();
		await afficherPanier();
	} catch (e) {
		console.error(e);
		window.location.href = `${contextPath}/error`;
	}
}

// Badge
async function updatePanierCount() {
	try {
		const response = await fetch(`${contextPath}/panier/count`);
		console.log(response);

		if (!response.ok) throw new Error('Erreur récupération count');
		const count = await response.json();
		console.log(count);

		const badge = document.getElementById('panier-count');
		const badgeFloating = document.getElementById('panier-count-floating');
		if (badge) badge.textContent = count;
		if (badgeFloating) badgeFloating.textContent = count;
	} catch (error) {
		console.error('Erreur updatePanierCount:', error);
		window.location.href = `${contextPath}/error`;
	}
}

// Initialisation après chargement du DOM
document.addEventListener('DOMContentLoaded', () => {
	if (document.querySelector('#panier-table tbody')) {
		afficherPanier();
	}
	updatePanierCount(); // Toujours mettre à jour le badge si présent
});
 */