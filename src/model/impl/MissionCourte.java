package model.impl;

import model.abstracts.Mission;

import java.time.LocalDateTime;

/** Mission courte (< 200 km). Coût = carburant uniquement. */
public class MissionCourte extends Mission {

    private static final long serialVersionUID = 1L;
    private static final double PRIX_CARBURANT_L = 1.85;
    private static final double CONSO_L100       = 8.0;

    public MissionCourte(String id, String depart, String destination,
                          LocalDateTime dateDepart, double distanceKm) {
        super(id, depart, destination, dateDepart, distanceKm);
    }

    @Override
    public double calculerCoutCarburant() {
        return (getDistanceKm() / 100.0) * CONSO_L100 * PRIX_CARBURANT_L;
    }

    @Override
    public String getResumeFacturation() {
        return String.format("Mission courte %s (%s → %s) : %.2f €",
                getId(), getDepart(), getDestination(), calculerCout());
    }
}
