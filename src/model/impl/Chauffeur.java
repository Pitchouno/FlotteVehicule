package model.impl;

import model.exceptions.ChauffeurIndisponibleException;
import model.interfaces.Assignable;
import util.Entite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Représente un chauffeur de la flotte. Implémente Assignable. */
public class Chauffeur extends Entite implements Assignable {

    private static final long serialVersionUID = 1L;

    private String nom;
    private String prenom;
    private String numeroPermis;
    private String categoriePermis; // ex : "B", "C", "CE"
    private boolean disponible;
    private String  missionEnCoursId;
    private final List<String> historiqueMissions;

    public Chauffeur(String id, String nom, String prenom,
                     String numeroPermis, String categoriePermis) {
        super(id);
        this.nom              = nom;
        this.prenom           = prenom;
        this.numeroPermis     = numeroPermis;
        this.categoriePermis  = categoriePermis;
        this.disponible       = true;
        this.historiqueMissions = new ArrayList<>();
    }

    // ── Implémentation de Assignable ──────────────────────────────────────

    @Override
    public boolean estDisponible() { return disponible; }

    @Override
    public void assigner(String missionId) {
        if (!disponible) throw new ChauffeurIndisponibleException(getId());
        this.missionEnCoursId = missionId;
        this.disponible = false;
        this.historiqueMissions.add(missionId);
    }

    @Override
    public void liberer() {
        this.missionEnCoursId = null;
        this.disponible = true;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────

    public String getNom()              { return nom; }
    public String getPrenom()           { return prenom; }
    public String getNomComplet()       { return prenom + " " + nom; }
    public String getNumeroPermis()     { return numeroPermis; }
    public String getCategoriePermis()  { return categoriePermis; }
    public String getMissionEnCoursId() { return missionEnCoursId; }
    public int    getNombreMissions()   { return historiqueMissions.size(); }

    public List<String> getHistoriqueMissions() {
        return Collections.unmodifiableList(historiqueMissions);
    }

    public void setNom(String nom)                      { this.nom = nom; }
    public void setPrenom(String prenom)                { this.prenom = prenom; }
    public void setNumeroPermis(String n)               { this.numeroPermis = n; }
    public void setCategoriePermis(String c)            { this.categoriePermis = c; }

    @Override
    public String toString() {
        return getNomComplet() + " [Permis " + categoriePermis + "]"
                + " — " + (disponible ? "Disponible" : "En mission");
    }
}
