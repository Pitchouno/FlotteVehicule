package model.interfaces;

/**
 * Contrat pour toute entité dont on peut suivre la position GPS.
 * Implémentée par VehiculeSpecial et MissionLongue.
 */
public interface Trackable {

    /** Met à jour les coordonnées GPS de l'entité. */
    void mettreAJourPosition(double latitude, double longitude);

    /** Retourne la position actuelle sous forme "lat, lon". */
    String getPositionActuelle();

    /** Retourne la distance totale parcourue en kilomètres. */
    double getDistanceParcourue();
}
