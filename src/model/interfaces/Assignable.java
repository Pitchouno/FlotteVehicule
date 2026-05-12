package model.interfaces;

/**
 * Contrat pour toute entité pouvant être assignée à une mission.
 * Implémentée par Vehicule et Chauffeur.
 */
public interface Assignable {

    /** Retourne true si l'entité est libre (pas en mission). */
    boolean estDisponible();

    /** Assigne l'entité à la mission identifiée par missionId. */
    void assigner(String missionId);

    /** Libère l'entité (fin ou annulation de mission). */
    void liberer();
}
