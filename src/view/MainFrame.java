package view;

import controller.FlotteController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/** Fenêtre principale de l'application. Contient les 3 vues en onglets. */
public class MainFrame extends JFrame {

    private final FlotteController controller;
    private final VehiculePanel  vehiculePanel;
    private final ChauffeurPanel chauffeurPanel;
    private final MissionPanel   missionPanel;
    private final StatsPanel     statsPanel;
    private final JLabel         lblStatus;

    public MainFrame(FlotteController controller) {
        this.controller     = controller;
        this.vehiculePanel  = new VehiculePanel(controller);
        this.chauffeurPanel = new ChauffeurPanel(controller);
        this.missionPanel   = new MissionPanel(controller);
        this.statsPanel     = new StatsPanel(controller);
        this.lblStatus      = new JLabel(" Prêt");

        construireFenetre();
        chargerDonnees();
    }

    private void construireFenetre() {
        setTitle("Gestionnaire de Flotte Automobile — ESIEE-IT");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 550));
        setLocationRelativeTo(null);

        // Menu
        setJMenuBar(construireMenu());

        // Onglets principaux
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("🚗 Véhicules",   vehiculePanel);
        tabs.addTab("👤 Chauffeurs",  chauffeurPanel);
        tabs.addTab("📋 Missions",    missionPanel);
        tabs.addTab("📊 Statistiques", statsPanel);

        // Mise à jour des stats quand on change d'onglet
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == statsPanel) statsPanel.actualiser();
        });

        add(tabs, BorderLayout.CENTER);

        // Barre de statut
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.add(lblStatus, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);

        // Confirmation à la fermeture
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quitter();
            }
        });
    }

    private JMenuBar construireMenu() {
        JMenuBar bar = new JMenuBar();

        // Menu Fichier
        JMenu menuFichier = new JMenu("Fichier");
        JMenuItem itemSauvegarder = new JMenuItem("💾 Sauvegarder");
        JMenuItem itemCharger     = new JMenuItem("📂 Charger");
        JMenuItem itemQuitter     = new JMenuItem("Quitter");
        itemSauvegarder.addActionListener(e -> sauvegarder());
        itemCharger.addActionListener(e -> chargerDonnees());
        itemQuitter.addActionListener(e -> quitter());
        menuFichier.add(itemSauvegarder);
        menuFichier.add(itemCharger);
        menuFichier.addSeparator();
        menuFichier.add(itemQuitter);

        // Menu Données
        JMenu menuDonnees = new JMenu("Données");
        JMenuItem itemDemo = new JMenuItem("Charger données de démo");
        itemDemo.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                    "Charger les données de démo ? (efface les données actuelles)",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                controller.chargerDonneesDemo();
                rafraichirTout();
                setStatus("Données de démo chargées.");
            }
        });
        menuDonnees.add(itemDemo);

        // Menu Aide
        JMenu menuAide = new JMenu("?");
        JMenuItem itemAPropos = new JMenuItem("À propos");
        itemAPropos.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Gestionnaire de Flotte Automobile\nProjet POO Avancée — ESIEE-IT\nSwing + Java 17",
                "À propos", JOptionPane.INFORMATION_MESSAGE));
        menuAide.add(itemAPropos);

        bar.add(menuFichier);
        bar.add(menuDonnees);
        bar.add(menuAide);
        return bar;
    }

    private void sauvegarder() {
        try {
            controller.sauvegarder();
            setStatus("Données sauvegardées avec succès.");
            JOptionPane.showMessageDialog(this, "Sauvegarde réussie.", "Sauvegarde", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde : " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chargerDonnees() {
        try {
            controller.charger();
            rafraichirTout();
            setStatus("Données chargées.");
        } catch (IOException ex) {
            setStatus("Aucune donnée sauvegardée trouvée — démarrage vide.");
        }
    }

    private void quitter() {
        int rep = JOptionPane.showConfirmDialog(this,
                "Sauvegarder avant de quitter ?", "Quitter",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (rep == JOptionPane.YES_OPTION) {
            sauvegarder();
            System.exit(0);
        } else if (rep == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
        // CANCEL → on ne quitte pas
    }

    private void rafraichirTout() {
        SwingUtilities.invokeLater(() -> {
            vehiculePanel.rafraichir();
            chauffeurPanel.rafraichir();
            missionPanel.rafraichir();
            statsPanel.actualiser();
        });
    }

    private void setStatus(String msg) {
        lblStatus.setText(" " + msg);
    }
}
