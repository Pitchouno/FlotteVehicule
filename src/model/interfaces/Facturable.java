package model.interfaces;

/**
 * Contrat pour toute entité dont on peut calculer un coût.
 * Implémentée par Mission (et ses sous-classes).
 */
public interface Facturable {

    /** Calcule et retourne le coût total en euros. */
    double calculerCout();

    /** Retourne un résumé lisible de la facturation. */
    String getResumeFacturation();
}
