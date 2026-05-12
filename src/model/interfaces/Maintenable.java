package model.interfaces;

import java.time.LocalDate;

/**
 * Contrat pour toute entité soumise à des entretiens périodiques.
 * Implémentée par Vehicule (et ses sous-classes).
 */
public interface Maintenable {

    /** Retourne la date du prochain entretien planifié. */
    LocalDate getProchainEntretien();

    /**
     * Enregistre un entretien effectué.
     * Le prochain entretien est automatiquement planifié 6 mois plus tard.
     */
    void enregistrerEntretien(LocalDate date, double cout);

    /** Retourne true si la date du prochain entretien est dépassée. */
    boolean necessiteMaintenanceUrgente();
}
