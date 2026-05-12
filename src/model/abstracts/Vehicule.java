package model.abstracts;

import model.exceptions.KilometrageInvalideException;
import model.exceptions.VehiculeIndisponibleException;
import model.interfaces.Assignable;
import model.interfaces.Maintenable;
import util.Entite;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe abstraite représentant un véhicule de la flotte.
 * Regroupe tous les attributs et comportements communs à VehiculeLeger,
 * VehiculeLourd et VehiculeSpecial.
 *
 * Implémente Assignable et Maintenable — le code est écrit une seule fois ici
 * et hérité automatiquement par toutes les sous-classes.
 *
 * Pourquoi abstraite ? Un véhicule "générique" n'a aucun sens dans notre domaine.
 * On crée toujours un léger, un lourd ou un spécial.
 */
public abstract class Vehicule extends Entite implements Assignable, Maintenable {

    private static final long serialVersionUID = 1L;

    // ── Attributs communs ─────────────────────────────────────────────
    private final String immatriculation;
    private String marque;
    private String modele;
    private int    annee;
    private double kilometrage;
    private boolean disponible;
    private String  missionEnCoursId;

    // Maintenance
    private LocalDate     prochainEntretien;
    private double        coutTotalMaintenance;
    private final List<String> historiqueEntretiens;

    // ── Constructeur ──────────────────────────────────────────────────
    public Vehicule(String immatriculation, String marque, String modele, int annee) {
        super(immatriculation);
        this.immatriculation      = immatriculation;
        this.marque               = marque;
        this.modele               = modele;
        this.annee                = annee;
        this.kilometrage          = 0;
        this.disponible           = true;
        this.missionEnCoursId     = null;
        this.coutTotalMaintenance = 0;
        this.historiqueEntretiens = new ArrayList<>();
        this.prochainEntretien    = LocalDate.now().plusMonths(6);
    }

    // ── Méthode abstraite ─────────────────────────────────────────────
    /** Retourne le type du véhicule : "Leger", "Lourd" ou "Special". */
    public abstract String getTypeVehicule();

    // ── Assignable ────────────────────────────────────────────────────
    @Override
    public boolean estDisponible() { return disponible; }

    @Override
    public void assigner(String missionId) {
        if (!disponible) throw new VehiculeIndisponibleException(immatriculation);
        this.missionEnCoursId = missionId;
        this.disponible       = false;
    }

    @Override
    public void liberer() {
        this.missionEnCoursId = null;
        this.disponible       = true;
    }

    // ── Maintenable ───────────────────────────────────────────────────
    @Override
    public LocalDate getProchainEntretien() { return prochainEntretien; }

    @Override
    public void enregistrerEntretien(LocalDate date, double cout) {
        if (cout < 0) throw new IllegalArgumentException("Le coût ne peut pas être négatif.");
        this.prochainEntretien     = date.plusMonths(6);
        this.coutTotalMaintenance += cout;
        this.historiqueEntretiens.add(date + " — " + String.format("%.2f", cout) + " €");
    }

    @Override
    public boolean necessiteMaintenanceUrgente() {
        return prochainEntretien != null && prochainEntretien.isBefore(LocalDate.now());
    }

    // ── Kilométrage ───────────────────────────────────────────────────

    /**
     * Remplace le kilométrage. Lance KilometrageInvalideException si la
     * nouvelle valeur est inférieure à l'ancienne (kilométrage ne peut que croître).
     * Utilisée lors des mises à jour en cours d'exploitation.
     */
    public void setKilometrage(double km) {
        if (km < this.kilometrage) {
            throw new KilometrageInvalideException(this.kilometrage, km);
        }
        this.kilometrage = km;
    }

    /**
     * Charge directement un kilométrage sans vérification (usage CSV uniquement).
     * Ne jamais appeler depuis la logique métier.
     */
    public void chargerKilometrage(double km) {
        this.kilometrage = km;
    }

    // ── Getters / Setters ─────────────────────────────────────────────
    public String getImmatriculation()          { return immatriculation; }
    public String getMarque()                   { return marque; }
    public String getModele()                   { return modele; }
    public int    getAnnee()                    { return annee; }
    public double getKilometrage()              { return kilometrage; }
    public String getMissionEnCoursId()         { return missionEnCoursId; }
    public double getCoutTotalMaintenance()     { return coutTotalMaintenance; }

    public List<String> getHistoriqueEntretiens() {
        return Collections.unmodifiableList(historiqueEntretiens);
    }

    public void setMarque(String marque) { this.marque = marque; }
    public void setModele(String modele) { this.modele = modele; }
    public void setAnnee(int annee)      { this.annee  = annee; }

    public void setProchainEntretien(LocalDate date) { this.prochainEntretien = date; }

    @Override
    public String toString() {
        return immatriculation + " — " + marque + " " + modele
                + " (" + annee + ") [" + getTypeVehicule() + "]"
                + " — " + (disponible ? "Disponible" : "En mission");
    }
}
