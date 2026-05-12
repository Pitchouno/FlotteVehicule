package model.impl;

import model.abstracts.Vehicule;

/** Voiture de tourisme ou utilitaire léger (< 3,5 t). */
public class VehiculeLeger extends Vehicule {

    private static final long serialVersionUID = 1L;

    private int nombrePassagers;
    private double consommationL100;

    public VehiculeLeger(String immatriculation, String marque,
                          String modele, int annee, int nombrePassagers) {
        super(immatriculation, marque, modele, annee);
        this.nombrePassagers = nombrePassagers;
        this.consommationL100 = 7.0;
    }

    @Override
    public String getTypeVehicule() { return "Leger"; }

    public int    getNombrePassagers()           { return nombrePassagers; }
    public double getConsommationL100()          { return consommationL100; }
    public void   setNombrePassagers(int n)      { this.nombrePassagers = n; }
    public void   setConsommationL100(double c)  { this.consommationL100 = c; }
}
