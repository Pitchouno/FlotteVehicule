package view;

import controller.FlotteController;
import model.abstracts.Mission;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Map;

/**
 * Vue statistiques — affiche tous les indicateurs calculés avec les Streams.
 * CORRECTIF : couleurs via UIManager.getColor() pour compatibilité light/dark mode.
 */
public class StatsPanel extends JPanel {

    private final FlotteController controller;
    private JPanel panelStats;

    public StatsPanel(FlotteController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JButton btnActualiser = new JButton("🔄 Actualiser");
        btnActualiser.addActionListener(e -> actualiser());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Tableau de bord — statistiques en temps réel"));
        top.add(btnActualiser);
        add(top, BorderLayout.NORTH);

        panelStats = new JPanel();
        panelStats.setLayout(new BoxLayout(panelStats, BoxLayout.Y_AXIS));
        add(new JScrollPane(panelStats), BorderLayout.CENTER);

        actualiser();
    }

    public void actualiser() {
        panelStats.removeAll();

        // ── Parc véhicules ────────────────────────────────────────────
        panelStats.add(sectionTitre("Parc de véhicules"));
        panelStats.add(stat("Véhicules total",                 String.valueOf(controller.getNombreVehicules())));
        panelStats.add(stat("Véhicules disponibles",           String.valueOf(controller.getNombreVehiculesDisponibles())));
        panelStats.add(stat("Véhicules en mission",            String.valueOf(controller.getNombreVehicules() - controller.getNombreVehiculesDisponibles())));
        panelStats.add(stat("Véhicules en maintenance urgente", String.valueOf(controller.vehiculesMaintenanceUrgente().size())));
        panelStats.add(stat("Kilométrage total flotte",        String.format("%.0f km", controller.getKilometrageTotalFlotte())));
        panelStats.add(stat("Kilométrage moyen / véhicule",    String.format("%.0f km", controller.getKilometrageMoyenVehicule())));
        panelStats.add(stat("Coût total maintenance",          String.format("%.2f €",  controller.getCoutTotalMaintenance())));

        Map<String, Long> repartition = controller.getRepartitionParType();
        repartition.forEach((type, nb) -> panelStats.add(stat("  → " + type, nb + " véhicule(s)")));

        // ── Chauffeurs ────────────────────────────────────────────────
        panelStats.add(sectionTitre("Chauffeurs"));
        panelStats.add(stat("Chauffeurs total",       String.valueOf(controller.getNombreChauffeurs())));
        panelStats.add(stat("Chauffeurs disponibles", String.valueOf(controller.getNombreChauffeursDisponibles())));
        panelStats.add(stat("Chauffeurs en mission",  String.valueOf(controller.getNombreChauffeurs() - controller.getNombreChauffeursDisponibles())));

        // ── Missions ──────────────────────────────────────────────────
        panelStats.add(sectionTitre("Missions"));
        panelStats.add(stat("Missions total",     String.valueOf(controller.getNombreMissions())));
        panelStats.add(stat("En attente",         String.valueOf(controller.getMissionsParStatut(Mission.Statut.EN_ATTENTE))));
        panelStats.add(stat("En cours",           String.valueOf(controller.getMissionsParStatut(Mission.Statut.EN_COURS))));
        panelStats.add(stat("Terminées",          String.valueOf(controller.getMissionsParStatut(Mission.Statut.TERMINEE))));
        panelStats.add(stat("Annulées",           String.valueOf(controller.getMissionsParStatut(Mission.Statut.ANNULEE))));
        panelStats.add(stat("Distance totale",    String.format("%.0f km",  controller.getDistanceTotaleMissions())));
        panelStats.add(stat("Coût total missions", String.format("%.2f €",  controller.getCoutTotalMissions())));
        panelStats.add(stat("Coût moyen / mission", String.format("%.2f €", controller.getCoutMoyenMission())));

        panelStats.revalidate();
        panelStats.repaint();
    }

    private JPanel sectionTitre(String titre) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        // CORRECTIF : couleur d'accent via UIManager (compatible light/dark)
        Color accent = UIManager.getColor("Component.focusColor");
        if (accent == null) accent = UIManager.getColor("Button.focus");
        if (accent == null) accent = new Color(70, 130, 180);
        p.setBorder(new MatteBorder(0, 0, 2, 0, accent));
        JLabel lbl = new JLabel(titre);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        lbl.setForeground(accent);
        p.add(lbl);
        return p;
    }

    private JPanel stat(String libelle, String valeur) {
        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        p.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        JLabel lblLib = new JLabel(libelle);
        JLabel lblVal = new JLabel(valeur);
        lblVal.setFont(lblVal.getFont().deriveFont(Font.BOLD));
        lblVal.setHorizontalAlignment(SwingConstants.RIGHT);
        p.add(lblLib, BorderLayout.WEST);
        p.add(lblVal, BorderLayout.EAST);
        return p;
    }
}
