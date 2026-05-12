package model.abstracts;

import model.interfaces.Facturable;
import util.Entite;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe abstraite représentant une mission de transport.
 * Regroupe les attributs communs à MissionCourte et MissionLongue.
 * Implémente Facturable — calculerCout() délègue à calculerCoutCarburant().
 */
public abstract class Mission extends Entite implements Facturable {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Cycle de vie d'une mission. */
    public enum Statut { EN_ATTENTE, EN_COURS, TERMINEE, ANNULEE }

    // ── Attributs communs ─────────────────────────────────────────────────
    private String depart;
    private String destination;
    private LocalDateTime dateDepart;
    private double distanceKm;
    private Statut statut;
    private String vehiculeAssigneId;
    private String chauffeurAssigneId;
    private String rapport; // rapport de fin de mission

    // ── Constructeur ──────────────────────────────────────────────────────

    public Mission(String id, String depart, String destination,
                   LocalDateTime dateDepart, double distanceKm) {
        super(id);
        this.depart = depart;
        this.destination = destination;
        this.dateDepart = dateDepart;
        this.distanceKm = distanceKm;
        this.statut = Statut.EN_ATTENTE;
    }

    // ── Méthode abstraite ─────────────────────────────────────────────────

    /**
     * Calcule le coût carburant spécifique à chaque type de mission.
     * MissionCourte et MissionLongue ont des calculs différents.
     */
    public abstract double calculerCoutCarburant();

    // ── Implémentation de Facturable ──────────────────────────────────────

    @Override
    public double calculerCout() {
        return calculerCoutCarburant();
    }

    // ── Getters / Setters ─────────────────────────────────────────────────

    public String       getDepart()             { return depart; }
    public String       getDestination()         { return destination; }
    public LocalDateTime getDateDepart()          { return dateDepart; }
    public double       getDistanceKm()          { return distanceKm; }
    public Statut       getStatut()              { return statut; }
    public String       getVehiculeAssigneId()   { return vehiculeAssigneId; }
    public String       getChauffeurAssigneId()  { return chauffeurAssigneId; }
    public String       getRapport()             { return rapport; }

    public void setStatut(Statut statut)                      { this.statut = statut; }
    public void setVehiculeAssigneId(String id)               { this.vehiculeAssigneId = id; }
    public void setChauffeurAssigneId(String id)              { this.chauffeurAssigneId = id; }
    public void setRapport(String rapport)                    { this.rapport = rapport; }
    public void setDepart(String depart)                      { this.depart = depart; }
    public void setDestination(String destination)            { this.destination = destination; }
    public void setDistanceKm(double distanceKm)              { this.distanceKm = distanceKm; }
    public void setDateDepart(LocalDateTime dateDepart)       { this.dateDepart = dateDepart; }

    @Override
    public String toString() {
        return getId() + " : " + depart + " → " + destination
                + " (" + String.format("%.0f", distanceKm) + " km)"
                + " [" + statut + "]";
    }
}
