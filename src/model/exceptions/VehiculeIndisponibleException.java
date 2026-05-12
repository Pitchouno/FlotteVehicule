package model.exceptions;

/** Lancée quand on tente d'assigner un véhicule déjà en mission. */
public class VehiculeIndisponibleException extends RuntimeException {
    private final String immatriculation;
    public VehiculeIndisponibleException(String immatriculation) {
        super("Le véhicule " + immatriculation + " n'est pas disponible.");
        this.immatriculation = immatriculation;
    }
    public String getImmatriculation() { return immatriculation; }
}
