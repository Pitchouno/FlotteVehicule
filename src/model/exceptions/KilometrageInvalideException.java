package model.exceptions;

/**
 * Lancée quand on tente de réduire le kilométrage d'un véhicule.
 * Utilisée dans Vehicule.setKilometrage().
 */
public class KilometrageInvalideException extends RuntimeException {

    private final double ancien;
    private final double nouveau;

    public KilometrageInvalideException(double ancien, double nouveau) {
        super(String.format(
            "Kilométrage invalide : la valeur %.0f est inférieure au kilométrage actuel %.0f.",
            nouveau, ancien));
        this.ancien  = ancien;
        this.nouveau = nouveau;
    }

    public double getAncien()  { return ancien; }
    public double getNouveau() { return nouveau; }
}
