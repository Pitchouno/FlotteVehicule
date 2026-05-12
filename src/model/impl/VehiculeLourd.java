package model.impl;

import model.abstracts.Vehicule;

/** Poids lourd, camion ou semi-remorque (> 3,5 t). */
public class VehiculeLourd extends Vehicule {

    private static final long serialVersionUID = 1L;

    private double chargeMaxTonnes;
    private int    nombreEssieux;
    private double consommationL100;

    public VehiculeLourd(String immatriculation, String marque,
                          String modele, int annee, double chargeMaxTonnes) {
        super(immatriculation, marque, modele, annee);
        this.chargeMaxTonnes = chargeMaxTonnes;
        this.nombreEssieux = 2;
        this.consommationL100 = 30.0;
    }

    @Override
    public String getTypeVehicule() { return "Lourd"; }

    public double getChargeMaxTonnes()           { return chargeMaxTonnes; }
    public int    getNombreEssieux()              { return nombreEssieux; }
    public double getConsommationL100()           { return consommationL100; }
    public void   setChargeMaxTonnes(double c)    { this.chargeMaxTonnes = c; }
    public void   setNombreEssieux(int n)         { this.nombreEssieux = n; }
    public void   setConsommationL100(double c)   { this.consommationL100 = c; }
}
