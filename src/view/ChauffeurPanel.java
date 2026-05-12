package view;

import controller.FlotteController;
import model.impl.Chauffeur;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class ChauffeurPanel extends JPanel {

    private final FlotteController controller;
    private final ChauffeurTableModel tableModel;
    private final JTable table;

    private final JTextField txtRecherche = new JTextField(15);
    private final JCheckBox  chkDispo     = new JCheckBox("Disponibles seulement");
    private final JComboBox<String> cboPermis = new JComboBox<>(new String[]{"Tous", "B", "C", "CE"});

    public ChauffeurPanel(FlotteController controller) {
        this.controller = controller;
        this.tableModel = new ChauffeurTableModel(controller.tousChauffeurs());
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
        p.add(new JLabel("Nom :"));
        p.add(txtRecherche);
        p.add(new JLabel("Permis :"));
        p.add(cboPermis);
        p.add(chkDispo);
        JButton btn = new JButton("Filtrer");
        btn.addActionListener(e -> appliquerFiltres());
        JButton reset = new JButton("Réinitialiser");
        reset.addActionListener(e -> { txtRecherche.setText(""); cboPermis.setSelectedIndex(0); chkDispo.setSelected(false); rafraichir(); });
        p.add(btn); p.add(reset);
        return p;
    }

    private JPanel construireBoutons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnAjouter   = new JButton("➕ Ajouter");
        JButton btnModifier  = new JButton("✏ Modifier");
        JButton btnSupprimer = new JButton("🗑 Supprimer");

        btnAjouter.addActionListener(e -> ouvrirFormulaireAjout());
        btnModifier.addActionListener(e -> ouvrirFormulaireModification());
        btnSupprimer.addActionListener(e -> supprimerSelection());

        p.add(btnAjouter); p.add(btnModifier); p.add(btnSupprimer);
        return p;
    }

    private void appliquerFiltres() {
        String  nom       = txtRecherche.getText().trim();
        String  permis    = cboPermis.getSelectedIndex() == 0 ? null : (String) cboPermis.getSelectedItem();
        Boolean disponible = chkDispo.isSelected() ? true : null;
        tableModel.setChauffeurs(controller.rechercherChauffeurs(nom, disponible, permis));
    }

    private void ouvrirFormulaireAjout() {
        ChauffeurFormDialog d = new ChauffeurFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), controller, null);
        d.setVisible(true);
        rafraichir();
    }

    private void ouvrirFormulaireModification() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Sélectionnez un chauffeur."); return; }
        Chauffeur c = tableModel.getChauffeurAt(table.convertRowIndexToModel(row));
        ChauffeurFormDialog d = new ChauffeurFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), controller, c);
        d.setVisible(true);
        rafraichir();
    }

    private void supprimerSelection() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Sélectionnez un chauffeur."); return; }
        Chauffeur c = tableModel.getChauffeurAt(table.convertRowIndexToModel(row));
        int ok = JOptionPane.showConfirmDialog(this, "Supprimer " + c.getNomComplet() + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try { controller.supprimerChauffeur(c.getId()); rafraichir(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE); }
        }
    }

    public void rafraichir() {
        tableModel.setChauffeurs(controller.tousChauffeurs());
    }
}
