package view;

import controller.FlotteController;
import model.abstracts.Mission;
import model.impl.MissionCourte;
import model.impl.MissionLongue;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDateTime;

public class MissionPanel extends JPanel {

    private final FlotteController controller;
    private final MissionTableModel tableModel;
    private final JTable table;

    private final JComboBox<String> cboStatut = new JComboBox<>(
            new String[]{"Tous", "EN_ATTENTE", "EN_COURS", "TERMINEE", "ANNULEE"});
    private final JTextField txtDepart = new JTextField(10);
    private final JTextField txtDest   = new JTextField(10);

    public MissionPanel(FlotteController controller) {
        this.controller = controller;
        this.tableModel = new MissionTableModel(controller.toutesMissions());
        this.table      = new JTable(tableModel);

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(construireBarreFiltres(), BorderLayout.NORTH);
        add(new JScrollPane(table),   BorderLayout.CENTER);
        add(construireBoutons(),      BorderLayout.SOUTH);

        table.setRowSorter(new TableRowSorter<>(tableModel));
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JPanel construireBarreFiltres() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        p.setBorder(BorderFactory.createTitledBorder("Filtres"));
        p.add(new JLabel("Statut :")); p.add(cboStatut);
        p.add(new JLabel("Départ :"));  p.add(txtDepart);
        p.add(new JLabel("Arrivée :")); p.add(txtDest);
        JButton btn = new JButton("Filtrer");
        btn.addActionListener(e -> {
            Mission.Statut statut = cboStatut.getSelectedIndex() == 0 ? null
                    : Mission.Statut.valueOf((String) cboStatut.getSelectedItem());
            tableModel.setMissions(controller.rechercherMissions(statut, txtDepart.getText().trim(), txtDest.getText().trim()));
        });
        JButton reset = new JButton("Réinitialiser");
        reset.addActionListener(e -> { cboStatut.setSelectedIndex(0); txtDepart.setText(""); txtDest.setText(""); rafraichir(); });
        p.add(btn); p.add(reset);
        return p;
    }

    private JPanel construireBoutons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnNouvelle = new JButton("➕ Nouvelle mission");
        JButton btnAffecter = new JButton("🔗 Affecter");
        JButton btnTerminer = new JButton("✔ Terminer");
        JButton btnSuppr    = new JButton("🗑 Supprimer");

        btnNouvelle.addActionListener(e -> creerMission());
        btnAffecter.addActionListener(e -> affecterMission());
        btnTerminer.addActionListener(e -> terminerMission());
        btnSuppr.addActionListener(e -> supprimerMission());

        p.add(btnNouvelle); p.add(btnAffecter); p.add(btnTerminer); p.add(btnSuppr);
        return p;
    }

    private void creerMission() {
        // Formulaire simplifié en boîte de dialogue
        JTextField txtDep   = new JTextField(10);
        JTextField txtDst   = new JTextField(10);
        JTextField txtDist  = new JTextField(6);
        JTextField txtPeage = new JTextField(6);
        JComboBox<String> cboType = new JComboBox<>(new String[]{"Courte", "Longue"});

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 6));
        form.add(new JLabel("Départ :"));      form.add(txtDep);
        form.add(new JLabel("Destination :")); form.add(txtDst);
        form.add(new JLabel("Distance (km) :")); form.add(txtDist);
        form.add(new JLabel("Type :"));        form.add(cboType);
        form.add(new JLabel("Péage (€) (si Longue) :")); form.add(txtPeage);

        int ok = JOptionPane.showConfirmDialog(this, form, "Nouvelle mission", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            String dep  = txtDep.getText().trim();
            String dst  = txtDst.getText().trim();
            double dist = Double.parseDouble(txtDist.getText().trim());
            String id   = controller.genererIdMission();

            Mission m = "Longue".equals(cboType.getSelectedItem())
                    ? new MissionLongue(id, dep, dst, LocalDateTime.now(), dist,
                                        txtPeage.getText().isBlank() ? 0 : Double.parseDouble(txtPeage.getText().trim()))
                    : new MissionCourte(id, dep, dst, LocalDateTime.now(), dist);

            controller.ajouterMission(m);
            rafraichir();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Distance ou péage invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void affecterMission() {
        Mission m = getMissionSelectionnee();
        if (m == null) { JOptionPane.showMessageDialog(this, "Sélectionnez une mission."); return; }
        if (m.getStatut() != Mission.Statut.EN_ATTENTE) {
            JOptionPane.showMessageDialog(this, "Seules les missions EN_ATTENTE peuvent être affectées.");
            return;
        }

        // Listes déroulantes des disponibles
        java.util.List<model.impl.Chauffeur> chauffeursDispo = controller.chauffeursDisponibles();
        java.util.List<model.abstracts.Vehicule> vehiculesDispo = controller.rechercherVehicules(null, true, null);

        if (vehiculesDispo.isEmpty() || chauffeursDispo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun véhicule ou chauffeur disponible.");
            return;
        }

        JComboBox<String> cboVeh  = new JComboBox<>(vehiculesDispo.stream().map(v -> v.getImmatriculation() + " — " + v.getMarque()).toArray(String[]::new));
        JComboBox<String> cboCh   = new JComboBox<>(chauffeursDispo.stream().map(model.impl.Chauffeur::getNomComplet).toArray(String[]::new));

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 6));
        form.add(new JLabel("Véhicule :")); form.add(cboVeh);
        form.add(new JLabel("Chauffeur :")); form.add(cboCh);

        int ok = JOptionPane.showConfirmDialog(this, form, "Affecter la mission " + m.getId(), JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            String vid = vehiculesDispo.get(cboVeh.getSelectedIndex()).getImmatriculation();
            String cid = chauffeursDispo.get(cboCh.getSelectedIndex()).getId();
            controller.affecterMission(m.getId(), vid, cid);
            rafraichir();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void terminerMission() {
        Mission m = getMissionSelectionnee();
        if (m == null) { JOptionPane.showMessageDialog(this, "Sélectionnez une mission."); return; }
        if (m.getStatut() != Mission.Statut.EN_COURS) {
            JOptionPane.showMessageDialog(this, "Seules les missions EN_COURS peuvent être terminées.");
            return;
        }
        JTextField txtKm     = new JTextField("0");
        JTextField txtRapport = new JTextField(20);
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 6));
        form.add(new JLabel("Km parcourus :")); form.add(txtKm);
        form.add(new JLabel("Rapport :")); form.add(txtRapport);
        int ok = JOptionPane.showConfirmDialog(this, form, "Terminer la mission " + m.getId(), JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        try {
            controller.terminerMission(m.getId(), Double.parseDouble(txtKm.getText().trim()), txtRapport.getText());
            rafraichir();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerMission() {
        Mission m = getMissionSelectionnee();
        if (m == null) { JOptionPane.showMessageDialog(this, "Sélectionnez une mission."); return; }
        int ok = JOptionPane.showConfirmDialog(this, "Supprimer la mission " + m.getId() + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try { controller.supprimerMission(m.getId()); rafraichir(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE); }
        }
    }

    private Mission getMissionSelectionnee() {
        int row = table.getSelectedRow();
        if (row == -1) return null;
        return tableModel.getMissionAt(table.convertRowIndexToModel(row));
    }

    public void rafraichir() {
        tableModel.setMissions(controller.toutesMissions());
    }
}
