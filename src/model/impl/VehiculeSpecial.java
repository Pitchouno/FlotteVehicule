package model.impl;

import model.abstracts.Vehicule;
import model.interfaces.Trackable;

/** Véhicule spécial (ambulance, benne, grue...) avec suivi GPS. */
public class VehiculeSpecial extends Vehicule implements Trackable {

    private static final long serialVersionUID = 1L;

    private String specialite; // ex : "Ambulance", "Benne", "Grue"
    private double latitude;
    private double longitude;
    private double distanceParcourue;

    public VehiculeSpecial(String immatriculation, String marque,
                            String modele, int annee, String specialite) {
        super(immatriculation, marque, modele, annee);
        this.specialite = specialite;
    }

    @Override
    public String getTypeVehicule() { return "Special"; }

    // ── Implémentation de Trackable ───────────────────────────────────────

    @Override
    public void mettreAJourPosition(double latitude, double longitude) {
        this.latitude  = latitude;
        this.longitude = longitude;
    }

    @Override
    public String getPositionActuelle() {
        return String.format("%.5f, %.5f", latitude, longitude);
    }

    @Override
    public double getDistanceParcourue() { return distanceParcourue; }

    // ── Getters / Setters ─────────────────────────────────────────────────

    public String getSpecialite()              { return specialite; }
    public void   setSpecialite(String s)      { this.specialite = s; }
    public void   setDistanceParcourue(double d){ this.distanceParcourue = d; }
}
