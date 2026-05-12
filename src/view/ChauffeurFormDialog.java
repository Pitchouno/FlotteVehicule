package view;

import controller.FlotteController;
import model.impl.Chauffeur;

import javax.swing.*;
import java.awt.*;

public class ChauffeurFormDialog extends JDialog {

    private final FlotteController controller;
    private final Chauffeur chauffeurExistant;

    private final JTextField txtId      = new JTextField(10);
    private final JTextField txtNom     = new JTextField(12);
    private final JTextField txtPrenom  = new JTextField(12);
    private final JTextField txtPermis  = new JTextField(12);
    private final JComboBox<String> cboCategorie = new JComboBox<>(new String[]{"B", "C", "CE", "D"});

    public ChauffeurFormDialog(Frame parent, FlotteController controller, Chauffeur chauffeur) {
        super(parent, chauffeur == null ? "Ajouter un chauffeur" : "Modifier le chauffeur", true);
        this.controller        = controller;
        this.chauffeurExistant = chauffeur;

        setLayout(new BorderLayout(8, 8));
        add(construireFormulaire(), BorderLayout.CENTER);
        add(construireBoutons(),    BorderLayout.SOUTH);

        if (chauffeur != null) {
            txtId.setText(chauffeur.getId()); txtId.setEditable(false);
            txtNom.setText(chauffeur.getNom());
            txtPrenom.setText(chauffeur.getPrenom());
            txtPermis.setText(chauffeur.getNumeroPermis());
            cboCategorie.setSelectedItem(chauffeur.getCategoriePermis());
        }
        pack();
        setLocationRelativeTo(parent);
    }

    private JPanel construireFormulaire() {
        JPanel p = new JPanel(new GridLayout(5, 2, 8, 6));
        p.setBorder(BorderFactory.createEmptyBorder(12, 16, 4, 16));
        p.add(new JLabel("ID *:")); p.add(txtId);
        p.add(new JLabel("Nom *:")); p.add(txtNom);
        p.add(new JLabel("Prénom *:")); p.add(txtPrenom);
        p.add(new JLabel("N° Permis *:")); p.add(txtPermis);
        p.add(new JLabel("Catégorie *:")); p.add(cboCategorie);
        return p;
    }

    private JPanel construireBoutons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave   = new JButton(chauffeurExistant == null ? "Ajouter" : "Enregistrer");
        JButton btnCancel = new JButton("Annuler");
        btnSave.addActionListener(e -> sauvegarder());
        btnCancel.addActionListener(e -> dispose());
        p.add(btnCancel); p.add(btnSave);
        return p;
    }

    private void sauvegarder() {
        String id       = txtId.getText().trim();
        String nom      = txtNom.getText().trim();
        String prenom   = txtPrenom.getText().trim();
        String permis   = txtPermis.getText().trim();
        String categorie = (String) cboCategorie.getSelectedItem();

        if (id.isEmpty() || nom.isEmpty() || prenom.isEmpty() || permis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs * sont obligatoires.", "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (chauffeurExistant == null) {
                controller.ajouterChauffeur(new Chauffeur(id, nom, prenom, permis, categorie));
            } else {
                chauffeurExistant.setNom(nom);
                chauffeurExistant.setPrenom(prenom);
                chauffeurExistant.setNumeroPermis(permis);
                chauffeurExistant.setCategoriePermis(categorie);
                controller.modifierChauffeur(chauffeurExistant);
            }
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
