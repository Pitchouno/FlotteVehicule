package model.impl;

import model.abstracts.Mission;
import model.interfaces.Trackable;

import java.time.LocalDateTime;

/** Mission longue (>= 200 km). Coût = carburant + péage. Suivi GPS activé. */
public class MissionLongue extends Mission implements Trackable {

    private static final long serialVersionUID = 1L;
    private static final double PRIX_CARBURANT_L = 1.85;
    private static final double CONSO_L100       = 30.0; // poids lourd

    private double coutPeage;
    private double latitude;
    private double longitude;
    private double distanceParcourue;

    public MissionLongue(String id, String depart, String destination,
                          LocalDateTime dateDepart, double distanceKm, double coutPeage) {
        super(id, depart, destination, dateDepart, distanceKm);
        this.coutPeage = coutPeage;
    }

    @Override
    public double calculerCoutCarburant() {
        return (getDistanceKm() / 100.0) * CONSO_L100 * PRIX_CARBURANT_L;
    }

    /** Surcharge : ajoute le péage au coût carburant. */
    @Override
    public double calculerCout() {
        return calculerCoutCarburant() + coutPeage;
    }

    @Override
    public String getResumeFacturation() {
        return String.format("Mission longue %s (%s → %s) : %.2f € (carburant %.2f € + péage %.2f €)",
                getId(), getDepart(), getDestination(),
                calculerCout(), calculerCoutCarburant(), coutPeage);
    }

    // ── Trackable ─────────────────────────────────────────────────────────

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

    public double getCoutPeage()              { return coutPeage; }
    public void   setCoutPeage(double c)      { this.coutPeage = c; }
    public void   setDistanceParcourue(double d){ this.distanceParcourue = d; }
}
