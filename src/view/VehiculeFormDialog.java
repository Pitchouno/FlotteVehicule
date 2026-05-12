package view;

import controller.FlotteController;
import model.abstracts.Vehicule;
import model.impl.VehiculeLeger;
import model.impl.VehiculeLourd;
import model.impl.VehiculeSpecial;

import javax.swing.*;
import java.awt.*;

/** Formulaire modal pour créer ou modifier un véhicule. */
public class VehiculeFormDialog extends JDialog {

    private final FlotteController controller;
    private final Vehicule vehiculeExistant; // null = création

    private final JTextField txtImmat      = new JTextField(12);
    private final JTextField txtMarque     = new JTextField(12);
    private final JTextField txtModele     = new JTextField(12);
    private final JSpinner   spnAnnee      = new JSpinner(new SpinnerNumberModel(2020, 1990, 2030, 1));
    private final JComboBox<String> cboType = new JComboBox<>(new String[]{"Leger", "Lourd", "Special"});
    private final JTextField txtExtra      = new JTextField(12); // passagers / charge / spécialité

    public VehiculeFormDialog(Frame parent, FlotteController controller, Vehicule vehicule) {
        super(parent, vehicule == null ? "Ajouter un véhicule" : "Modifier le véhicule", true);
        this.controller       = controller;
        this.vehiculeExistant = vehicule;

        setLayout(new BorderLayout(8, 8));
        add(construireFormulaire(), BorderLayout.CENTER);
        add(construireBoutons(),    BorderLayout.SOUTH);

        if (vehicule != null) prefillForm(vehicule);

        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private JPanel construireFormulaire() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(12, 16, 4, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        String[][] champs = {
                {"Immatriculation *", "immat"},
                {"Marque *",          "marque"},
                {"Modèle *",          "modele"},
                {"Année *",           "annee"},
                {"Type *",            "type"},
                {"Info spécifique",   "extra"},
        };

        boolean creation = vehiculeExistant == null;
        txtImmat.setEditable(creation); // on ne peut pas changer l'immat

        int row = 0;
        for (String[] ch : champs) {
            gbc.gridx = 0; gbc.gridy = row;
            p.add(new JLabel(ch[0] + " :"), gbc);
            gbc.gridx = 1;
            Component comp = switch (ch[1]) {
                case "annee" -> spnAnnee;
                case "type"  -> cboType;
                default      -> switch (ch[1]) {
                    case "immat"  -> txtImmat;
                    case "marque" -> txtMarque;
                    case "modele" -> txtModele;
                    default       -> txtExtra;
                };
            };
            p.add(comp, gbc);
            row++;
        }

        // Label contextuel pour le champ extra
        cboType.addActionListener(e -> mettreAJourLabelExtra(p));
        mettreAJourLabelExtra(p);

        return p;
    }

    private void mettreAJourLabelExtra(JPanel p) {
        // Mise à jour du placeholder
        String type = (String) cboType.getSelectedItem();
        txtExtra.setToolTipText(switch (type) {
            case "Leger"   -> "Nombre de passagers (ex : 5)";
            case "Lourd"   -> "Charge max en tonnes (ex : 20.0)";
            case "Special" -> "Spécialité (ex : Ambulance)";
            default        -> "";
        });
    }

    private JPanel construireBoutons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave   = new JButton(vehiculeExistant == null ? "Ajouter" : "Enregistrer");
        JButton btnCancel = new JButton("Annuler");
        btnSave.addActionListener(e -> sauvegarder());
        btnCancel.addActionListener(e -> dispose());
        p.add(btnCancel);
        p.add(btnSave);
        return p;
    }

    private void prefillForm(Vehicule v) {
        txtImmat.setText(v.getImmatriculation());
        txtMarque.setText(v.getMarque());
        txtModele.setText(v.getModele());
        spnAnnee.setValue(v.getAnnee());
        cboType.setSelectedItem(v.getTypeVehicule());
        if (v instanceof VehiculeLeger vl)  txtExtra.setText(String.valueOf(vl.getNombrePassagers()));
        if (v instanceof VehiculeLourd vl)  txtExtra.setText(String.valueOf(vl.getChargeMaxTonnes()));
        if (v instanceof VehiculeSpecial vs) txtExtra.setText(vs.getSpecialite());
    }

    private void sauvegarder() {
        String immat  = txtImmat.getText().trim();
        String marque = txtMarque.getText().trim();
        String modele = txtModele.getText().trim();
        int    annee  = (int) spnAnnee.getValue();
        String type   = (String) cboType.getSelectedItem();
        String extra  = txtExtra.getText().trim();

        if (immat.isEmpty() || marque.isEmpty() || modele.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs obligatoires (*).",
                    "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (vehiculeExistant == null) {
                // Création
                Vehicule v = switch (type) {
                    case "Leger"   -> new VehiculeLeger(immat, marque, modele, annee,
                                            extra.isEmpty() ? 5 : Integer.parseInt(extra));
                    case "Lourd"   -> new VehiculeLourd(immat, marque, modele, annee,
                                            extra.isEmpty() ? 20.0 : Double.parseDouble(extra));
                    case "Special" -> new VehiculeSpecial(immat, marque, modele, annee,
                                            extra.isEmpty() ? "Spécial" : extra);
                    default        -> throw new IllegalArgumentException("Type inconnu");
                };
                controller.ajouterVehicule(v);
            } else {
                // Modification (on ne peut pas changer le type ni l'immat)
                vehiculeExistant.setMarque(marque);
                vehiculeExistant.setModele(modele);
                vehiculeExistant.setAnnee(annee);
                controller.modifierVehicule(vehiculeExistant);
            }
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valeur numérique invalide dans le champ 'Info spécifique'.",
                    "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
