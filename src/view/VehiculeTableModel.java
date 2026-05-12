package view;

import model.abstracts.Vehicule;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/** TableModel personnalisé pour afficher les véhicules dans une JTable. */
public class VehiculeTableModel extends AbstractTableModel {

    private static final String[] COLONNES = {
            "Immatriculation", "Marque", "Modèle", "Année", "Type", "Kilométrage", "Disponible", "Prochain entretien"
    };

    private List<Vehicule> vehicules;

    public VehiculeTableModel(List<Vehicule> vehicules) {
        this.vehicules = vehicules;
    }

    public void setVehicules(List<Vehicule> vehicules) {
        this.vehicules = vehicules;
        fireTableDataChanged();
    }

    public Vehicule getVehiculeAt(int row) {
        return vehicules.get(row);
    }

    @Override public int getRowCount()    { return vehicules.size(); }
    @Override public int getColumnCount() { return COLONNES.length; }
    @Override public String getColumnName(int col) { return COLONNES[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Vehicule v = vehicules.get(row);
        return switch (col) {
            case 0 -> v.getImmatriculation();
            case 1 -> v.getMarque();
            case 2 -> v.getModele();
            case 3 -> v.getAnnee();
            case 4 -> v.getTypeVehicule();
            case 5 -> String.format("%.0f km", v.getKilometrage());
            case 6 -> v.estDisponible() ? "✓ Oui" : "✗ Non";
            case 7 -> v.getProchainEntretien() != null ? v.getProchainEntretien().toString() : "—";
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return col == 3 ? Integer.class : String.class;
    }
}
