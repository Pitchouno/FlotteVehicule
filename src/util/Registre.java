package util;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Conteneur générique borné : T doit être une Entite.
 * Centralise les opérations CRUD sur n'importe quelle collection métier.
 *
 * Exemples d'utilisation :
 *   Registre<Vehicule>  vehicules  = new Registre<>("Vehicules");
 *   Registre<Chauffeur> chauffeurs = new Registre<>("Chauffeurs");
 *   Registre<Mission>   missions   = new Registre<>("Missions");
 */
public class Registre<T extends Entite> {

    private final Map<String, T> stockage;
    private final String nom;

    public Registre(String nom) {
        this.nom = nom;
        this.stockage = new LinkedHashMap<>(); // ordre d'insertion préservé
    }

    // ── Opérations CRUD ──────────────────────────────────────────────────

    /**
     * Ajoute une entité. Lance une exception si l'ID existe déjà.
     */
    public void ajouter(T entite) {
        if (entite == null) throw new IllegalArgumentException("L'entité ne peut pas être null.");
        if (stockage.containsKey(entite.getId())) {
            throw new IllegalArgumentException("ID déjà existant : " + entite.getId());
        }
        stockage.put(entite.getId(), entite);
    }

    /**
     * Remplace une entité existante (mise à jour).
     * Lance une exception si l'ID n'existe pas.
     */
    public void modifier(T entite) {
        if (!stockage.containsKey(entite.getId())) {
            throw new IllegalArgumentException("ID introuvable : " + entite.getId());
        }
        stockage.put(entite.getId(), entite);
    }

    /**
     * Supprime l'entité avec l'ID donné.
     * Retourne true si trouvée et supprimée, false sinon.
     */
    public boolean supprimer(String id) {
        return stockage.remove(id) != null;
    }

    /**
     * Recherche par ID. Retourne un Optional pour forcer la gestion du cas absent.
     */
    public Optional<T> trouverParId(String id) {
        return Optional.ofNullable(stockage.get(id));
    }

    // ── Requêtes ─────────────────────────────────────────────────────────

    /**
     * Filtre les entités selon un critère (lambda ou méthode référence).
     * Exemple : vehicules.filtrer(v -> v.estDisponible())
     */
    public List<T> filtrer(Predicate<T> critere) {
        return stockage.values().stream()
                .filter(critere)
                .collect(Collectors.toList());
    }

    /**
     * Retourne toutes les entités sous forme de liste non modifiable.
     */
    public List<T> tousLesElements() {
        return Collections.unmodifiableList(new ArrayList<>(stockage.values()));
    }

    /** Retourne true si le registre contient l'ID donné. */
    public boolean contient(String id) {
        return stockage.containsKey(id);
    }

    public int taille() {
        return stockage.size();
    }

    public boolean estVide() {
        return stockage.isEmpty();
    }

    public String getNom() {
        return nom;
    }

    @Override
    public String toString() {
        return "Registre[" + nom + "] : " + stockage.size() + " élément(s)";
    }
}
