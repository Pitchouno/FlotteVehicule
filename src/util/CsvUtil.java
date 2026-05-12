package util;

import model.abstracts.Mission;
import model.abstracts.Vehicule;
import model.impl.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilitaire de persistance CSV.
 * Sauvegarde et charge les véhicules, chauffeurs et missions depuis/vers des fichiers .csv.
 */
public class CsvUtil {

    private CsvUtil() {} // classe utilitaire, pas d'instanciation

    // ════════════════════════════════════════
    // VÉHICULES
    // ════════════════════════════════════════

    public static void sauvegarderVehicules(List<Vehicule> vehicules, String chemin) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(chemin))) {
            w.write("immatriculation,marque,modele,annee,type,kilometrage,disponible,prochainEntretien");
            w.newLine();
            for (Vehicule v : vehicules) {
                String entretien = v.getProchainEntretien() != null ? v.getProchainEntretien().toString() : "";
                w.write(String.join(",",
                        v.getImmatriculation(),
                        v.getMarque(),
                        v.getModele(),
                        String.valueOf(v.getAnnee()),
                        v.getTypeVehicule(),
                        String.valueOf(v.getKilometrage()),
                        String.valueOf(v.estDisponible()),
                        entretien
                ));
                w.newLine();
            }
        }
    }

    public static List<Vehicule> chargerVehicules(String chemin) throws IOException {
        List<Vehicule> liste = new ArrayList<>();
        File f = new File(chemin);
        if (!f.exists()) return liste;
        try (BufferedReader r = new BufferedReader(new FileReader(chemin))) {
            r.readLine(); // ignorer l'en-tête
            String ligne;
            while ((ligne = r.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] c = ligne.split(",", -1);
                if (c.length < 6) continue;
                Vehicule v = switch (c[4]) {
                    case "Leger"   -> new VehiculeLeger(c[0], c[1], c[2], Integer.parseInt(c[3]), 5);
                    case "Lourd"   -> new VehiculeLourd(c[0], c[1], c[2], Integer.parseInt(c[3]), 20.0);
                    case "Special" -> new VehiculeSpecial(c[0], c[1], c[2], Integer.parseInt(c[3]), "Spécial");
                    default        -> null;
                };
                if (v != null) {
                    // CORRECTIF : chargerKilometrage() contourne la validation
                    // (setKilometrage lèverait KilometrageInvalideException car on part de 0)
                    try { v.chargerKilometrage(Double.parseDouble(c[5])); } catch (Exception ignored) {}
                    if (c.length > 7 && !c[7].isEmpty()) {
                        try { v.setProchainEntretien(LocalDate.parse(c[7])); } catch (Exception ignored) {}
                    }
                    liste.add(v);
                }
            }
        }
        return liste;
    }

    // ════════════════════════════════════════
    // CHAUFFEURS
    // ════════════════════════════════════════

    public static void sauvegarderChauffeurs(List<Chauffeur> chauffeurs, String chemin) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(chemin))) {
            w.write("id,nom,prenom,numeroPermis,categoriePermis,disponible");
            w.newLine();
            for (Chauffeur c : chauffeurs) {
                w.write(String.join(",",
                        c.getId(), c.getNom(), c.getPrenom(),
                        c.getNumeroPermis(), c.getCategoriePermis(),
                        String.valueOf(c.estDisponible())
                ));
                w.newLine();
            }
        }
    }

    public static List<Chauffeur> chargerChauffeurs(String chemin) throws IOException {
        List<Chauffeur> liste = new ArrayList<>();
        File f = new File(chemin);
        if (!f.exists()) return liste;
        try (BufferedReader r = new BufferedReader(new FileReader(chemin))) {
            r.readLine();
            String ligne;
            while ((ligne = r.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] c = ligne.split(",", -1);
                if (c.length < 5) continue;
                liste.add(new Chauffeur(c[0], c[1], c[2], c[3], c[4]));
            }
        }
        return liste;
    }

    // ════════════════════════════════════════
    // MISSIONS
    // ════════════════════════════════════════

    public static void sauvegarderMissions(List<Mission> missions, String chemin) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(chemin))) {
            w.write("id,depart,destination,dateDepart,distanceKm,type,statut,vehiculeId,chauffeurId,coutPeage");
            w.newLine();
            for (Mission m : missions) {
                String type  = (m instanceof MissionLongue) ? "Longue" : "Courte";
                String peage = (m instanceof MissionLongue ml) ? String.valueOf(ml.getCoutPeage()) : "0";
                String vid   = m.getVehiculeAssigneId()  != null ? m.getVehiculeAssigneId()  : "";
                String cid   = m.getChauffeurAssigneId() != null ? m.getChauffeurAssigneId() : "";
                w.write(String.join(",",
                        m.getId(), m.getDepart(), m.getDestination(),
                        m.getDateDepart().toString(),
                        String.valueOf(m.getDistanceKm()),
                        type, m.getStatut().name(), vid, cid, peage
                ));
                w.newLine();
            }
        }
    }

    public static List<Mission> chargerMissions(String chemin) throws IOException {
        List<Mission> liste = new ArrayList<>();
        File f = new File(chemin);
        if (!f.exists()) return liste;
        try (BufferedReader r = new BufferedReader(new FileReader(chemin))) {
            r.readLine();
            String ligne;
            while ((ligne = r.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] c = ligne.split(",", -1);
                if (c.length < 6) continue;
                LocalDateTime date;
                try { date = LocalDateTime.parse(c[3]); } catch (Exception e) { date = LocalDateTime.now(); }
                double dist  = Double.parseDouble(c[4]);
                double peage = c.length > 9 ? Double.parseDouble(c[9]) : 0;
                Mission m = "Longue".equals(c[5])
                        ? new MissionLongue(c[0], c[1], c[2], date, dist, peage)
                        : new MissionCourte(c[0], c[1], c[2], date, dist);
                if (c.length > 6 && !c[6].isEmpty()) {
                    try { m.setStatut(Mission.Statut.valueOf(c[6])); } catch (Exception ignored) {}
                }
                if (c.length > 7 && !c[7].isEmpty()) m.setVehiculeAssigneId(c[7]);
                if (c.length > 8 && !c[8].isEmpty()) m.setChauffeurAssigneId(c[8]);
                liste.add(m);
            }
        }
        return liste;
    }
}
