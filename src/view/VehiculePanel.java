package view;

import controller.FlotteController;
import model.abstracts.Vehicule;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/** Vue principale pour la gestion des véhicules. */
public class VehiculePanel extends JPanel {

    private final FlotteController controller;
    private final VehiculeTableModel tableModel;
    private final JTable table;
    private final TableRowSorter<VehiculeTableModel> sorter;

    // Filtres
    private final JTextField txtRecherche = new JTextField(15);
    private final JComboBox<String> cboType = new JComboBox<>(new String[]{"Tous", "Leger", "Lourd", "Special"});
    private final JCheckBox chkDispo = new JCheckBox("Disponibles seulement");

    public VehiculePanel(FlotteController controller) {
        this.controller = controller;
        this.tableModel = new VehiculeTableModel(controller.tousVehicules());
        this.table      = new JTable(tableModel);
        this.sorter     = new TableRowSorter<>(tableModel);

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(construireBarreFiltres(), BorderLayout.NORTH);
        add(construireTableau(),      BorderLayout.CENTER);
        add(construireBoutons(),      BorderLayout.SOUTH);
    }

    // ── Construction des composants ───────────────────────────────────────

    private JPanel construireBarreFiltres() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        p.setBorder(BorderFactory.createTitledBorder("Filtres"));
        p.add(new JLabel("Recherche :"));
        p.add(txtRecherche);
        p.add(new JLabel("Type :"));
        p.add(cboType);
        p.add(chkDispo);
        JButton btnFiltrer = new JButton("Filtrer");
        btnFiltrer.addActionListener(e -> appliquerFiltres());
        JButton btnReset = new JButton("Réinitialiser");
        btnReset.addActionListener(e -> reinitialiserFiltres());
        p.add(btnFiltrer);
        p.add(btnReset);
        return p;
    }

    private JScrollPane construireTableau() {
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        // Largeurs de colonnes
        table.getColumnModel().getColumn(0).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        return new JScrollPane(table);
    }

    private JPanel construireBoutons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnAjouter   = new JButton("➕ Ajouter");
        JButton btnModifier  = new JButton("✏ Modifier");
        JButton btnSupprimer = new JButton("🗑 Supprimer");
        JButton btnDetail    = new JButton("📋 Détail");

        btnAjouter.addActionListener(e -> ouvrirFormulaireAjout());
        btnModifier.addActionListener(e -> ouvrirFormulaireModification());
        btnSupprimer.addActionListener(e -> supprimerSelection());
        btnDetail.addActionListener(e -> afficherDetail());

        p.add(btnAjouter);
        p.add(btnModifier);
        p.add(btnSupprimer);
        p.add(btnDetail);

        // Compteur
        JLabel lblCount = new JLabel();
        actualiserCompteur(lblCount);
        p.add(Box.createHorizontalStrut(20));
        p.add(lblCount);

        return p;
    }

    // ── Actions (délèguent toutes au controller) ──────────────────────────

    private void appliquerFiltres() {
        String  marque    = txtRecherche.getText().trim();
        String  type      = cboType.getSelectedIndex() == 0 ? null : (String) cboType.getSelectedItem();
        Boolean disponible = chkDispo.isSelected() ? true : null;

        List<Vehicule> resultats = controller.rechercherVehicules(type, disponible, marque);
        tableModel.setVehicules(resultats);
    }

    private void reinitialiserFiltres() {
        txtRecherche.setText("");
        cboType.setSelectedIndex(0);
        chkDispo.setSelected(false);
        tableModel.setVehicules(controller.tousVehicules());
    }

    private void ouvrirFormulaireAjout() {
        VehiculeFormDialog dialog = new VehiculeFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), controller, null);
        dialog.setVisible(true);
        rafraichir();
    }

    private void ouvrirFormulaireModification() {
        Vehicule selection = getVehiculeSelectionne();
        if (selection == null) { afficherErreur("Sélectionnez un véhicule à modifier."); return; }
        VehiculeFormDialog dialog = new VehiculeFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), controller, selection);
        dialog.setVisible(true);
        rafraichir();
    }

    private void supprimerSelection() {
        Vehicule selection = getVehiculeSelectionne();
        if (selection == null) { afficherErreur("Sélectionnez un véhicule à supprimer."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer " + selection.getImmatriculation() + " ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.supprimerVehicule(selection.getImmatriculation());
                rafraichir();
            } catch (Exception ex) {
                afficherErreur(ex.getMessage());
            }
        }
    }

    private void afficherDetail() {
        Vehicule selection = getVehiculeSelectionne();
        if (selection == null) { afficherErreur("Sélectionnez un véhicule."); return; }
        String msg = String.format("""
                Immatriculation : %s
                Marque / Modèle : %s %s
                Année           : %d
                Type            : %s
                Kilométrage     : %.0f km
                Disponible      : %s
                Prochain entretien : %s
                Coût maintenance : %.2f €
                Entretiens : %s
                """,
                selection.getImmatriculation(), selection.getMarque(), selection.getModele(),
                selection.getAnnee(), selection.getTypeVehicule(), selection.getKilometrage(),
                selection.estDisponible() ? "Oui" : "Non (" + selection.getMissionEnCoursId() + ")",
                selection.getProchainEntretien(),
                selection.getCoutTotalMaintenance(),
                selection.getHistoriqueEntretiens().isEmpty() ? "Aucun" : String.join(", ", selection.getHistoriqueEntretiens())
        );
        JOptionPane.showMessageDialog(this, msg, "Détail du véhicule", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Utilitaires ───────────────────────────────────────────────────────

    private Vehicule getVehiculeSelectionne() {
        int row = table.getSelectedRow();
        if (row == -1) return null;
        int modelRow = table.convertRowIndexToModel(row);
        return tableModel.getVehiculeAt(modelRow);
    }

    public void rafraichir() {
        tableModel.setVehicules(controller.tousVehicules());
    }

    private void actualiserCompteur(JLabel lbl) {
        lbl.setText(controller.getNombreVehicules() + " véhicule(s)");
    }

    private void afficherErreur(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
