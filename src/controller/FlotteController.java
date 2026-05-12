package controller;

import model.abstracts.Mission;
import model.abstracts.Vehicule;
import model.exceptions.MissionDejaAffecteeException;
import model.impl.Chauffeur;
import model.impl.MissionCourte;
import model.impl.MissionLongue;
import model.impl.VehiculeLeger;
import model.impl.VehiculeLourd;
import model.impl.VehiculeSpecial;
import util.CsvUtil;
import util.Registre;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Contrôleur principal de l'application.
 * Contient TOUTE la logique métier — les vues Swing ne font qu'appeler ces méthodes.
 * Utilise les Streams Java 8+ pour tous les traitements sur les collections.
 */
public class FlotteController {

    // ── Registres (generics bornés) ───────────────────────────────────
    private final Registre<Vehicule>  vehicules  = new Registre<>("Vehicules");
    private final Registre<Chauffeur> chauffeurs = new Registre<>("Chauffeurs");
    private final Registre<Mission>   missions   = new Registre<>("Missions");

    private static final String CSV_VEHICULES  = "resources/vehicules.csv";
    private static final String CSV_CHAUFFEURS = "resources/chauffeurs.csv";
    private static final String CSV_MISSIONS   = "resources/missions.csv";

    /**
     * Compteur pour générer des IDs de mission uniques.
     * CORRECTIF : synchronisé après chargement CSV pour éviter les doublons.
     */
    private int compteurMission = 1;

    // ════════════════════════════════════════════════════════════════
    // VÉHICULES — CRUD
    // ════════════════════════════════════════════════════════════════

    public void ajouterVehicule(Vehicule v) {
        vehicules.ajouter(v);
    }

    public void modifierVehicule(Vehicule v) {
        vehicules.modifier(v);
    }

    public boolean supprimerVehicule(String immatriculation) {
        Optional<Vehicule> v = vehicules.trouverParId(immatriculation);
        if (v.isPresent() && !v.get().estDisponible()) {
            throw new IllegalStateException("Impossible de supprimer un véhicule en mission.");
        }
        return vehicules.supprimer(immatriculation);
    }

    public Optional<Vehicule> trouverVehicule(String immatriculation) {
        return vehicules.trouverParId(immatriculation);
    }

    public List<Vehicule> tousVehicules() {
        return vehicules.tousLesElements();
    }

    // ── Filtrage multicritères (Stream filter + lambdas) ──────────────

    public List<Vehicule> rechercherVehicules(String type, Boolean disponible, String marque) {
        return vehicules.tousLesElements().stream()
                .filter(v -> type       == null || type.isBlank()   || v.getTypeVehicule().equalsIgnoreCase(type))
                .filter(v -> disponible == null                      || v.estDisponible() == disponible)
                .filter(v -> marque     == null || marque.isBlank()  || v.getMarque().toLowerCase().contains(marque.toLowerCase()))
                .collect(Collectors.toList());
    }

    // ── Tri dynamique (Stream sorted + Comparator) ────────────────────

    public List<Vehicule> trierVehicules(String colonne, boolean croissant) {
        Comparator<Vehicule> comp = switch (colonne) {
            case "marque"      -> Comparator.comparing(Vehicule::getMarque);
            case "annee"       -> Comparator.comparingInt(Vehicule::getAnnee);
            case "kilometrage" -> Comparator.comparingDouble(Vehicule::getKilometrage);
            case "type"        -> Comparator.comparing(Vehicule::getTypeVehicule);
            default            -> Comparator.comparing(Vehicule::getImmatriculation);
        };
        if (!croissant) comp = comp.reversed();
        return vehicules.tousLesElements().stream()
                .sorted(comp)
                .collect(Collectors.toList());
    }

    public List<Vehicule> vehiculesMaintenanceUrgente() {
        return vehicules.tousLesElements().stream()
                .filter(Vehicule::necessiteMaintenanceUrgente)
                .collect(Collectors.toList());
    }

    // ════════════════════════════════════════════════════════════════
    // CHAUFFEURS — CRUD
    // ════════════════════════════════════════════════════════════════

    public void ajouterChauffeur(Chauffeur c) {
        chauffeurs.ajouter(c);
    }

    public void modifierChauffeur(Chauffeur c) {
        chauffeurs.modifier(c);
    }

    public boolean supprimerChauffeur(String id) {
        Optional<Chauffeur> c = chauffeurs.trouverParId(id);
        if (c.isPresent() && !c.get().estDisponible()) {
            throw new IllegalStateException("Impossible de supprimer un chauffeur en mission.");
        }
        return chauffeurs.supprimer(id);
    }

    public Optional<Chauffeur> trouverChauffeur(String id) {
        return chauffeurs.trouverParId(id);
    }

    public List<Chauffeur> tousChauffeurs() {
        return chauffeurs.tousLesElements();
    }

    public List<Chauffeur> rechercherChauffeurs(String nom, Boolean disponible, String permis) {
        return chauffeurs.tousLesElements().stream()
                .filter(c -> nom        == null || nom.isBlank()    || c.getNomComplet().toLowerCase().contains(nom.toLowerCase()))
                .filter(c -> disponible == null                      || c.estDisponible() == disponible)
                .filter(c -> permis     == null || permis.isBlank()  || c.getCategoriePermis().equalsIgnoreCase(permis))
                .collect(Collectors.toList());
    }

    public List<Chauffeur> chauffeursDisponibles() {
        return chauffeurs.tousLesElements().stream()
                .filter(Chauffeur::estDisponible)
                .collect(Collectors.toList());
    }

    // ════════════════════════════════════════════════════════════════
    // MISSIONS — CRUD + AFFECTATION
    // ════════════════════════════════════════════════════════════════

    /**
     * Génère un ID unique de la forme M0001, M0002...
     * CORRECTIF : le compteur est synchronisé après chargement CSV.
     */
    public String genererIdMission() {
        return "M" + String.format("%04d", compteurMission++);
    }

    public void ajouterMission(Mission m) {
        missions.ajouter(m);
    }

    public boolean supprimerMission(String id) {
        Optional<Mission> m = missions.trouverParId(id);
        if (m.isPresent() && m.get().getStatut() == Mission.Statut.EN_COURS) {
            throw new IllegalStateException("Impossible de supprimer une mission en cours.");
        }
        return missions.supprimer(id);
    }

    public Optional<Mission> trouverMission(String id) {
        return missions.trouverParId(id);
    }

    public List<Mission> toutesMissions() {
        return missions.tousLesElements();
    }

    /**
     * Affecte un véhicule ET un chauffeur à une mission.
     * Lance des exceptions métier si l'un ou l'autre est indisponible.
     */
    public void affecterMission(String missionId, String vehiculeId, String chauffeurId) {
        Mission  m = missions.trouverParId(missionId)
                .orElseThrow(() -> new NoSuchElementException("Mission introuvable : " + missionId));
        Vehicule v = vehicules.trouverParId(vehiculeId)
                .orElseThrow(() -> new NoSuchElementException("Véhicule introuvable : " + vehiculeId));
        Chauffeur c = chauffeurs.trouverParId(chauffeurId)
                .orElseThrow(() -> new NoSuchElementException("Chauffeur introuvable : " + chauffeurId));

        if (m.getVehiculeAssigneId() != null)
            throw new MissionDejaAffecteeException(missionId);

        v.assigner(missionId);   // lance VehiculeIndisponibleException si occupé
        c.assigner(missionId);   // lance ChauffeurIndisponibleException si occupé
        m.setVehiculeAssigneId(vehiculeId);
        m.setChauffeurAssigneId(chauffeurId);
        m.setStatut(Mission.Statut.EN_COURS);
    }

    /**
     * Termine une mission : met à jour le kilométrage et libère les ressources.
     */
    public void terminerMission(String missionId, double kmParcourus, String rapport) {
        Mission m = missions.trouverParId(missionId)
                .orElseThrow(() -> new NoSuchElementException("Mission introuvable : " + missionId));

        if (m.getVehiculeAssigneId() != null) {
            vehicules.trouverParId(m.getVehiculeAssigneId()).ifPresent(v -> {
                v.setKilometrage(v.getKilometrage() + kmParcourus);
                v.liberer();
            });
        }
        if (m.getChauffeurAssigneId() != null) {
            chauffeurs.trouverParId(m.getChauffeurAssigneId()).ifPresent(Chauffeur::liberer);
        }
        m.setStatut(Mission.Statut.TERMINEE);
        if (rapport != null && !rapport.isBlank()) m.setRapport(rapport);
    }

    public List<Mission> rechercherMissions(Mission.Statut statut, String depart, String destination) {
        return missions.tousLesElements().stream()
                .filter(m -> statut      == null || m.getStatut() == statut)
                .filter(m -> depart      == null || depart.isBlank()      || m.getDepart().toLowerCase().contains(depart.toLowerCase()))
                .filter(m -> destination == null || destination.isBlank() || m.getDestination().toLowerCase().contains(destination.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Mission> trierMissions(String colonne, boolean croissant) {
        Comparator<Mission> comp = switch (colonne) {
            case "depart"      -> Comparator.comparing(Mission::getDepart);
            case "destination" -> Comparator.comparing(Mission::getDestination);
            case "distance"    -> Comparator.comparingDouble(Mission::getDistanceKm);
            case "cout"        -> Comparator.comparingDouble(Mission::calculerCout);
            case "statut"      -> Comparator.comparing(m -> m.getStatut().name());
            default            -> Comparator.comparing(Mission::getId);
        };
        if (!croissant) comp = comp.reversed();
        return missions.tousLesElements().stream()
                .sorted(comp)
                .collect(Collectors.toList());
    }

    // ════════════════════════════════════════════════════════════════
    // STATISTIQUES (Streams : map, count, average, sum, groupingBy)
    // ════════════════════════════════════════════════════════════════

    public int    getNombreVehicules()            { return vehicules.taille(); }
    public int    getNombreChauffeurs()           { return chauffeurs.taille(); }
    public int    getNombreMissions()             { return missions.taille(); }

    public long getNombreVehiculesDisponibles() {
        return vehicules.tousLesElements().stream()
                .filter(Vehicule::estDisponible).count();
    }

    public long getNombreChauffeursDisponibles() {
        return chauffeurs.tousLesElements().stream()
                .filter(Chauffeur::estDisponible).count();
    }

    public double getKilometrageTotalFlotte() {
        return vehicules.tousLesElements().stream()
                .mapToDouble(Vehicule::getKilometrage).sum();
    }

    public double getKilometrageMoyenVehicule() {
        return vehicules.tousLesElements().stream()
                .mapToDouble(Vehicule::getKilometrage)
                .average().orElse(0.0);
    }

    public double getCoutTotalMissions() {
        return missions.tousLesElements().stream()
                .mapToDouble(Mission::calculerCout).sum();
    }

    public double getCoutMoyenMission() {
        return missions.tousLesElements().stream()
                .mapToDouble(Mission::calculerCout)
                .average().orElse(0.0);
    }

    public double getCoutTotalMaintenance() {
        return vehicules.tousLesElements().stream()
                .mapToDouble(Vehicule::getCoutTotalMaintenance).sum();
    }

    public long getMissionsParStatut(Mission.Statut statut) {
        return missions.tousLesElements().stream()
                .filter(m -> m.getStatut() == statut).count();
    }

    /** Répartition des véhicules par type — Map<type, count>. */
    public Map<String, Long> getRepartitionParType() {
        return vehicules.tousLesElements().stream()
                .collect(Collectors.groupingBy(Vehicule::getTypeVehicule, Collectors.counting()));
    }

    public double getDistanceTotaleMissions() {
        return missions.tousLesElements().stream()
                .mapToDouble(Mission::getDistanceKm).sum();
    }

    // ════════════════════════════════════════════════════════════════
    // PERSISTANCE CSV
    // ════════════════════════════════════════════════════════════════

    public void sauvegarder() throws IOException {
        new java.io.File("resources").mkdirs();
        CsvUtil.sauvegarderVehicules(vehicules.tousLesElements(),   CSV_VEHICULES);
        CsvUtil.sauvegarderChauffeurs(chauffeurs.tousLesElements(), CSV_CHAUFFEURS);
        CsvUtil.sauvegarderMissions(missions.tousLesElements(),     CSV_MISSIONS);
    }

    /**
     * Charge les données depuis les CSV.
     * CORRECTIF : synchronise le compteurMission après le chargement
     * pour éviter de générer des IDs déjà existants.
     */
    public void charger() throws IOException {
        CsvUtil.chargerVehicules(CSV_VEHICULES).forEach(vehicules::ajouter);
        CsvUtil.chargerChauffeurs(CSV_CHAUFFEURS).forEach(chauffeurs::ajouter);
        CsvUtil.chargerMissions(CSV_MISSIONS).forEach(missions::ajouter);
        synchroniserCompteurMission();
    }

    /**
     * Calcule le prochain numéro de mission disponible en analysant
     * les IDs existants (ex. "M0003" → compteur repart à 4).
     */
    private void synchroniserCompteurMission() {
        int max = missions.tousLesElements().stream()
                .map(Mission::getId)
                .filter(id -> id.matches("M\\d+"))
                .mapToInt(id -> Integer.parseInt(id.substring(1)))
                .max()
                .orElse(0);
        this.compteurMission = max + 1;
    }

    // ════════════════════════════════════════════════════════════════
    // DONNÉES DE DÉMONSTRATION
    // ════════════════════════════════════════════════════════════════

    public void chargerDonneesDemo() {
        ajouterVehicule(new VehiculeLeger("AA-001-BB",  "Renault",  "Clio",    2021, 5));
        ajouterVehicule(new VehiculeLeger("BB-002-CC",  "Peugeot",  "208",     2020, 5));
        ajouterVehicule(new VehiculeLourd("CC-003-DD",  "Mercedes", "Actros",  2019, 20.0));
        ajouterVehicule(new VehiculeLourd("DD-004-EE",  "Volvo",    "FH16",    2022, 24.0));
        ajouterVehicule(new VehiculeSpecial("EE-005-FF","Ford",     "Transit", 2021, "Ambulance"));
        ajouterVehicule(new VehiculeSpecial("FF-006-GG","Iveco",    "Daily",   2020, "Benne"));

        vehicules.trouverParId("AA-001-BB").ifPresent(v -> v.chargerKilometrage(45000));
        vehicules.trouverParId("BB-002-CC").ifPresent(v -> v.chargerKilometrage(32000));
        vehicules.trouverParId("CC-003-DD").ifPresent(v -> v.chargerKilometrage(120000));
        vehicules.trouverParId("DD-004-EE").ifPresent(v -> v.chargerKilometrage(87000));

        ajouterChauffeur(new Chauffeur("C001", "Dupont",  "Jean",    "P-123456", "B"));
        ajouterChauffeur(new Chauffeur("C002", "Martin",  "Sophie",  "P-234567", "B"));
        ajouterChauffeur(new Chauffeur("C003", "Bernard", "Michel",  "P-345678", "CE"));
        ajouterChauffeur(new Chauffeur("C004", "Leroy",   "Claire",  "P-456789", "C"));
        ajouterChauffeur(new Chauffeur("C005", "Moreau",  "Antoine", "P-567890", "CE"));

        ajouterMission(new MissionCourte(genererIdMission(), "Paris",    "Lyon",       LocalDateTime.now().minusDays(2), 465));
        ajouterMission(new MissionCourte(genererIdMission(), "Lyon",     "Marseille",  LocalDateTime.now().minusDays(1), 315));
        ajouterMission(new MissionLongue(genererIdMission(), "Paris",    "Bordeaux",   LocalDateTime.now(),              580, 45.0));
        ajouterMission(new MissionLongue(genererIdMission(), "Bordeaux", "Strasbourg", LocalDateTime.now().plusDays(1),  850, 80.0));
        ajouterMission(new MissionCourte(genererIdMission(), "Paris",    "Rouen",      LocalDateTime.now().plusDays(2),  135));
    }
}
